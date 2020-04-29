package it.gov.pagopa.bpd.point_processor.service;

import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.WinningTransactionRestClient;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.WinningTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WinningTransactionConnectorServiceImpl implements WinningTransactionConnectorService {

    private WinningTransactionRestClient winningTransactionRestClient;

    @Autowired
    public WinningTransactionConnectorServiceImpl(WinningTransactionRestClient winningTransactionRestClient) {
        this.winningTransactionRestClient = winningTransactionRestClient;
    }

    @Override
    public WinningTransaction saveWinningTransaction(WinningTransaction winningTransaction) {
        return winningTransactionRestClient.saveWinningTransaction(winningTransaction);
    }
}
