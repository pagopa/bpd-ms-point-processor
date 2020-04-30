package it.gov.pagopa.bpd.point_processor.command;

import eu.sia.meda.core.command.Command;
import it.gov.pagopa.bpd.point_processor.command.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class RuleEngineExecutionCommandImpl extends BaseRuleEngineExecutionCommandImpl implements Command<BigDecimal>  {

    public RuleEngineExecutionCommandImpl(Transaction transaction) {
        super(transaction);
    }

}
