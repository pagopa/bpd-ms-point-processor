package it.gov.pagopa.bpd.point_processor.connector.winning_transaction;

import eu.sia.meda.connector.meda.ArchMedaInternalConnectorConfigurationService;
import eu.sia.meda.connector.rest.BaseRestConnectorTest;
import it.gov.pagopa.bpd.point_processor.connector.award_period.OldAwardPeriodRestClient;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.WinningTransaction;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.enums.OperationType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Test class for {@link OldAwardPeriodRestClient}
 */

@Import({
        OldWinningTransactionRestClientImpl.class,
        WinningTransactionRestConnector.class,
        ArchMedaInternalConnectorConfigurationService.class
})
@TestPropertySource(
        locations = {
                "classpath:config/WinningTransactionRestConnector.properties"
        },
        properties = {
                "connectors.medaInternalConfigurations.items.WinningTransactionRestConnector.mocked=true",
                "connectors.medaInternalConfigurations.items.WinningTransactionRestConnector.randomMock=false",
                "connectors.medaInternalConfigurations.items.WinningTransactionRestConnector.path=winning-transactions/save"
        })
public class OldWinningTransactionRestClientImplTest extends BaseRestConnectorTest {

    @Autowired
    OldWinningTransactionRestClient oldWinningTransactionRestClient;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void saveWinningTransaction_Ok() {
        try {
            WinningTransaction winningTransaction = oldWinningTransactionRestClient
                    .saveWinningTransaction(getSaveModel());
            Assert.assertNotNull(winningTransaction);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
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