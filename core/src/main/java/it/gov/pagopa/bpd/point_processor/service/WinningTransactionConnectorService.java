package it.gov.pagopa.bpd.point_processor.service;

import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.WinningTransactionRestClient;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.WinningTransaction;

/**
 * Service for managing {@link WinningTransaction} data
 * @see WinningTransactionConnectorServiceImpl
 */

public interface WinningTransactionConnectorService {

    /**
     * Implementation of {@link WinningTransactionConnectorServiceImpl#saveWinningTransaction(WinningTransaction)},
     * that contacts the endpoint managed with {@link WinningTransactionRestClient},
     * for saving a {@link WinningTransaction}
     * @param winningTransaction
     *              {@link WinningTransaction} instance to save
     * @return instance of {@link WinningTransaction}, resulting from the save operation
     */
    WinningTransaction saveWinningTransaction(WinningTransaction winningTransaction);

}
