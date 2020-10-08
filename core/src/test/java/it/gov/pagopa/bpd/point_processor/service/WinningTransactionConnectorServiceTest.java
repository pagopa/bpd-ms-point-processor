package it.gov.pagopa.bpd.point_processor.service;

import eu.sia.meda.BaseTest;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.WinningTransactionRestClient;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.WinningTransaction;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.enums.OperationType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Class for unit-testing {@link WinningTransactionConnectorService}
 */
public class WinningTransactionConnectorServiceTest extends BaseTest {

    @Mock
    private WinningTransactionRestClient winningTransactionRestClientMock;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private WinningTransactionConnectorService winningTransactionConnectorService;

    @Before
    public void initTest() {
        Mockito.reset(winningTransactionRestClientMock);
        winningTransactionConnectorService =
                new WinningTransactionConnectorServiceImpl(winningTransactionRestClientMock);
    }

    @Test
    public void testSave_Ok() {

        try {

            BDDMockito.doReturn(getSaveModel())
                    .when(winningTransactionRestClientMock)
                    .saveWinningTransaction(Mockito.eq(getSaveModel()));

            WinningTransaction winningTransaction = winningTransactionConnectorService
                    .saveWinningTransaction(getSaveModel());

            Assert.assertEquals(getSaveModel(), winningTransaction);
            BDDMockito.verify(winningTransactionRestClientMock, Mockito.atLeastOnce())
                    .saveWinningTransaction(Mockito.eq(getSaveModel()));

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void testSave_KO_Connector() {

        BDDMockito.doAnswer(invocationOnMock -> {
            throw new Exception();
        }).when(winningTransactionRestClientMock)
          .saveWinningTransaction(Mockito.any());

        expectedException.expect(Exception.class);
        winningTransactionConnectorService.saveWinningTransaction(null);

        BDDMockito.verify(winningTransactionRestClientMock, Mockito.atLeastOnce())
                .saveWinningTransaction(Mockito.any());

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