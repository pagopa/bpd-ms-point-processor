package it.gov.pagopa.bpd.point_processor.connector.winning_transaction.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration class for WinningTransactionRestConnector
 */

@Configuration
@PropertySource("classpath:config/WinningTransactionRestConnector.properties")
public class OldWinningTransactionRestConnectorConfig {
}
