package it.gov.pagopa.bpd.point_processor.connector.award_period;

import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;

import java.util.List;

/**
 * Interface defining the methods for the REST client related to {@link AwardPeriod} endpoints
 * @see AwardPeriodRestClientImpl
 */
public interface AwardPeriodRestClient {

    /**
     * Method for calling on the endpoint for finding {@link List<AwardPeriod>} related to the input date
     * @return Instance of {@link List<AwardPeriod>}, active
     */
    List<AwardPeriod> getAwardPeriods();

}
