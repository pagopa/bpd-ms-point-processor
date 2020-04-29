package it.gov.pagopa.bpd.point_processor.connector.winning_transaction.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config/WinningTransactionProcessorRestConnector.properties")
public class WinningTransactionRestConnectorConfig { }
