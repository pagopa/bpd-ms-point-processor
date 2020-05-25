package it.gov.pagopa.bpd.point_processor.connector.winning_transaction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.bpd.common.connector.BaseFeignRestClientTest;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.config.WinningTransactionRestConnectorConfig;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.WinningTransaction;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.enums.OperationType;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@TestPropertySource(
        locations = "classpath:config/winning_transaction/rest-client.properties",
        properties = "spring.application.name=bpd-ms-point-processor-integration-rest")
@Import({WinningTransactionRestConnectorConfig.class})
public class WinningTransactionRestClientTest extends BaseFeignRestClientTest {

    static {
        SERIVICE_PORT_ENV_VAR_NAME = "BPD_MS_WINNING_TRANSACTION_PORT";
    }

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WinningTransactionRestClient restClient;

    @Test
    public void saveWinningTransaction_Ok() throws IOException {

        InputStream mockedJson = getClass()
                .getClassLoader()
                .getResourceAsStream("winning_transaction/saveMock.json");

        final JsonNode jsonNode = objectMapper.readTree(mockedJson);

        stubFor(post(urlEqualTo("/bpd/winning-transactions"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .withBody(jsonNode.toString())));

        WinningTransaction winningTransaction = restClient.saveWinningTransaction(getSaveModel());
        Assert.assertNotNull(winningTransaction);
    }

    protected WinningTransaction getSaveModel() {
        return WinningTransaction.builder()
                .idTrxAcquirer("1")
                .acquirerCode("001")
                .trxDate(OffsetDateTime.parse("2020-04-09T16:22:45.304Z"))
                .amount(BigDecimal.valueOf(1313.13))
                .operationType(OperationType.PAGAMENTO)
                .hpan("hpan")
                .merchantId("0")
                .circuitType("00")
                .mcc("813")
                .idTrxIssuer("0")
                .amountCurrency("833")
                .correlationId("1")
                .acquirerId("0")
                .awardPeriodId(1L)
                .score(BigDecimal.ONE)
                .build();
    }

}