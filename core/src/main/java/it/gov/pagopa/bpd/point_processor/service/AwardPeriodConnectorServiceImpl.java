package it.gov.pagopa.bpd.point_processor.service;

import it.gov.pagopa.bpd.point_processor.connector.award_period.AwardPeriodRestClient;
import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Slf4j
class AwardPeriodConnectorServiceImpl implements AwardPeriodConnectorService {

    private AwardPeriodRestClient awardPeriodRestClient;

    @Autowired
    public AwardPeriodConnectorServiceImpl(AwardPeriodRestClient awardPeriodRestClient) {
        this.awardPeriodRestClient = awardPeriodRestClient;
    }

    public AwardPeriod getAwardPeriod(OffsetDateTime accountingDate) {
        List<AwardPeriod> awardPeriods = awardPeriodRestClient.getAwardPeriods(accountingDate);
        return awardPeriods.isEmpty() ? null : awardPeriods.get(0);
    }

}
