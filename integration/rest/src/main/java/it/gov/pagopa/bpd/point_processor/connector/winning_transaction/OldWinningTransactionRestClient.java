package it.gov.pagopa.bpd.point_processor.connector.winning_transaction;

import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.WinningTransaction;

/**
 * Interface defining the methods for the REST client related to {@link WinningTransaction} endpoints
 *
 * @see OldWinningTransactionRestClientImpl
 */
public interface OldWinningTransactionRestClient {

    /**
     * Method for calling on the endpoint for saving a {@link WinningTransaction} instance
     *
     * @param winningTransaction Instance of {@link WinningTransaction} to be saved
     * @return Instance of {@link WinningTransaction} containing the returing resource of the saving process
     */
    WinningTransaction saveWinningTransaction(WinningTransaction winningTransaction);

}
