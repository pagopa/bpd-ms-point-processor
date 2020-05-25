package it.gov.pagopa.bpd.point_processor.connector.winning_transaction;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import it.gov.pagopa.bpd.common.connector.BaseFeignRestClientTest;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.config.WinningTransactionRestConnectorConfig;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.WinningTransaction;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.TestPropertySourceUtils;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@TestPropertySource(
        locations = "classpath:config/winning_transaction/rest-client.properties",
        properties = "spring.application.name=bpd-ms-point-processor-integration-rest")
@ContextConfiguration(initializers = WinningTransactionRestClientTest.RandomPortInitializer.class,
        classes = WinningTransactionRestConnectorConfig.class)
public class WinningTransactionRestClientTest extends BaseFeignRestClientTest {

    @ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(wireMockConfig()
            .dynamicPort()
            .usingFilesUnderClasspath("stubs/winning-transaction")
    );

    @Test
    public void saveWinningTransaction_Ok() {
        WinningTransaction winningTransaction = restClient.saveWinningTransaction(getSaveModel());

        Assert.assertNotNull(winningTransaction);
    }

    @Autowired
    private WinningTransactionRestClient restClient;

    public static class RandomPortInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @SneakyThrows
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertySourceUtils
                    .addInlinedPropertiesToEnvironment(applicationContext,
                            String.format("rest-client.winning-transaction.base-url=http://%s:%d/bpd/winning-transactions",
                                    wireMockRule.getOptions().bindAddress(),
                                    wireMockRule.port())
                    );
        }
    }

    protected WinningTransaction getSaveModel() {
        return WinningTransaction.builder()
                .idTrxAcquirer(1)
                .acquirerCode("001")
                .trxDate(OffsetDateTime.parse("2020-04-09T16:22:45.304Z"))
                .amount(BigDecimal.valueOf(1313.13))
                .operationType("00")
                .hpan("hpan")
                .merchantId(0)
                .circuitType("00")
                .mcc("813")
                .idTrxIssuer(0)
                .amountCurrency("833")
                .correlationId(1)
                .acquirerId(0)
                .awardPeriodId(1L)
                .score(BigDecimal.ONE)
                .build();
    }

}