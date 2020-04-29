package it.gov.pagopa.bpd.point_processor.connector.winning_transaction;

import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.WinningTransaction;

public interface WinningTransactionRestClient {

    public WinningTransaction saveWinningTransaction(WinningTransaction winningTransaction);

}
