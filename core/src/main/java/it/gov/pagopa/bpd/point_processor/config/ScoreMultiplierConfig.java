package it.gov.pagopa.bpd.point_processor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config/scoreMultiplier.properties")
public class ScoreMultiplierConfig {}
