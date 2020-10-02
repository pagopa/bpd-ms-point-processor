package it.gov.pagopa.bpd.point_processor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
class ScoreMultiplierServiceImpl implements ScoreMultiplierService {

    @Value(value = "${it.gov.pagopa.bpd.point_processor.service.scoreMultiplier}")
    private Double scoreMultiplier;

    public BigDecimal getScoreMultiplier() {
        return BigDecimal.valueOf(scoreMultiplier);
    }

}
