package it.gov.pagopa.bpd.point_processor.connector.award_period;

import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AwardPeriod Rest Client
 */
@FeignClient(name = "${rest-client.award-period.serviceCode}", url = "${rest-client.award-period.base-url}")
public interface AwardPeriodRestClient {

    @GetMapping(value = "${rest-client.award-period.findAll.url}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    List<AwardPeriod> findAll();

    @Cacheable(value = "awardPeriods", key = "#root.method.name+#ordering.name()")
    default List<AwardPeriod> findAll(Ordering ordering) {
        List<AwardPeriod> awardPeriods = findAll();
        if (awardPeriods != null) {
            awardPeriods = awardPeriods.stream()
                    .sorted(ordering.comparator)
                    .collect(Collectors.toList());
        }
        return awardPeriods;
    }

    enum Ordering {
        START_DATE(Comparator.comparing(AwardPeriod::getStartDate));

        private final Comparator<AwardPeriod> comparator;

        Ordering(Comparator<AwardPeriod> comparator) {
            this.comparator = comparator;
        }

    }

}
