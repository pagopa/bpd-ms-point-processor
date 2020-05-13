package it.gov.pagopa.bpd.point_processor.connector.award_period.model;

import it.gov.pagopa.bpd.point_processor.connector.award_period.AwardPeriodRestClient;
import lombok.*;

import java.time.LocalDate;

/**
 * Resource model for the data recovered through {@link AwardPeriodRestClient}
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "awardPeriodId", callSuper = false)
public class AwardPeriod {

    private Long awardPeriodId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer gracePeriod;

}
