package it.gov.pagopa.bpd.point_processor.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sia.meda.connector.jpa.JPAConnectorImpl;
import eu.sia.meda.event.service.ErrorPublisherService;
import eu.sia.meda.eventlistener.BaseEventListenerIntegrationTest;
import it.gov.pagopa.bpd.point_processor.MCC_CategoryDAO;
import it.gov.pagopa.bpd.point_processor.command.model.Transaction;
import it.gov.pagopa.bpd.point_processor.config.TestConfig;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.WinningTransaction;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.enums.OperationType;
import it.gov.pagopa.bpd.point_processor.factory.ProcessTransactionCommandModelFactory;
import it.gov.pagopa.bpd.point_processor.model.entity.MCC_Category;
import it.gov.pagopa.bpd.point_processor.service.AwardPeriodConnectorService;
import it.gov.pagopa.bpd.point_processor.service.PointProcessorErrorPublisherService;
import it.gov.pagopa.bpd.point_processor.service.ScoreMultiplierService;
import it.gov.pagopa.bpd.point_processor.service.WinningTransactionConnectorService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.configuration.ObjectPostProcessorConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Integration Testing class for the whole micro-service, it executes the entire flow starting from the
 * inbound event listener, to the outbound call to the REST service
 */

@AutoConfigureWireMock
@EnableConfigurationProperties
@EnableJpaRepositories(
        repositoryBaseClass = JPAConnectorImpl.class,
        basePackages = {"it.gov.pagopa.bpd"}
)
@ContextConfiguration(classes = {
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
                "classpath:config/testPointProcessorErrorPublisher.properties",
                "classpath:config/AwardPeriodRestConnector.properties",
                "classpath:config/WinningTransactionRestConnector.properties"
        },
        properties = {
                "connectors.jpaConfigurations.connection.mocked:true",
                "connectors.jpaConfigurations.connection.path:postgres/",
                "spring.main.allow-bean-definition-overriding=true",
                "listeners.eventConfigurations.items.OnTransactionProcessRequestListener.bootstrapServers=${spring.embedded.kafka.brokers}",
                "connectors.eventConfigurations.items.PointProcessorErrorPublisherConnector.bootstrapServers=${spring.embedded.kafka.brokers}",
                "connectors.medaInternalConfigurations.items.AwardPeriodRestConnector.mocked=true",
                "connectors.medaInternalConfigurations.items.AwardPeriodRestConnector.randomMock=false",
                "connectors.medaInternalConfigurations.items.AwardPeriodRestConnector.path=award-periods/findAll",
                "connectors.medaInternalConfigurations.items.WinningTransactionRestConnector.mocked=true",
                "connectors.medaInternalConfigurations.items.WinningTransactionRestConnector.randomMock=false",
                "connectors.medaInternalConfigurations.items.WinningTransactionRestConnector.path=winning-transactions/save",
                "point-processor.mcc-score-multiplier.0000=0.10",

        })
public class OnTransactionProcessRequestListenerIntegrationTest extends BaseEventListenerIntegrationTest {

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

//    @SpyBean
//    AwardPeriodRestClient awardPeriodRestClientSpy;
//
//    @SpyBean
//    WinningTransactionRestClient winningTransactionRestClient;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    MCC_CategoryDAO mcc_categoryDAOMock;

    @Before
    public void setUp() {
        MCC_Category mcc_category = new MCC_Category();
        mcc_category.setMccCategoryId("0");
        mcc_category.setMultiplierScore(BigDecimal.valueOf(0.10));
        mcc_category.setMccCategoryDescription("test");
        BDDMockito.doReturn(mcc_category).when(mcc_categoryDAOMock)
                .findByMerchantCategoryCodes_Mcc(Mockito.eq("0000"));
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

    protected Object getSentData() {
        return WinningTransaction.builder()
                .idTrxAcquirer("1")
                .acquirerCode("001")
                .trxDate(OffsetDateTime.parse("2020-04-10T16:59:59.245+02:00"))
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

            Transaction sentTransaction = (Transaction) getRequestObject();
            WinningTransaction savedTransaction = (WinningTransaction) getSentData();
            BDDMockito.verify(awardPeriodConnectorServiceSpy, Mockito.atLeastOnce())
                    .getAwardPeriod(Mockito.eq(LocalDate.now()));
//            BDDMockito.verify(awardPeriodRestClientSpy, Mockito.atLeastOnce()).getAwardPeriods();
            BDDMockito.verify(scoreMultiplierService, Mockito.atLeastOnce())
                    .getScoreMultiplier(Mockito.eq(sentTransaction.getMcc()));
//            BDDMockito.verify(winningTransactionRestClient, Mockito.atLeastOnce())
//                    .saveWinningTransaction(Mockito.any());
            BDDMockito.verify(winningTransactionConnectorServiceSpy, Mockito.atLeastOnce())
                    .saveWinningTransaction(Mockito.eq(savedTransaction));
            BDDMockito.verifyZeroInteractions(pointProcessorErrorPublisherServiceSpy);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

}