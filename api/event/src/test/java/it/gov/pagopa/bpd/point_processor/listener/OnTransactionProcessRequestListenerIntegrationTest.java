package it.gov.pagopa.bpd.point_processor.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import eu.sia.meda.event.service.ErrorPublisherService;
import eu.sia.meda.eventlistener.BaseEventListenerIntegrationTest;
import it.gov.pagopa.bpd.point_processor.command.model.Transaction;
import it.gov.pagopa.bpd.point_processor.config.TestConfig;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.WinningTransaction;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.enums.OperationType;
import it.gov.pagopa.bpd.point_processor.factory.ProcessTransactionCommandModelFactory;
import it.gov.pagopa.bpd.point_processor.service.AwardPeriodConnectorService;
import it.gov.pagopa.bpd.point_processor.service.PointProcessorErrorPublisherService;
import it.gov.pagopa.bpd.point_processor.service.ScoreMultiplierService;
import it.gov.pagopa.bpd.point_processor.service.WinningTransactionConnectorService;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.configuration.ObjectPostProcessorConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.TestPropertySourceUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * Integration Testing class for the whole micro-service, it executes the entire flow starting from the
 * inbound event listener, to the outbound call to the REST service
 */

@EnableConfigurationProperties
@ContextConfiguration(initializers = OnTransactionProcessRequestListenerIntegrationTest.RandomPortInitializer.class,
        classes = {
                TestConfig.class,
                RestTemplateAutoConfiguration.class,
                JacksonAutoConfiguration.class,
                ObjectPostProcessorConfiguration.class,
                AuthenticationConfiguration.class,
                KafkaAutoConfiguration.class,
                FeignAutoConfiguration.class
        })
@TestPropertySource(
        locations = {
                "classpath:config/testTransactionRequestListener.properties",
                "classpath:config/testPointProcessorErrorPublisher.properties"
        },
        properties = {
                "logging.level.it.gov.pagopa.bpd.point_processor=DEBUG",
                "spring.main.allow-bean-definition-overriding=true",
                "listeners.eventConfigurations.items.OnTransactionProcessRequestListener.bootstrapServers=${spring.embedded.kafka.brokers}",
                "connectors.eventConfigurations.items.PointProcessorErrorPublisherConnector.bootstrapServers=${spring.embedded.kafka.brokers}",
                "point-processor.mcc-score-multiplier.0000=0.10",

        })
public class OnTransactionProcessRequestListenerIntegrationTest extends BaseEventListenerIntegrationTest {

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

    @Override
    protected Object getRequestObject() {
        return Transaction.builder()
                .idTrxAcquirer("1")
                .acquirerCode("001")
                .trxDate(OffsetDateTime.parse("2020-04-10T14:59:59.245Z"))
                .amount(BigDecimal.valueOf(100))
                .operationType("00")
                .hpan("test")
                .merchantId("0")
                .circuitType("00")
                .mcc("0000")
                .idTrxIssuer("0")
                .amountCurrency("833")
                .correlationId("1")
                .acquirerId("0")
                .build();
    }

    @Value("${listeners.eventConfigurations.items.OnTransactionProcessRequestListener.topic}")
    private String topicSubscription;

    @Override
    protected ErrorPublisherService getErrorPublisherService() {
        return null;
    }

    @SpyBean
    PointProcessorErrorPublisherService pointProcessorErrorPublisherServiceSpy;

    @SpyBean
    WinningTransactionConnectorService winningTransactionConnectorServiceSpy;

    @SpyBean
    AwardPeriodConnectorService awardPeriodConnectorServiceSpy;

    @SpyBean
    ScoreMultiplierService scoreMultiplierService;

    @SpyBean
    ProcessTransactionCommandModelFactory processTransactionCommandModelFactory;


    @Autowired
    ObjectMapper objectMapper;


    @Before
    public void setUp() {}

    public static class RandomPortInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @SneakyThrows
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertySourceUtils
                    .addInlinedPropertiesToEnvironment(applicationContext,
                            String.format("rest-client.award-period.base-url=http://%s:%d/bpd/award-periods",
                                    wireMockRule.getOptions().bindAddress(),
                                    wireMockRule.port()),
                            String.format("rest-client.winning-transaction.base-url=http://%s:%d/bpd/winning-transactions",
                                    wireMockRule.getOptions().bindAddress(),
                                    wireMockRule.port())
                    );
        }
    }

    protected Object getSentData() {
        return WinningTransaction.builder()
                .idTrxAcquirer("1")
                .acquirerCode("001")
                .trxDate(OffsetDateTime.parse("2020-04-10T16:59:59.245Z"))
                .amount(BigDecimal.valueOf(100))
                .operationType(OperationType.PAGAMENTO)
                .hpan("test")
                .merchantId("0")
                .circuitType("00")
                .mcc("0000")
                .idTrxIssuer("0")
                .amountCurrency("833")
                .correlationId("1")
                .acquirerId("0")
                .awardPeriodId(1L)
                .score(BigDecimal.valueOf(10D))
                .build();
    }


    @Override
    protected String getTopicSubscription() {
        return topicSubscription;
    }

    @Override
    protected String getTopicPublished() {
        return "test";
    }


    @Override
    protected void verifyPublishedMessages(List<ConsumerRecord<String, String>> records) {

        try {

            BDDMockito.verify(awardPeriodConnectorServiceSpy, Mockito.atLeastOnce())
                    .getAwardPeriod(Mockito.eq(LocalDate.now()));
            BDDMockito.verify(scoreMultiplierService, Mockito.atLeastOnce())
                    .getScoreMultiplier();
            BDDMockito.verify(winningTransactionConnectorServiceSpy, Mockito.atLeastOnce())
                    .saveWinningTransaction(Mockito.any());
            BDDMockito.verifyZeroInteractions(pointProcessorErrorPublisherServiceSpy);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Override
    protected Duration getTimeout() {
        return Duration.ofMillis(10000L);
    }

}