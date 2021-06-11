package it.gov.pagopa.bpd.point_processor.service;

import it.gov.pagopa.bpd.point_processor.connector.award_period.AwardPeriodRestClient;
import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Comparator;
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
     * Implementation of {@link AwardPeriodConnectorService#getAwardPeriod(LocalDate, OffsetDateTime)}, that contacts
     * THe endpoint managed with {@link AwardPeriodRestClient} to recover {@link List<AwardPeriod>}, and recovers
     * the first active period available
     *
     * @param accountingDate {@link LocalDate} used for searching a {@link AwardPeriod}
     * @return instance of {@link AwardPeriod} associated to the input param
     */
    public AwardPeriod getAwardPeriod(LocalDate accountingDate, OffsetDateTime trxDate) {
        List<AwardPeriod> awardPeriods = awardPeriodRestClient.getActiveAwardPeriods();
        return awardPeriods.stream().sorted(Comparator.comparing(AwardPeriod::getStartDate))
                .filter(awardPeriod -> {
                    LocalDate startDate = awardPeriod.getStartDate();
                    LocalDate endDate = awardPeriod.getEndDate();
                    Integer gracePeriod = awardPeriod.getGracePeriod();
                    LocalDate endGracePeriodDate = endDate.plusDays(gracePeriod != null ? gracePeriod : 30);
                    return (accountingDate.equals(startDate) || accountingDate.isAfter(startDate)) &&
                            (accountingDate.equals(endGracePeriodDate) || accountingDate.isBefore(endGracePeriodDate)) &&
                            (trxDate.toLocalDate().equals(startDate) ||
                            (trxDate.toLocalDate().isAfter(startDate) && trxDate.toLocalDate().isBefore(endDate))
                                    || trxDate.toLocalDate().equals(endDate));

                })
                .findFirst().orElse(null);
    }

}
