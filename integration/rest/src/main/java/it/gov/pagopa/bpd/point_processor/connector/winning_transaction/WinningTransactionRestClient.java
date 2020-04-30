package it.gov.pagopa.bpd.point_processor.connector.winning_transaction;

import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.WinningTransaction;

public interface WinningTransactionRestClient {

    WinningTransaction saveWinningTransaction(WinningTransaction winningTransaction);

}
