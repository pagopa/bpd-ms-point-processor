package it.gov.pagopa.bpd.point_processor.service;

import it.gov.pagopa.bpd.point_processor.config.PointProcessorProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
class ScoreMultiplierServiceImpl implements ScoreMultiplierService {

    private final PointProcessorProperties pointProcessorProperties;

    @PostConstruct
    public void post() {
       if (log.isDebugEnabled()) {
           log.debug("Loaded MccScoreMultiplierMap from propertie:");
           log.debug(pointProcessorProperties.getMccScoreMultiplier().toString());
       }
    }

    public BigDecimal getScoreMultiplier(String mcc) {
        Double scoreMultiplier = pointProcessorProperties.getMccScoreMultiplier().get(mcc);
        return scoreMultiplier != null ? BigDecimal.valueOf(scoreMultiplier) : BigDecimal.ZERO;
    }

}
