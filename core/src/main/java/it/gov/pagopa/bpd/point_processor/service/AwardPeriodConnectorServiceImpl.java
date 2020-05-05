package it.gov.pagopa.bpd.point_processor.service;

import it.gov.pagopa.bpd.point_processor.connector.award_period.AwardPeriodRestClient;
import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Implementation of {@link AwardPeriodConnectorService}, that uses {@link AwardPeriodRestClient} for data recovery
 */
@Service
@Slf4j
class AwardPeriodConnectorServiceImpl implements AwardPeriodConnectorService {

    private AwardPeriodRestClient awardPeriodRestClient;

    @Autowired
    public AwardPeriodConnectorServiceImpl(AwardPeriodRestClient awardPeriodRestClient) {
        this.awardPeriodRestClient = awardPeriodRestClient;
    }

    /**
     * Implementation of {@link AwardPeriodConnectorService#getAwardPeriod(OffsetDateTime)}, that contacts
     * THe endpoint managed with {@link AwardPeriodRestClient} to recover an available {@link AwardPeriod}
     * @param accountingDate
     *              {@link OffsetDateTime} used for searching a {@link AwardPeriod}
     * @return instance of {@link AwardPeriod} associated to the input param
     */
    public AwardPeriod getAwardPeriod(OffsetDateTime accountingDate) {
        List<AwardPeriod> awardPeriods = awardPeriodRestClient.getAwardPeriods(accountingDate);
        return awardPeriods.isEmpty() ? null : awardPeriods.get(0);
    }

}
