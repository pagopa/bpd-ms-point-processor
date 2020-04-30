package it.gov.pagopa.bpd.point_processor.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sia.meda.core.command.BaseCommand;
import it.gov.pagopa.bpd.point_processor.command.model.ProcessTransactionCommandModel;
import it.gov.pagopa.bpd.point_processor.command.model.Transaction;
import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.WinningTransaction;
import it.gov.pagopa.bpd.point_processor.mapper.TransactionMapper;
import it.gov.pagopa.bpd.point_processor.service.AwardPeriodConnectorService;
import it.gov.pagopa.bpd.point_processor.service.PointProcessorErrorPublisherService;
import it.gov.pagopa.bpd.point_processor.service.WinningTransactionConnectorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.*;
import java.math.BigDecimal;
import java.util.Set;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
class BaseProcessTransactionCommandImpl extends BaseCommand<Boolean> implements ProcessTransactionCommand {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    private final ProcessTransactionCommandModel processTransactionCommandModel;
    private WinningTransactionConnectorService winningTransactionConnectorService;
    private AwardPeriodConnectorService awardPeriodConnectorService;
    private PointProcessorErrorPublisherService pointProcessorErrorPublisherService;
    private BeanFactory beanFactory;
    private ObjectMapper objectMapper;
    private TransactionMapper transactionMapper;

    public BaseProcessTransactionCommandImpl(ProcessTransactionCommandModel processTransactionCommandModel){
        this.processTransactionCommandModel = processTransactionCommandModel;
    }

    public BaseProcessTransactionCommandImpl(ProcessTransactionCommandModel processTransactionCommandModel,
                                         WinningTransactionConnectorService winningTransactionConnectorService,
                                         AwardPeriodConnectorService awardPeriodConnectorService,
                                         PointProcessorErrorPublisherService pointProcessorErrorPublisherService,
                                         ObjectMapper objectMapper,
                                         BeanFactory beanFactory){
        this.processTransactionCommandModel = processTransactionCommandModel;
        this.winningTransactionConnectorService = winningTransactionConnectorService;
        this.awardPeriodConnectorService = awardPeriodConnectorService;
        this.pointProcessorErrorPublisherService = pointProcessorErrorPublisherService;
        this.objectMapper = objectMapper;
        this.beanFactory = beanFactory;
    }


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

            AwardPeriod awardPeriod = awardPeriodConnectorService.getAwardPeriod(
                    processTransactionCommandModel.getPayload().getTrxDate());

            if (awardPeriod != null) {

                RuleEngineExecutionCommand ruleEngineExecutionCommand =
                        beanFactory.getBean(RuleEngineExecutionCommand.class, transaction);

                BigDecimal awardScore = ruleEngineExecutionCommand.execute();

                if (awardScore.doubleValue() > 0) {
                    WinningTransaction winningTransaction = transactionMapper.map(transaction);
                    winningTransaction.setAwardPeriodId(awardPeriod.getAwardPeriodId());
                    winningTransaction.setScore(awardScore);
                    winningTransactionConnectorService.saveWinningTransaction(winningTransaction);
                }

            }

            return true;

        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("Error occured during processing for transaction: " +
                        transaction.getIdTrxAcquirer() + ", " +
                        transaction.getAcquirerCode() + ", " +
                        transaction.getTrxDate());
            }
            try {
                pointProcessorErrorPublisherService.publishErrorEvent(
                        objectMapper.writeValueAsBytes(transaction),
                        processTransactionCommandModel.getHeaders(),
                        "Error occured during processing for transaction:" + e.getMessage());
            } catch (JsonProcessingException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(e.getMessage(),e);
                }
            }
            return false;
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

    /**
     * Method to process a validation check for the parsed Transaction request
     * @param request
     *          instance of Transaction, parsed from the inbound byye[] payload
     * @throws ConstraintViolationException
     */
    private void validateRequest(Transaction request) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(request);
        if (constraintViolations.size() > 0) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }

}
