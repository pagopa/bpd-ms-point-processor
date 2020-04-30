package it.gov.pagopa.bpd.point_processor.connector.award_period.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@Builder
@EqualsAndHashCode(of = "awardPeriodId", callSuper = false)
public class AwardPeriod {

    private Long awardPeriodId;
    private LocalDate startDate;
    private LocalDate endDate;

}
