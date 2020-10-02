package it.gov.pagopa.bpd.point_processor.command;

import eu.sia.meda.core.command.BaseCommand;
import it.gov.pagopa.bpd.point_processor.command.model.ProcessTransactionCommandModel;
import it.gov.pagopa.bpd.point_processor.command.model.Transaction;
import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.WinningTransaction;
import it.gov.pagopa.bpd.point_processor.mapper.TransactionMapper;
import it.gov.pagopa.bpd.point_processor.service.AwardPeriodConnectorService;
import it.gov.pagopa.bpd.point_processor.service.WinningTransactionConnectorService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import javax.validation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;


/**
 * Class extending {@link BaseCommand<Boolean>}, implementation of {@link ProcessTransactionCommand}.
 * The command defines the execution of the whole {@link Transaction} processing, aggregating and hiding the
 * services used to call on the services and commands involved in the process
 *
 * @see ProcessTransactionCommandImpl
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
class BaseProcessTransactionCommandImpl extends BaseCommand<Boolean> implements ProcessTransactionCommand {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    private final ProcessTransactionCommandModel processTransactionCommandModel;
    private WinningTransactionConnectorService winningTransactionConnectorService;
    private AwardPeriodConnectorService awardPeriodConnectorService;
    private BeanFactory beanFactory;
    private TransactionMapper transactionMapper;
    private LocalDate processDateTime;

    public BaseProcessTransactionCommandImpl(ProcessTransactionCommandModel processTransactionCommandModel) {
        this.processTransactionCommandModel = processTransactionCommandModel;
        this.processDateTime = LocalDate.now();
    }

    public BaseProcessTransactionCommandImpl(ProcessTransactionCommandModel processTransactionCommandModel,
                                             WinningTransactionConnectorService winningTransactionConnectorService,
                                             AwardPeriodConnectorService awardPeriodConnectorService,
                                             BeanFactory beanFactory,
                                             TransactionMapper transactionMapper) {
        this.processTransactionCommandModel = processTransactionCommandModel;
        this.processDateTime = LocalDate.now();
        this.winningTransactionConnectorService = winningTransactionConnectorService;
        this.awardPeriodConnectorService = awardPeriodConnectorService;
        this.beanFactory = beanFactory;
        this.transactionMapper = transactionMapper;
    }


    /**
     * The processing logic contains the {@link Transaction} validation calls on {@link AwardPeriodConnectorService} to
     * recover an appropriate awardPeriod to use, calls on {@link RuleEngineExecutionCommand} to obtain the score
     * for the transaction, and if not equals to zero, calls on {@link WinningTransactionConnectorService} to save it
     *
     * @return {@link Boolean} defining the execution outcome
     */
    @SneakyThrows
    @Override
    public Boolean doExecute() {

        Transaction transaction = processTransactionCommandModel.getPayload();

        try {

            if (logger.isDebugEnabled()) {
                logger.debug("Executing ProcessTransactionCommand for transaction: " +
                        transaction.getIdTrxAcquirer() + ", " +
                        transaction.getAcquirerCode() + ", " +
                        transaction.getTrxDate());
            }

            validateRequest(transaction);

            AwardPeriod awardPeriod = awardPeriodConnectorService.getAwardPeriod(processDateTime);

            if (awardPeriod == null) {
                throw new Exception("No AwardPeriod found");
            }

            RuleEngineExecutionCommand ruleEngineExecutionCommand =
                    beanFactory.getBean(RuleEngineExecutionCommand.class, transaction, awardPeriod);

            BigDecimal awardScore = ruleEngineExecutionCommand.execute();

            if (awardScore.doubleValue() != 0) {
                WinningTransaction winningTransaction = transactionMapper.map(transaction);
                winningTransaction.setAwardPeriodId(awardPeriod.getAwardPeriodId());
                winningTransaction.setCashback(awardScore);
                winningTransactionConnectorService.saveWinningTransaction(winningTransaction);
            }

            return true;

        } catch (Exception e) {

            if (transaction != null) {

                if (logger.isErrorEnabled()) {
                    logger.error("Error occured during processing for transaction: " +
                            transaction.getIdTrxAcquirer() + ", " +
                            transaction.getAcquirerCode() + ", " +
                            transaction.getTrxDate());
                    logger.error(e.getMessage(), e);
                }

            }

            throw e;

        }

    }

    @Autowired
    public void setWinningTransactionConnectorService(
            WinningTransactionConnectorService winningTransactionConnectorService) {
        this.winningTransactionConnectorService = winningTransactionConnectorService;
    }

    @Autowired
    public void setAwardPeriodConnectorService(AwardPeriodConnectorService awardPeriodConnectorService) {
        this.awardPeriodConnectorService = awardPeriodConnectorService;
    }

    @Autowired
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Autowired
    public void setTransactionMapper(TransactionMapper transactionMapper) {
        this.transactionMapper = transactionMapper;
    }

    /**
     * Method to process a validation check for the parsed Transaction request
     *
     * @param request instance of Transaction, parsed from the inbound byte[] payload
     * @throws ConstraintViolationException
     */
    private void validateRequest(Transaction request) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(request);
        if (constraintViolations.size() > 0) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }

}
