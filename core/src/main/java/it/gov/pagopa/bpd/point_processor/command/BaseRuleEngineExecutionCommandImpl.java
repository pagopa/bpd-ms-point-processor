package it.gov.pagopa.bpd.point_processor.command;

import eu.sia.meda.core.command.BaseCommand;
import it.gov.pagopa.bpd.point_processor.command.model.Transaction;
import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;
import it.gov.pagopa.bpd.point_processor.service.ScoreMultiplierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import java.math.BigDecimal;

import static java.math.BigDecimal.ROUND_HALF_DOWN;

/**
 *
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
class BaseRuleEngineExecutionCommandImpl extends BaseCommand<BigDecimal> implements RuleEngineExecutionCommand {

    private final Transaction transaction;
    private final AwardPeriod awardPeriod;
    private ScoreMultiplierService scoreMultiplierService;

    public BaseRuleEngineExecutionCommandImpl(
            Transaction transaction, AwardPeriod awardPeriod) {
        this.transaction = transaction;
        this.awardPeriod = awardPeriod;
    }

    public BaseRuleEngineExecutionCommandImpl(
            Transaction transaction, AwardPeriod awardPeriod, ScoreMultiplierService scoreMultiplierService) {
        this.transaction = transaction;
        this.awardPeriod = awardPeriod;
        this.scoreMultiplierService = scoreMultiplierService;
    }

    @Override
    public BigDecimal doExecute() {
        BigDecimal awardScore = scoreMultiplierService.getScoreMultiplier().multiply(transaction.getAmount())
                .min(BigDecimal.valueOf(awardPeriod.getMaxTransactionCashback())).setScale(2, ROUND_HALF_DOWN);
        return "01".equals(transaction.getOperationType()) ? awardScore.negate() : awardScore;
    }

    @Autowired
    public void setScoreMultiplierService(ScoreMultiplierService scoreMultiplierService) {
        this.scoreMultiplierService = scoreMultiplierService;
    }

}
