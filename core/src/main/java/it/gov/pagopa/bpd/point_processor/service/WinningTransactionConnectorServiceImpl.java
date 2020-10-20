package it.gov.pagopa.bpd.point_processor.service;

import eu.sia.meda.event.transformer.SimpleEventRequestTransformer;
import eu.sia.meda.event.transformer.SimpleEventResponseTransformer;
import it.gov.pagopa.bpd.point_processor.publisher.SaveTransactionPublisherConnector;
import it.gov.pagopa.bpd.point_processor.publisher.model.WinningTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link WinningTransactionConnectorService}
 */

@Service
@Slf4j
class WinningTransactionConnectorServiceImpl implements WinningTransactionConnectorService {

    private final SaveTransactionPublisherConnector saveTransactionPublisherConnector;
    private final SimpleEventRequestTransformer<WinningTransaction> simpleEventRequestTransformer;
    private final SimpleEventResponseTransformer simpleEventResponseTransformer;


    @Autowired
    public WinningTransactionConnectorServiceImpl(
            SaveTransactionPublisherConnector saveTransactionPublisherConnector,
            SimpleEventRequestTransformer<WinningTransaction> simpleEventRequestTransformer,
            SimpleEventResponseTransformer simpleEventResponseTransformer) {
        this.saveTransactionPublisherConnector = saveTransactionPublisherConnector;
        this.simpleEventRequestTransformer = simpleEventRequestTransformer;
        this.simpleEventResponseTransformer = simpleEventResponseTransformer;
    }

    @Override
    public void saveWinningTransaction(WinningTransaction winningTransaction) {
        saveTransactionPublisherConnector.doCall(winningTransaction,
                simpleEventRequestTransformer, simpleEventResponseTransformer);
    }
}
