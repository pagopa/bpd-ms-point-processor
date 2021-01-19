package it.gov.pagopa.bpd.point_processor.service;

import it.gov.pagopa.bpd.point_processor.connector.award_period.AwardPeriodRestClient;
import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;
import it.gov.pagopa.bpd.point_processor.exception.AwardPeriodNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Implementation of {@link AwardPeriodConnectorService}, that uses {@link AwardPeriodRestClient} for data recovery
 */
@Service
@Slf4j
class AwardPeriodConnectorServiceImpl implements AwardPeriodConnectorService {

    private final AwardPeriodRestClient awardPeriodRestClient;

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
     * @throws AwardPeriodNotFoundException
     */
    public AwardPeriod getAwardPeriod(OffsetDateTime accountingDateTime) throws AwardPeriodNotFoundException {
        AwardPeriod result;

        if (OffsetDateTime.now().isBefore(accountingDateTime)) {
            throw new AwardPeriodNotFoundException("Future accounting date");
        }

        List<AwardPeriod> awardPeriods = awardPeriodRestClient.findAll();

        if (awardPeriods == null || awardPeriods.isEmpty()) {
            throw new AwardPeriodNotFoundException("No periods available");
        }

        if (isWithinProgrammeScope(accountingDateTime.toLocalDate(), awardPeriods)) {
            final LocalDate today = LocalDate.now();
            TreeSet<AwardPeriod> activeAwardPeriods = awardPeriods.stream()
                    .filter(awardPeriod -> isWithinPeriod(today, awardPeriod))
                    .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(AwardPeriod::getStartDate))));

            if (activeAwardPeriods.isEmpty()) {
                throw new AwardPeriodNotFoundException("No active periods available");

            } else {
                result = activeAwardPeriods.first();

                for (AwardPeriod awardPeriod : activeAwardPeriods) {

                    if (isWithinPeriodStrict(accountingDateTime.toLocalDate(), awardPeriod)) {
                        result = awardPeriod;
                    }
                }
            }

        } else {
            throw new AwardPeriodNotFoundException("Accounting date out of programme scope");
        }

        return result;
    }


    private boolean isWithinProgrammeScope(LocalDate accountingDate, List<AwardPeriod> awardPeriods) {
        boolean result = false;

        if (!awardPeriods.isEmpty()) {
            awardPeriods.sort(Comparator.comparing(AwardPeriod::getStartDate));
            LocalDate programmeStartDate = awardPeriods.get(0).getStartDate();
            LocalDate endGracePeriodDate = awardPeriods.get(awardPeriods.size() - 1).getEndDate()
                    .plusDays(awardPeriods.get(awardPeriods.size() - 1).getGracePeriod());

            result = (accountingDate.equals(programmeStartDate) || accountingDate.isAfter(programmeStartDate))
                    && (accountingDate.equals(endGracePeriodDate) || accountingDate.isBefore(endGracePeriodDate));
        }

        return result;
    }


    private boolean isWithinPeriod(LocalDate localDate, AwardPeriod awardPeriod) {
        LocalDate endGracePeriodDate = awardPeriod.getEndDate()
                .plusDays(awardPeriod.getGracePeriod());

        return (localDate.equals(awardPeriod.getStartDate()) || localDate.isAfter(awardPeriod.getStartDate()))
                && (localDate.equals(endGracePeriodDate) || localDate.isBefore(endGracePeriodDate));
    }


    private boolean isWithinPeriodStrict(LocalDate localDate, AwardPeriod awardPeriod) {

        return (localDate.equals(awardPeriod.getStartDate()) || localDate.isAfter(awardPeriod.getStartDate()))
                && (localDate.equals(awardPeriod.getEndDate()) || localDate.isBefore(awardPeriod.getEndDate()));
    }

}
