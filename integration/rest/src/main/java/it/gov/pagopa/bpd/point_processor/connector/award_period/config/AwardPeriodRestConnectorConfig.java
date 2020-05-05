package it.gov.pagopa.bpd.point_processor.connector.award_period.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration class for AwardPeriodRestConnector
 */

@Configuration
@PropertySource("classpath:config/AwardPeriodRestConnector.properties")
public class AwardPeriodRestConnectorConfig { }
