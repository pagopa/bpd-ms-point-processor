package it.gov.pagopa.bpd.point_processor.service;

import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.WinningTransaction;

public interface WinningTransactionConnectorService {

    WinningTransaction saveWinningTransaction(WinningTransaction winningTransaction);

}
