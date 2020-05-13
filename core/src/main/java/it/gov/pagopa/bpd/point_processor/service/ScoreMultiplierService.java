package it.gov.pagopa.bpd.point_processor.service;

import java.math.BigDecimal;

public interface ScoreMultiplierService {

    public BigDecimal getScoreMultiplier(String mcc);

}
