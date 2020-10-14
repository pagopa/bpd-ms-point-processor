package it.gov.pagopa.bpd.point_processor.config;

import it.gov.pagopa.bpd.point_processor.PointProcessorErrorPublisherConnector;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration class for {@link PointProcessorErrorPublisherConnector}
 */

@Configuration
@PropertySource("classpath:config/pointProcessorErrorPublisher.properties")
public class PointProcessorErrorPublisherConfig { }
