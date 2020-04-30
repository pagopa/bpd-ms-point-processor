package it.gov.pagopa.bpd.point_processor.connector.award_period;

import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;

import java.time.OffsetDateTime;
import java.util.List;

public interface AwardPeriodRestClient {

    List<AwardPeriod> getAwardPeriods(OffsetDateTime accountingDate);

}
