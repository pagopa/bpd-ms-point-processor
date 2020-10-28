package it.gov.pagopa.bpd.point_processor.connector.award_period;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class AwardPeriodCacheServiceImpl {

    @Scheduled(cron = "${rest-client.award-period.actives.cache.evict.cron}")
    @CacheEvict(value = "awardPeriods", allEntries = true)
    public void awardPeriodsCacheEvict() {
        if (log.isInfoEnabled()) {
            log.info("evicted awardPeriods cache");
        }
    }

}
