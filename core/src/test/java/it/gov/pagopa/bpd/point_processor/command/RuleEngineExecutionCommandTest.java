package it.gov.pagopa.bpd.point_processor.command;

import eu.sia.meda.BaseTest;
import it.gov.pagopa.bpd.point_processor.command.model.Transaction;
import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;
import it.gov.pagopa.bpd.point_processor.service.ScoreMultiplierService;
import lombok.SneakyThrows;
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
 * Class for unit-testing {@link RuleEngineExecutionCommand}
 */
public class RuleEngineExecutionCommandTest extends BaseTest {

    @Mock
    private ScoreMultiplierService scoreMultiplierServiceMock;

    @Before
    public void initTest() {
        Mockito.reset(scoreMultiplierServiceMock);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @SneakyThrows
    @Test
    public void TestExecute_PositiveScore() {

        BDDMockito.doReturn(BigDecimal.valueOf(0.10D)).when(scoreMultiplierServiceMock)
                .getScoreMultiplier();

        RuleEngineExecutionCommand ruleEngineExecutionCommand =
                new RuleEngineExecutionCommandImpl(
                        getCommandModel(), getAwardPeriod(), scoreMultiplierServiceMock);
        BigDecimal score = ruleEngineExecutionCommand.execute();

        Assert.assertNotNull(score);
        Assert.assertEquals(Double.valueOf(10D),Double.valueOf(score.doubleValue()));

        BDDMockito.verify(scoreMultiplierServiceMock).getScoreMultiplier();

    }

    @SneakyThrows
    @Test
    public void TestExecute_NegativeScore() {

        BDDMockito.doReturn(BigDecimal.valueOf(0.10D)).when(scoreMultiplierServiceMock).getScoreMultiplier();

        Transaction transaction = getCommandModel();
        transaction.setOperationType("01");

        RuleEngineExecutionCommand ruleEngineExecutionCommand =
                new RuleEngineExecutionCommandImpl(
                        transaction, getAwardPeriod(), scoreMultiplierServiceMock);
        BigDecimal score = ruleEngineExecutionCommand.execute();

        Assert.assertNotNull(score);
        Assert.assertEquals(Double.valueOf(-10D),Double.valueOf(score.doubleValue()));

        BDDMockito.verify(scoreMultiplierServiceMock).getScoreMultiplier();

    }


    @SneakyThrows
    @Test
    public void TestExecute_Null() {

        BDDMockito.doThrow(new NullPointerException()).when(scoreMultiplierServiceMock).getScoreMultiplier();

        Transaction transaction = getCommandModel();
        transaction.setOperationType("01");

        RuleEngineExecutionCommand ruleEngineExecutionCommand =
                new RuleEngineExecutionCommandImpl(transaction, getAwardPeriod(), scoreMultiplierServiceMock);

        expectedException.expect(NullPointerException.class);
        ruleEngineExecutionCommand.execute();

        BDDMockito.verify(scoreMultiplierServiceMock).getScoreMultiplier();

    }

    protected AwardPeriod getAwardPeriod() {
        return AwardPeriod.builder().maxTransactionCashback(15).build();
    }

    protected Transaction getCommandModel() {
        return Transaction.builder()
                .idTrxAcquirer("1")
                .acquirerCode("001")
                .trxDate(OffsetDateTime.parse("2020-04-09T16:22:45.304Z"))
                .amount(BigDecimal.valueOf(100.0))
                .operationType("00")
                .hpan("hpan")
                .merchantId("0")
                .circuitType("00")
                .mcc("813")
                .idTrxIssuer("0")
                .amountCurrency("833")
                .correlationId("1")
                .acquirerId("0")
                .build();
    }

}