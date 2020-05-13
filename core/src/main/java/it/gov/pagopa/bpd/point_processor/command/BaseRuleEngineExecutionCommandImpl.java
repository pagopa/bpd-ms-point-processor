package it.gov.pagopa.bpd.point_processor.command;

import eu.sia.meda.core.command.BaseCommand;
import it.gov.pagopa.bpd.point_processor.command.model.Transaction;
import it.gov.pagopa.bpd.point_processor.service.ScoreMultiplierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import java.math.BigDecimal;

/**
 *
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
class BaseRuleEngineExecutionCommandImpl extends BaseCommand<BigDecimal> implements RuleEngineExecutionCommand {

    private final Transaction transaction;
    private ScoreMultiplierService scoreMultiplierService;

    public BaseRuleEngineExecutionCommandImpl(Transaction transaction){
        this.transaction = transaction;
    }

    public BaseRuleEngineExecutionCommandImpl(Transaction transaction, ScoreMultiplierService scoreMultiplierService) {
        this.transaction = transaction;
        this.scoreMultiplierService = scoreMultiplierService;
    }

    @Override
    public BigDecimal doExecute() {
        BigDecimal multiplier_sign = BigDecimal.valueOf(
                ("01".equals(transaction.getOperationType()) ? 1.0 : -1.0));
        BigDecimal scoreMultiplier = scoreMultiplierService.getScoreMultiplier(transaction.getMcc());
        return multiplier_sign.multiply(scoreMultiplier.multiply(transaction.getAmount()));
    }

    @Autowired
    public void setScoreMultiplierService(ScoreMultiplierService scoreMultiplierService) {
        this.scoreMultiplierService = scoreMultiplierService;
    }

}
