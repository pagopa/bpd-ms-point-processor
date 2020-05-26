package it.gov.pagopa.bpd.point_processor.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sia.meda.eventlistener.BaseEventListener;
import it.gov.pagopa.bpd.point_processor.command.ProcessTransactionCommand;
import it.gov.pagopa.bpd.point_processor.command.model.ProcessTransactionCommandModel;
import it.gov.pagopa.bpd.point_processor.command.model.Transaction;
import it.gov.pagopa.bpd.point_processor.factory.ModelFactory;
import it.gov.pagopa.bpd.point_processor.service.PointProcessorErrorPublisherService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Class Extending the {@link BaseEventListener}, manages the inbound requests, and calls on the appropriate
 * command for the processing logic, associated to the {@link Transaction} payload
 */

@Service
@Slf4j
public class OnTransactionProcessRequestListener extends BaseEventListener {

    private final ModelFactory<Pair<byte[], Headers>, ProcessTransactionCommandModel>
            processTransactionCommandModelModelFactory;
    private final BeanFactory beanFactory;
    private final PointProcessorErrorPublisherService pointProcessorErrorPublisherService;
    private final ObjectMapper objectMapper;

    @Autowired
    public OnTransactionProcessRequestListener(
            ModelFactory<Pair<byte[], Headers>,ProcessTransactionCommandModel> processTransactionCommandModelModelFactory,
            BeanFactory beanFactory,
            PointProcessorErrorPublisherService pointProcessorErrorPublisherService,
            ObjectMapper objectMapper) {
        this.processTransactionCommandModelModelFactory = processTransactionCommandModelModelFactory;
        this.beanFactory = beanFactory;
        this.pointProcessorErrorPublisherService = pointProcessorErrorPublisherService;
        this.objectMapper = objectMapper;
    }

    /**
     * Method called on receiving a message in the inbound queue,
     * that should contain a JSON payload containing transaction data,
     * calls on a command to execute the check and send logic for the input {@link Transaction} data
     * In case of error, sends data to an error channel
     * @param payload
     *          Message JSON payload in byte[] format
     * @param headers
     *          Kafka headers from the inbound message
     */

    @SneakyThrows
    @Override
    public void onReceived(byte[] payload, Headers headers) {

        ProcessTransactionCommandModel processTransactionCommandModel = null;

        try {

            if (logger.isDebugEnabled()) {
                logger.debug("Processing new request on inbound queue");
            }

            processTransactionCommandModel = processTransactionCommandModelModelFactory
                    .createModel(Pair.of(payload, headers));
            ProcessTransactionCommand command = beanFactory.getBean(
                    ProcessTransactionCommand.class, processTransactionCommandModel);

            if (!command.execute()) {
                logger.debug("Failed to execute ProcessTransactionCommand");
            } else {
                logger.debug("ProcessTransactionCommand successfully executed for inbound message");
            }

        } catch (Exception e) {

            String payloadString = "null";
            String error = "Unexpected error during transaction processing";

            try {
                payloadString = new String(payload, StandardCharsets.UTF_8);
            } catch (Exception e2) {
                if (logger.isErrorEnabled()) {
                    logger.error("Something gone wrong converting the payload into String", e2);
                }
            }

            if (processTransactionCommandModel != null && processTransactionCommandModel.getPayload() != null) {
                payloadString = new String(payload, StandardCharsets.UTF_8);
                error = String.format("Unexpected error during transaction processing: %s, %s",
                        payloadString, e.getMessage());
            } else if (payload != null) {
                error = String.format("Something gone wrong during the evaluation of the payload: %s, %s",
                        payloadString, e.getMessage());
                if (logger.isErrorEnabled()) {
                    logger.error(error, e);
                }
            }

            if (!pointProcessorErrorPublisherService.publishErrorEvent(payload, headers, error)) {
                if (log.isErrorEnabled()) {
                    log.error("Could not publish transaction processing error");
                }
                throw e;
            }

        }
    }

}
