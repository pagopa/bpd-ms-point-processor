package it.gov.pagopa.bpd.point_processor.connector.award_period;

import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Interface defining the methods for the REST client related to {@link AwardPeriod} endpoints
 * @see AwardPeriodRestClientImpl
 */
public interface AwardPeriodRestClient {

    /**
     * Method for calling on the endpoint for finding {@link List<AwardPeriod>} related to the input date
     * @param accountingDate
     *            Instance of {@link OffsetDateTime} used to search for available {@link AwardPeriod}
     * @return Instance of {@link List<AwardPeriod>} related to the accountingDate
     */
    List<AwardPeriod> getAwardPeriods(OffsetDateTime accountingDate);

}
