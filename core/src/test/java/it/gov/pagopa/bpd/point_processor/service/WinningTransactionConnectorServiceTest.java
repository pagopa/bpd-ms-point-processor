package it.gov.pagopa.bpd.point_processor.service;

import eu.sia.meda.BaseTest;
import eu.sia.meda.event.transformer.SimpleEventRequestTransformer;
import eu.sia.meda.event.transformer.SimpleEventResponseTransformer;
import it.gov.pagopa.bpd.point_processor.publisher.SaveTransactionPublisherConnector;
import it.gov.pagopa.bpd.point_processor.publisher.model.WinningTransaction;
import it.gov.pagopa.bpd.point_processor.publisher.model.enums.OperationType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Class for unit-testing {@link WinningTransactionConnectorService}
 */
public class WinningTransactionConnectorServiceTest extends BaseTest {

    @Mock
    private SaveTransactionPublisherConnector saveTransactionPublisherConnector;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private WinningTransactionConnectorService winningTransactionConnectorService;

    @SpyBean
    private SimpleEventRequestTransformer<WinningTransaction> simpleEventRequestTransformerSpy;

    @SpyBean
    private SimpleEventResponseTransformer simpleEventResponseTransformerSpy;

    @Before
    public void initTest() {
        Mockito.reset(saveTransactionPublisherConnector);
        winningTransactionConnectorService =
                new WinningTransactionConnectorServiceImpl(
                        saveTransactionPublisherConnector,
                        simpleEventRequestTransformerSpy,
                        simpleEventResponseTransformerSpy);
    }

    @Test
    public void testSave_Ok() {

        try {

            BDDMockito.doReturn(true)
                    .when(saveTransactionPublisherConnector)
                    .doCall(Mockito.eq(getSaveModel()),Mockito.any(),Mockito.any());

            winningTransactionConnectorService.saveWinningTransaction(getSaveModel());

            BDDMockito.verify(saveTransactionPublisherConnector, Mockito.atLeastOnce())
                    .doCall(Mockito.eq(getSaveModel()),Mockito.any(),Mockito.any());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void testSave_KO_Connector() {

        BDDMockito.doAnswer(invocationOnMock -> {
            throw new Exception();
        }).when(saveTransactionPublisherConnector)
          .doCall(Mockito.any(),Mockito.any(),Mockito.any());

        expectedException.expect(Exception.class);
        winningTransactionConnectorService.saveWinningTransaction(null);

        BDDMockito.verify(saveTransactionPublisherConnector, Mockito.atLeastOnce())
                .doCall(Mockito.any(),Mockito.any(),Mockito.any());
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