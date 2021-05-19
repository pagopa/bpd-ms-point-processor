package it.gov.pagopa.bpd.point_processor.connector.award_period;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import it.gov.pagopa.bpd.common.connector.BaseFeignRestClientTest;
import it.gov.pagopa.bpd.point_processor.connector.award_period.config.AwardPeriodRestConnectorConfig;
import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.test.util.AopTestUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@TestPropertySource(
        locations = "classpath:config/award_period/rest-client.properties",
        properties = "spring.application.name=bpd-ms-point-processor-integration-rest")
@ContextConfiguration(initializers = AwardPeriodRestClientTest.RandomPortInitializer.class,
        classes = {
                AwardPeriodRestConnectorConfig.class,
                CacheAutoConfiguration.class,
                AwardPeriodCacheServiceImpl.class
        })
public class AwardPeriodRestClientTest extends BaseFeignRestClientTest {

    @ClassRule
    public static WireMockClassRule wireMockRule;

    static {
        String port = System.getenv("WIREMOCKPORT");
        wireMockRule = new WireMockClassRule(wireMockConfig()
                .port(port != null ? Integer.parseInt(port) : 0)
                .bindAddress("localhost")
                .usingFilesUnderClasspath("stubs/award-period")
        );
    }

    public static class RandomPortInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @SneakyThrows
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertySourceUtils
                    .addInlinedPropertiesToEnvironment(applicationContext,
                            String.format("rest-client.award-period.base-url=http://%s:%d/bpd/award-periods",
                                    wireMockRule.getOptions().bindAddress(),
                                    wireMockRule.port())
                    );
        }
    }

    @SpyBean
    private AwardPeriodRestClient restClient;

    @SpyBean
    private CacheManager cacheManager;

    @Autowired
    private AwardPeriodCacheServiceImpl awardPeriodCacheServiceImpl;

    private AwardPeriodRestClient restClientSpy;


    @PostConstruct
    void init() {
        restClientSpy = AopTestUtils.getTargetObject(restClient);
    }


    @Before
    public void setup() {
        Mockito.reset(restClientSpy);
        clearCacheAwardPeriods();
    }

    private void clearCacheAwardPeriods() {
        final Cache awardPeriodsCache = cacheManager.getCache("awardPeriods");
        if (awardPeriodsCache != null) {
            awardPeriodsCache.clear();
        }
    }

    @Test
    public void getAwardPeriods_Ok_NotEmpty() {
        final List<AwardPeriod> actualResponse = restClient.getActiveAwardPeriods();

        Assert.assertNotNull(actualResponse);
        Assert.assertFalse(actualResponse.isEmpty());
    }

    @Test
    public void getAwardPeriods_cacheMiss_notPresent() {
        Assert.assertFalse(getCachedAwardPeriods().isPresent());

        final List<AwardPeriod> actualResponse = restClient.getActiveAwardPeriods();

        Assert.assertNotNull(actualResponse);
        Assert.assertFalse(actualResponse.isEmpty());
        BDDMockito.verify(restClientSpy, Mockito.times(1))
                .getActiveAwardPeriods();
    }

    @SuppressWarnings("unchecked")
    private Optional<List<AwardPeriod>> getCachedAwardPeriods() {
        return Optional.ofNullable(cacheManager.getCache("awardPeriods")).map(c -> c.get("getActiveAwardPeriods", List.class));
    }

    @Test
    public void getAwardPeriods_cacheMiss_evicted() {
        Assert.assertFalse(getCachedAwardPeriods().isPresent());

        restClient.getActiveAwardPeriods();

        Assert.assertTrue(getCachedAwardPeriods().isPresent());

        awardPeriodCacheServiceImpl.awardPeriodsCacheEvict();

        Assert.assertFalse(getCachedAwardPeriods().isPresent());

        restClient.getActiveAwardPeriods();

        BDDMockito.verify(restClientSpy, Mockito.times(2))
                .getActiveAwardPeriods();
    }

    @Test
    public void getAwardPeriods_cacheHit() {
        Assert.assertFalse(getCachedAwardPeriods().isPresent());

        final List<AwardPeriod> actualResponse1 = restClient.getActiveAwardPeriods();
        Assert.assertNotNull(actualResponse1);

        final Optional<List<AwardPeriod>> cachedAwardPeriods = getCachedAwardPeriods();
        Assert.assertNotNull(cachedAwardPeriods);
        Assert.assertTrue(cachedAwardPeriods.isPresent());

        final List<AwardPeriod> actualResponse2 = restClient.getActiveAwardPeriods();
        Assert.assertNotNull(actualResponse2);

        Assert.assertArrayEquals(actualResponse1.toArray(), actualResponse2.toArray());
        Assert.assertArrayEquals(actualResponse1.toArray(), cachedAwardPeriods.get().toArray());

        BDDMockito.verify(restClientSpy, Mockito.times(1))
                .getActiveAwardPeriods();
    }

}