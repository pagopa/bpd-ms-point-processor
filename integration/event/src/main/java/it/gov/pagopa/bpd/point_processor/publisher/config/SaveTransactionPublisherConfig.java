package it.gov.pagopa.bpd.point_processor.publisher.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration class for the SaveTransactionPublisherConfig
 */

@Configuration
@PropertySource("classpath:config/saveTransactionPublisher.properties")
public class SaveTransactionPublisherConfig {
}
