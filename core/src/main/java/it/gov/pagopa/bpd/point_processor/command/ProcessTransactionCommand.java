package it.gov.pagopa.bpd.point_processor.command;

import eu.sia.meda.core.command.Command;
import it.gov.pagopa.bpd.point_processor.command.model.Transaction;

/**
 * Interface extending {@link Command<Boolean>}, defines the command,
 * to be used for inbound {@link Transaction} to be processed
 * @see BaseProcessTransactionCommandImpl
 * @see ProcessTransactionCommandImpl
 */

public interface ProcessTransactionCommand extends Command<Boolean> {}
