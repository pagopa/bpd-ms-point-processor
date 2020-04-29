package it.gov.pagopa.bpd.point_processor.command;

import eu.sia.meda.core.command.BaseCommand;
import it.gov.pagopa.bpd.point_processor.command.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
class RuleEngineExecutionCommandImpl extends BaseCommand<BigDecimal> implements RuleEngineExecutionCommand {

    private final Transaction transaction;

    public RuleEngineExecutionCommandImpl(Transaction transaction){
        this.transaction = transaction;
    }

    @Override
    public BigDecimal doExecute() {
        //TODO: Execute RuleEngine rules
        return BigDecimal.valueOf(
                    new Random()
                        .doubles(1, -10, 11)
                        .findFirst().getAsDouble()
                );
    }

}
