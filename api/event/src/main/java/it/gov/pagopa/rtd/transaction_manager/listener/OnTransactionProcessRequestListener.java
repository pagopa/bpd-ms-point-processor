package it.gov.pagopa.rtd.transaction_manager.listener;

import eu.sia.meda.eventlistener.BaseEventListener;
import it.gov.pagopa.bpd.point_processor.command.ProcessTransactionCommand;
import it.gov.pagopa.bpd.point_processor.command.model.ProcessTransactionCommandModel;
import it.gov.pagopa.rtd.transaction_manager.factory.ModelFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Class Extending the MEDA BaseEventListener, manages the inbound requests, and calls on the appropriate
 * command for the check and send logic associated to the Transaction payload
 */

@Service
@Slf4j
public class OnTransactionProcessRequestListener extends BaseEventListener {

    private final ModelFactory<Pair<byte[], Headers>, ProcessTransactionCommandModel>
            processTransactionCommandModelModelFactory;
    private final BeanFactory beanFactory;

    @Autowired
    public OnTransactionProcessRequestListener(
            ModelFactory<Pair<byte[], Headers>,ProcessTransactionCommandModel> processTransactionCommandModelModelFactory,
            BeanFactory beanFactory) {
        this.processTransactionCommandModelModelFactory = processTransactionCommandModelModelFactory;
        this.beanFactory = beanFactory;
    }

    /**
     * Method called on receiving a message in the inbound queue,
     * that should contain a JSON payload containing transaction data,
     * calls on a command to execute the check and send logic for the input Transaction data
     * In case of error, sends data to an error channel
     * @param payload
     *          Message JSON payload in byte[] format
     * @param headers
     *          Kafka headers from the inbound message
     */

    @SneakyThrows
    @Override
    public void onReceived(byte[] payload, Headers headers) {

        try {

            if (logger.isDebugEnabled()) {
                logger.debug("Processing new request on inbound queue");
            }

            ProcessTransactionCommandModel processTransactionCommandModel =
                    processTransactionCommandModelModelFactory.createModel(Pair.of(payload, headers));
            ProcessTransactionCommand command = beanFactory.getBean(
                    ProcessTransactionCommand.class, processTransactionCommandModel);

            if (!command.execute()) {
                throw new Exception("Failed to execute ProcessTransactionCommand");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("ProcessTransactionCommand successfully executed for inbound message");
            }

        } catch (Exception e) {
            //TODO: Gestione casi d'errori per acknowledgment
            String payloadString = "null";
            if (payload != null) {
                try {
                    payloadString = new String(payload, StandardCharsets.UTF_8);
                } catch (Exception e2) {
                    logger.error("Something gone wrong converting the payload into String", e2);
                }
                logger.error(String.format(
                        "Something gone wrong during the evaluation of the payload:%n%s", payloadString), e);
            }
            throw e;
        }
    }

}
