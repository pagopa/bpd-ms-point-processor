package it.gov.pagopa.bpd.point_processor.connector.award_period;

import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * AwardPeriod Rest Client
 */
@FeignClient(name = "${rest-client.award-period.serviceCode}", url = "${rest-client.award-period.base-url}")
public interface AwardPeriodRestClient {

    @GetMapping(value = "${rest-client.award-period.actives.url}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @Cacheable("awardPeriods")
    List<AwardPeriod> getActiveAwardPeriods();

}
