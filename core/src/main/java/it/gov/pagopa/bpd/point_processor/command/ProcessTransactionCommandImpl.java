package it.gov.pagopa.bpd.point_processor.command;

import eu.sia.meda.core.command.BaseCommand;
import it.gov.pagopa.bpd.point_processor.command.model.ProcessTransactionCommandModel;
import it.gov.pagopa.bpd.point_processor.command.model.Transaction;
import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.WinningTransaction;
import it.gov.pagopa.bpd.point_processor.service.AwardPeriodConnectorService;
import it.gov.pagopa.bpd.point_processor.service.WinningTransactionConnectorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
class ProcessTransactionCommandImpl extends BaseCommand<Boolean> implements ProcessTransactionCommand {

    private final ProcessTransactionCommandModel processTransactionCommandModel;
    private WinningTransactionConnectorService winningTransactionConnectorService;
    private AwardPeriodConnectorService awardPeriodConnectorService;
    private BeanFactory beanFactory;


    public ProcessTransactionCommandImpl(ProcessTransactionCommandModel processTransactionCommandModel){
        this.processTransactionCommandModel = processTransactionCommandModel;
    }

    public ProcessTransactionCommandImpl(ProcessTransactionCommandModel processTransactionCommandModel,
                                         WinningTransactionConnectorService winningTransactionConnectorService,
                                         AwardPeriodConnectorService awardPeriodConnectorService,
                                         BeanFactory beanFactory){
        this.processTransactionCommandModel = processTransactionCommandModel;
        this.winningTransactionConnectorService = winningTransactionConnectorService;
        this.awardPeriodConnectorService = awardPeriodConnectorService;
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

            AwardPeriod awardPeriod = awardPeriodConnectorService.getAwardPeriod(
                    processTransactionCommandModel.getPayload().getTrxDate());

            if (awardPeriod != null) {

                RuleEngineExecutionCommand ruleEngineExecutionCommand =
                        beanFactory.getBean(RuleEngineExecutionCommand.class, transaction);

                BigDecimal awardScore = ruleEngineExecutionCommand.execute();

                WinningTransaction winningTransaction = null;
                winningTransactionConnectorService.saveWinningTransaction(winningTransaction);

            }

            return true;

        } catch (Exception e) {
            //TODO: Gestione errori transazione su coda dedicata ed acknowledgment
            if (logger.isErrorEnabled()) {
                logger.error("Error occured during processing for transaction: " +
                        transaction.getIdTrxAcquirer() + ", " +
                        transaction.getAcquirerCode() + ", " +
                        transaction.getTrxDate());
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

}
