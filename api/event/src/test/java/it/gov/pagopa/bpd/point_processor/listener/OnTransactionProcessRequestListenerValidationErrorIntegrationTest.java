package it.gov.pagopa.bpd.point_processor.listener;

import eu.sia.meda.event.service.ErrorPublisherService;
import it.gov.pagopa.bpd.point_processor.command.model.Transaction;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Integration Testing class for the whole micro-service, it executes the error flow starting from the
 * inbound event listener, to the production of a message in the outbound error channel
 */

@TestPropertySource(
        properties = {
                "connectors.eventConfigurations.items.PointProcessorErrorPublisherConnector.topic=bpd-trx-validation-error"
        }
)
public class OnTransactionProcessRequestListenerValidationErrorIntegrationTest
        extends OnTransactionProcessRequestListenerIntegrationTest {

    @Value("${connectors.eventConfigurations.items.PointProcessorErrorPublisherConnector.topic}")
    private String topicPublished;

    @Override
    protected Object getRequestObject() {
        return Transaction.builder()
                .acquirerCode("001")
                .trxDate(OffsetDateTime.parse("2020-04-10T14:59:59.245Z"))
                .amount(BigDecimal.valueOf(1313.13))
                .operationType("00")
                .hpan("test")
                .merchantId("0")
                .circuitType("00")
                .mcc("813")
                .idTrxIssuer("0")
                .amountCurrency("833")
                .correlationId("1")
                .acquirerId("0")
                .build();
    }

    @Override
    protected String getTopicPublished() {
        return topicPublished;
    }

    @SneakyThrows
    @Override
    protected void verifyPublishedMessages(List<ConsumerRecord<String, String>> records) {
        Assert.assertEquals(1,records.size());
        Transaction sentTransaction = (Transaction) getRequestObject();
        sentTransaction.setTrxDate(OffsetDateTime.parse("2020-04-10T16:59:59.245+02:00"));
        Transaction publishedTransaction = objectMapper.readValue(records.get(0).value(), Transaction.class);
        Assert.assertEquals(sentTransaction, publishedTransaction);
    }

    @Override
    protected ErrorPublisherService getErrorPublisherService() {
        return null;
    }

}