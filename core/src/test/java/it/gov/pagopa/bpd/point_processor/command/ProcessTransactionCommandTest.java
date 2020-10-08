package it.gov.pagopa.bpd.point_processor.command;

import eu.sia.meda.BaseTest;
import it.gov.pagopa.bpd.point_processor.command.model.ProcessTransactionCommandModel;
import it.gov.pagopa.bpd.point_processor.command.model.Transaction;
import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.WinningTransaction;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.enums.OperationType;
import it.gov.pagopa.bpd.point_processor.mapper.TransactionMapper;
import it.gov.pagopa.bpd.point_processor.service.AwardPeriodConnectorService;
import it.gov.pagopa.bpd.point_processor.service.WinningTransactionConnectorService;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.BeanFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Class for unit-testing {@link ProcessTransactionCommand}
 */
public class ProcessTransactionCommandTest extends BaseTest {

    @Mock
    private WinningTransactionConnectorService winningTransactionConnectorServiceMock;

    @Mock
    private AwardPeriodConnectorService awardPeriodConnectorServiceMock;

    @Mock
    private RuleEngineExecutionCommand ruleEngineExecutionCommandMock;

    @Mock
    BeanFactory beanFactoryMock;

    @Spy
    TransactionMapper transactionMapperSpy;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    LocalDate localDate = LocalDate.now();

    @Before
    public void initTest() {
        Mockito.reset(
                winningTransactionConnectorServiceMock,
                awardPeriodConnectorServiceMock,
                ruleEngineExecutionCommandMock,
                beanFactoryMock,
                transactionMapperSpy);
        BDDMockito.doReturn(ruleEngineExecutionCommandMock).when(beanFactoryMock)
                .getBean(Mockito.eq(RuleEngineExecutionCommand.class),Mockito.any());
        BDDMockito.doReturn(getAwardPeriod()).when(awardPeriodConnectorServiceMock)
                .getAwardPeriod(Mockito.eq(localDate));
    }

    @Test
    public void testExecute_Ok_WinningTransaction() {

        Transaction transaction = getCommandModel();
        BDDMockito.doReturn(getSaveModel()).when(winningTransactionConnectorServiceMock)
                .saveWinningTransaction(Mockito.eq(getSaveModel()));

        ProcessTransactionCommand processTransactionCommand = new ProcessTransactionCommandImpl(
                ProcessTransactionCommandModel.builder().payload(transaction).build(),
                winningTransactionConnectorServiceMock,
                awardPeriodConnectorServiceMock,
                beanFactoryMock,
                transactionMapperSpy
        );

        try {

            BDDMockito.doReturn(BigDecimal.ONE).when(ruleEngineExecutionCommandMock).execute();

            Boolean commandResult = processTransactionCommand.execute();
            Assert.assertTrue(commandResult);

            BDDMockito.verify(awardPeriodConnectorServiceMock, Mockito.atLeastOnce())
                    .getAwardPeriod(Mockito.eq(localDate));
            BDDMockito.verify(ruleEngineExecutionCommandMock, Mockito.atLeastOnce()).execute();
            BDDMockito.verify(winningTransactionConnectorServiceMock, Mockito.atLeastOnce())
                    .saveWinningTransaction(Mockito.eq(getSaveModel()));

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @SneakyThrows
    @Test
    public void testExecute_KO_TransactionValidation() {

        Transaction transaction = getCommandModel();
        transaction.setAcquirerCode(null);

        ProcessTransactionCommand processTransactionCommand = new ProcessTransactionCommandImpl(
                ProcessTransactionCommandModel.builder().payload(transaction).build(),
                winningTransactionConnectorServiceMock,
                awardPeriodConnectorServiceMock,
                beanFactoryMock,
                transactionMapperSpy
        );

        exceptionRule.expect(Exception.class);
        processTransactionCommand.execute();

        BDDMockito.verifyZeroInteractions(awardPeriodConnectorServiceMock);
        BDDMockito.verifyZeroInteractions(winningTransactionConnectorServiceMock);
        BDDMockito.verifyZeroInteractions(ruleEngineExecutionCommandMock);

    }

    @Test
    public void testExecute_KO_TransactionNull() {

        Transaction transaction = getCommandModel();
        transaction.setAcquirerCode(null);

        ProcessTransactionCommand processTransactionCommand = new ProcessTransactionCommandImpl(
                ProcessTransactionCommandModel.builder().payload(null).build(),
                winningTransactionConnectorServiceMock,
                awardPeriodConnectorServiceMock,
                beanFactoryMock,
                transactionMapperSpy
        );

        try {

            exceptionRule.expect(AssertionError.class);
            processTransactionCommand.execute();

            BDDMockito.verifyZeroInteractions(awardPeriodConnectorServiceMock);
            BDDMockito.verifyZeroInteractions(winningTransactionConnectorServiceMock);
            BDDMockito.verifyZeroInteractions(ruleEngineExecutionCommandMock);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void testExecute_Ok_WinningTransaction_NegativeScore() {

        Transaction transaction = getCommandModel();
        WinningTransaction winningTransaction = getSaveModel();
        winningTransaction.setCashback(BigDecimal.valueOf(-1));
        BDDMockito.doReturn(getSaveModel()).when(winningTransactionConnectorServiceMock)
                .saveWinningTransaction(Mockito.eq(getSaveModel()));

        ProcessTransactionCommand processTransactionCommand = new ProcessTransactionCommandImpl(
                ProcessTransactionCommandModel.builder().payload(transaction).build(),
                winningTransactionConnectorServiceMock,
                awardPeriodConnectorServiceMock,
                beanFactoryMock,
                transactionMapperSpy
        );

        try {

            BDDMockito.doReturn(BigDecimal.valueOf(-1)).when(ruleEngineExecutionCommandMock).execute();

            Boolean commandResult = processTransactionCommand.execute();
            Assert.assertTrue(commandResult);

            BDDMockito.verify(awardPeriodConnectorServiceMock, Mockito.atLeastOnce())
                    .getAwardPeriod(Mockito.eq(localDate));
            BDDMockito.verify(ruleEngineExecutionCommandMock, Mockito.atLeastOnce()).execute();
            BDDMockito.verify(winningTransactionConnectorServiceMock, Mockito.atLeastOnce())
                    .saveWinningTransaction(Mockito.eq(getSaveModel()));

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @SneakyThrows
    @Test
    public void testExecute_OK_NoAwardPeriod() {

        Transaction transaction = getCommandModel();

        ProcessTransactionCommand processTransactionCommand = new ProcessTransactionCommandImpl(
                ProcessTransactionCommandModel.builder().payload(transaction).build(),
                winningTransactionConnectorServiceMock,
                awardPeriodConnectorServiceMock,
                beanFactoryMock,
                transactionMapperSpy
        );


        BDDMockito.doReturn(null).when(awardPeriodConnectorServiceMock)
                .getAwardPeriod(Mockito.any());

        exceptionRule.expect(Exception.class);
        processTransactionCommand.execute();

        BDDMockito.verify(awardPeriodConnectorServiceMock, Mockito.atLeastOnce())
                .getAwardPeriod(Mockito.eq(localDate));
        BDDMockito.verifyZeroInteractions(ruleEngineExecutionCommandMock);
        BDDMockito.verifyZeroInteractions(winningTransactionConnectorServiceMock);



    }

    @SneakyThrows
    @Test
    public void testExecute_KO_RuleEngineCommandError() {

        Transaction transaction = getCommandModel();

        ProcessTransactionCommand processTransactionCommand = new ProcessTransactionCommandImpl(
                ProcessTransactionCommandModel.builder().payload(transaction).build(),
                winningTransactionConnectorServiceMock,
                awardPeriodConnectorServiceMock,
                beanFactoryMock,
                transactionMapperSpy
        );

        BDDMockito.doThrow(new Exception()).when(ruleEngineExecutionCommandMock).execute();

        exceptionRule.expect(Exception.class);
        processTransactionCommand.execute();

        BDDMockito.verify(awardPeriodConnectorServiceMock, Mockito.atLeastOnce())
                .getAwardPeriod(Mockito.eq(localDate));
        BDDMockito.verify(ruleEngineExecutionCommandMock, Mockito.atLeastOnce()).execute();
        BDDMockito.verifyZeroInteractions(winningTransactionConnectorServiceMock);

    }

    protected Transaction getCommandModel() {
        return Transaction.builder()
                .idTrxAcquirer("1")
                .acquirerCode("001")
                .trxDate(OffsetDateTime.parse("2020-04-09T16:22:45.304Z"))
                .amount(BigDecimal.valueOf(1313.13))
                .operationType("00")
                .hpan("hpan")
                .merchantId("0")
                .circuitType("00")
                .mcc("813")
                .idTrxIssuer("0")
                .amountCurrency("833")
                .correlationId("1")
                .acquirerId("0")
                .bin("000001")
                .terminalId("0")
                .build();
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
                .cashback(BigDecimal.ONE)
                .bin("000001")
                .terminalId("0")
                .build();
    }

    protected AwardPeriod getAwardPeriod() {
        return AwardPeriod.builder()
                .awardPeriodId(1L)
                .startDate(OffsetDateTime.parse("2020-04-01T16:22:45.304Z").toLocalDate())
                .endDate(OffsetDateTime.parse("2999-04-30T16:22:45.304Z").toLocalDate())
                .build();
    }

}