package it.gov.pagopa.bpd.point_processor.service;


import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Service for managing {@link AwardPeriod} data
 * @see AwardPeriodConnectorServiceImpl
 */

public interface AwardPeriodConnectorService {

    /**
     * Method that manages the logic for recovering an AwardPeriod related to the date passed as input
     *
     * @param accountingDate {@link OffsetDateTime} used for searching a {@link AwardPeriod}
     * @return instance of {@link AwardPeriod} associated to the input param
     */
    AwardPeriod getAwardPeriod(LocalDate accountingDate, OffsetDateTime trxDate);

}
