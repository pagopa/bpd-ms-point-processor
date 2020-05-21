package it.gov.pagopa.bpd.point_processor.service;

import it.gov.pagopa.bpd.point_processor.connector.award_period.OldAwardPeriodRestClient;
import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * Implementation of {@link AwardPeriodConnectorService}, that uses {@link OldAwardPeriodRestClient} for data recovery
 */
@Service
@Slf4j
class AwardPeriodConnectorServiceImpl implements AwardPeriodConnectorService {

    private OldAwardPeriodRestClient oldAwardPeriodRestClient;

    @Autowired
    public AwardPeriodConnectorServiceImpl(OldAwardPeriodRestClient oldAwardPeriodRestClient) {
        this.oldAwardPeriodRestClient = oldAwardPeriodRestClient;
    }

    /**
     * Implementation of {@link AwardPeriodConnectorService#getAwardPeriod(LocalDate)}, that contacts
     * THe endpoint managed with {@link OldAwardPeriodRestClient} to recover {@link List<AwardPeriod>}, and recovers
     * the first active period available
     *
     * @param accountingDate {@link LocalDate} used for searching a {@link AwardPeriod}
     * @return instance of {@link AwardPeriod} associated to the input param
     */
    public AwardPeriod getAwardPeriod(LocalDate accountingDate) {
        List<AwardPeriod> awardPeriods = oldAwardPeriodRestClient.getAwardPeriods();
        return awardPeriods.stream().sorted(Comparator.comparing(AwardPeriod::getStartDate))
                .filter(awardPeriod -> {
                    LocalDate startDate = awardPeriod.getStartDate();
                    LocalDate endDate = awardPeriod.getEndDate();
                    Integer gracePeriod = awardPeriod.getGracePeriod();
                    LocalDate endGracePeriodDate = endDate.plusDays(gracePeriod != null ? gracePeriod : 30);
                    return (accountingDate.equals(startDate) || accountingDate.isAfter(startDate)) &&
                           (accountingDate.equals(endGracePeriodDate) || accountingDate.isBefore(endGracePeriodDate));

                })
                .findFirst().orElse(null);
    }

}
