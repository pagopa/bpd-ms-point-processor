package it.gov.pagopa.bpd.point_processor.service;

import eu.sia.meda.event.transformer.SimpleEventRequestTransformer;
import eu.sia.meda.event.transformer.SimpleEventResponseTransformer;
import it.gov.pagopa.bpd.point_processor.publisher.SaveTransactionPublisherConnector;
import it.gov.pagopa.bpd.point_processor.publisher.model.WinningTransaction;
import it.gov.pagopa.bpd.point_processor.service.transformer.HeaderAwareRequestTransformer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link WinningTransactionConnectorService}
 */

@Service
@Slf4j
class WinningTransactionConnectorServiceImpl implements WinningTransactionConnectorService {

    private final SaveTransactionPublisherConnector saveTransactionPublisherConnector;
    private final HeaderAwareRequestTransformer<WinningTransaction> simpleEventRequestTransformer;
    private final SimpleEventResponseTransformer simpleEventResponseTransformer;


    @Autowired
    public WinningTransactionConnectorServiceImpl(
            SaveTransactionPublisherConnector saveTransactionPublisherConnector,
            HeaderAwareRequestTransformer<WinningTransaction> simpleEventRequestTransformer,
            SimpleEventResponseTransformer simpleEventResponseTransformer) {
        this.saveTransactionPublisherConnector = saveTransactionPublisherConnector;
        this.simpleEventRequestTransformer = simpleEventRequestTransformer;
        this.simpleEventResponseTransformer = simpleEventResponseTransformer;
    }

    @Override
    public void saveWinningTransaction(WinningTransaction winningTransaction, Header statusUpdateHeader) {
        RecordHeaders recordHeaders = new RecordHeaders();
        if (statusUpdateHeader != null && statusUpdateHeader.value() != null) {
            recordHeaders.add("CITIZEN_VALIDATION_DATETIME", statusUpdateHeader.value());
        }
        saveTransactionPublisherConnector.doCall(winningTransaction,
                simpleEventRequestTransformer, simpleEventResponseTransformer, recordHeaders);
    }
}
