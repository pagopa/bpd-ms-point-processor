package it.gov.pagopa.bpd.point_processor.command;

import eu.sia.meda.core.command.Command;

import java.math.BigDecimal;

/**
 * Interface extending {@link Command<BigDecimal>}, defines the command,
 * to be used for score calculation through the rule-engine process
 * @see BaseRuleEngineExecutionCommandImpl
 * @see RuleEngineExecutionCommandImpl
 */

public interface RuleEngineExecutionCommand extends Command<BigDecimal> {}
