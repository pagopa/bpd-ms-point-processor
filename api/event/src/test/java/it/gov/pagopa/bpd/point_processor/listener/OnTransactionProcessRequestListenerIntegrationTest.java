package it.gov.pagopa.bpd.point_processor.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sia.meda.event.service.ErrorPublisherService;
import eu.sia.meda.eventlistener.BaseEventListenerIntegrationTest;
import it.gov.pagopa.bpd.point_processor.command.model.Transaction;
import it.gov.pagopa.bpd.point_processor.factory.ProcessTransactionCommandModelFactory;
import it.gov.pagopa.bpd.point_processor.config.TestConfig;
import it.gov.pagopa.bpd.point_processor.service.AwardPeriodConnectorService;
import it.gov.pagopa.bpd.point_processor.service.PointProcessorErrorPublisherService;
import it.gov.pagopa.bpd.point_processor.service.ScoreMultiplierService;
import it.gov.pagopa.bpd.point_processor.service.WinningTransactionConnectorService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Assert;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.SpyBean;
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

@EnableConfigurationProperties
@ContextConfiguration(classes = {
        TestConfig.class,
        RestTemplateAutoConfiguration.class,
        JacksonAutoConfiguration.class,
        ObjectPostProcessorConfiguration.class,
        AuthenticationConfiguration.class,
        KafkaAutoConfiguration.class
})
@TestPropertySource(
        locations = {
                "classpath:config/testTransactionRequestListener.properties",
                "classpath:config/testPointProcessorErrorPublisher.properties",
                "classpath:config/AwardPeriodRestConnector.properties",
                "classpath:config/WinningTransactionRestConnector.properties"
        },
        properties = {
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

    @Autowired
    ObjectMapper objectMapper;

    @Override
    protected Object getRequestObject() {
         return Transaction.builder()
                .idTrxAcquirer(1)
                .acquirerCode("001")
                .trxDate(OffsetDateTime.parse("2020-04-10T14:59:59.245Z"))
                .amount(BigDecimal.valueOf(1313.13))
                .operationType("00")
                .hpan("test")
                .merchantId(0)
                .circuitType("00")
                .mcc("0000")
                .idTrxIssuer(0)
                .amountCurrency("833")
                .correlationId(1)
                .acquirerId(0)
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
            BDDMockito.verify(awardPeriodConnectorServiceSpy, Mockito.atLeastOnce())
                    .getAwardPeriod(Mockito.eq(LocalDate.now()));
            BDDMockito.verify(scoreMultiplierService, Mockito.atLeastOnce())
                    .getScoreMultiplier(Mockito.eq(sentTransaction.getMcc()));
            BDDMockito.verify(winningTransactionConnectorServiceSpy, Mockito.atMost(1))
                    .saveWinningTransaction(Mockito.any());
            BDDMockito.verifyZeroInteractions(pointProcessorErrorPublisherServiceSpy);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

}