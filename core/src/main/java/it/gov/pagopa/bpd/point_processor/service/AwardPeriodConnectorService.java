package it.gov.pagopa.bpd.point_processor.service;

import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;

import java.time.OffsetDateTime;

public interface AwardPeriodConnectorService {

    AwardPeriod getAwardPeriod(OffsetDateTime accountingDate);

}
