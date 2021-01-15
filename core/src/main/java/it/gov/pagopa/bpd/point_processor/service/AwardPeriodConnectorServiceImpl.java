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
     * Implementation of {@link AwardPeriodConnectorService#getAwardPeriod(OffsetDateTime)}, that contacts
     * THe endpoint managed with {@link AwardPeriodRestClient} to recover {@link List<AwardPeriod>}, and recovers
     * the first active period available
     *
     * @param accountingDateTime {@link LocalDate} used for searching a {@link AwardPeriod}
     * @return instance of {@link AwardPeriod} associated to the input param
     */
    public AwardPeriod getAwardPeriod(OffsetDateTime accountingDateTime) {
        List<AwardPeriod> awardPeriods = awardPeriodRestClient.getActiveAwardPeriods();
        return awardPeriods.stream()
                .filter(awardPeriod -> checkAccountingDate(accountingDateTime, awardPeriod))
                .min(Comparator.comparing(AwardPeriod::getStartDate)).orElse(null);
    }


    private static boolean checkAccountingDate(OffsetDateTime accountingDateTime, AwardPeriod awardPeriod) {
        LocalDate accountingDate = accountingDateTime.toLocalDate();
        LocalDate endGracePeriodDate = awardPeriod.getEndDate().plusDays(awardPeriod.getGracePeriod() != null
                ? awardPeriod.getGracePeriod()
                : 30);

        return (accountingDate.equals(awardPeriod.getStartDate()) || accountingDate.isAfter(awardPeriod.getStartDate()))
                && (accountingDate.equals(endGracePeriodDate) || accountingDate.isBefore(endGracePeriodDate));
    }

}
