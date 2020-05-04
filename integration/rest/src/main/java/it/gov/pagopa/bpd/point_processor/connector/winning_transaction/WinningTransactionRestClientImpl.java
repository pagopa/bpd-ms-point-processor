package it.gov.pagopa.bpd.point_processor.connector.winning_transaction;

import eu.sia.meda.connector.rest.transformer.request.SimpleRestPostRequestTransformer;
import eu.sia.meda.connector.rest.transformer.response.SimpleRest2xxResponseTransformer;
import eu.sia.meda.service.BaseService;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.WinningTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
class WinningTransactionRestClientImpl extends BaseService  implements WinningTransactionRestClient {

    private final WinningTransactionRestConnector connector;
    private final SimpleRestPostRequestTransformer requestTransformer;
    private final SimpleRest2xxResponseTransformer<WinningTransaction> responseTransformer;

    @Autowired
    public WinningTransactionRestClientImpl(WinningTransactionRestConnector connector,
                                            SimpleRestPostRequestTransformer requestTransformer,
                                     SimpleRest2xxResponseTransformer<WinningTransaction> responseTransformer) {
        this.connector = connector;
        this.requestTransformer = requestTransformer;
        this.responseTransformer = responseTransformer;
    }

    public WinningTransaction saveWinningTransaction(WinningTransaction winningTransaction) {
        final HashMap<String, Object> params = new HashMap<>();
        final HashMap<String, Object> queryParams = new HashMap<>();
        return connector.call(winningTransaction, requestTransformer, responseTransformer, params, queryParams);
    }

}
