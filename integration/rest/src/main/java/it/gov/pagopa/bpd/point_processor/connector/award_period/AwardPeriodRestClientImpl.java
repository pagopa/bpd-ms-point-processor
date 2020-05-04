package it.gov.pagopa.bpd.point_processor.connector.award_period;

import eu.sia.meda.connector.rest.transformer.request.SimpleRestGetRequestTransformer;
import eu.sia.meda.connector.rest.transformer.response.SimpleRest2xxResponseTransformer;
import eu.sia.meda.service.BaseService;
import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;

@Service
class AwardPeriodRestClientImpl extends BaseService implements AwardPeriodRestClient {

    private final AwardPeriodRestConnector connector;
    private final SimpleRestGetRequestTransformer requestTransformer;
    private final SimpleRest2xxResponseTransformer<List<AwardPeriod>> responseTransformer;

    @Autowired
    public AwardPeriodRestClientImpl(AwardPeriodRestConnector connector,
                                SimpleRestGetRequestTransformer requestTransformer,
                                SimpleRest2xxResponseTransformer<List<AwardPeriod>> responseTransformer) {
        this.connector = connector;
        this.requestTransformer = requestTransformer;
        this.responseTransformer = responseTransformer;
    }

    @Override
    public List<AwardPeriod> getAwardPeriods(OffsetDateTime accountingDate) {

        final HashMap<String, Object> params = new HashMap<>();
        final HashMap<String, Object> queryParams = new HashMap<>();
        queryParams.put("offsetDateTime", accountingDate.toString());

        return connector.call(null, requestTransformer, responseTransformer, params, queryParams);
    }
}
