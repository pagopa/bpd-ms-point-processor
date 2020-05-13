package it.gov.pagopa.bpd.point_processor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Data
@Component
@ConfigurationProperties(prefix = "point-processor")
public class PointProcessorProperties{

    String test;
    HashMap<String, Double> mccScoreMultiplier;

}
