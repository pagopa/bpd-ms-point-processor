package it.gov.pagopa.bpd.point_processor.service;

import it.gov.pagopa.bpd.point_processor.publisher.model.WinningTransaction;
import it.gov.pagopa.bpd.point_processor.publisher.SaveTransactionPublisherConnector;

/**
 * Service for managing {@link WinningTransaction} data
 * @see WinningTransactionConnectorServiceImpl
 */

public interface WinningTransactionConnectorService {

    /**
     * Implementation of {@link WinningTransactionConnectorServiceImpl#saveWinningTransaction(WinningTransaction)},
     * that contacts the endpoint managed with {@link SaveTransactionPublisherConnector},
     * for saving a {@link WinningTransaction}
     * @param winningTransaction
     *              {@link WinningTransaction} instance to save
     */
    void saveWinningTransaction(WinningTransaction winningTransaction);

}
