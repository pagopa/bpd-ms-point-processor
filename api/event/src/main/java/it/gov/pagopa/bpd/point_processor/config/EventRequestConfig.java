package it.gov.pagopa.bpd.point_processor.config;

import it.gov.pagopa.bpd.point_processor.listener.OnTransactionProcessRequestListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration class for {@link OnTransactionProcessRequestListener}
 */

@Configuration
@PropertySource("classpath:config/transactionRequestListener.properties")
public class EventRequestConfig { }
