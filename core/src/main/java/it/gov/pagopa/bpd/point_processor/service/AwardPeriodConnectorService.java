package it.gov.pagopa.bpd.point_processor.service;


import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;
import it.gov.pagopa.bpd.point_processor.exception.AwardPeriodNotFoundException;

import java.time.OffsetDateTime;

/**
 * Service for managing {@link AwardPeriod} data
 *
 * @see AwardPeriodConnectorServiceImpl
 */

public interface AwardPeriodConnectorService {

    /**
     * Method that manages the logic for recovering an AwardPeriod related to the date passed as input
     *
     * @param accountingDateTime {@link OffsetDateTime} used for searching a {@link AwardPeriod}
     * @return instance of {@link AwardPeriod} associated to the input param
     * @throws AwardPeriodNotFoundException
     */
    AwardPeriod getAwardPeriod(OffsetDateTime accountingDateTime) throws AwardPeriodNotFoundException;

}
