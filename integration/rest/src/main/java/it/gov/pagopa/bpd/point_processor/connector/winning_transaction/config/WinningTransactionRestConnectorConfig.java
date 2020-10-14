package it.gov.pagopa.bpd.point_processor.connector.winning_transaction.config;

import it.gov.pagopa.bpd.common.connector.config.RestConnectorConfig;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.WinningTransactionRestClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Import(RestConnectorConfig.class)
@EnableFeignClients(clients = WinningTransactionRestClient.class)
@PropertySource("classpath:config/winning_transaction/rest-client.properties")
public class WinningTransactionRestConnectorConfig {
}
