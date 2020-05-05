package it.gov.pagopa.bpd.point_processor.connector.winning_transaction;

import eu.sia.meda.connector.meda.MedaInternalConnector;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.WinningTransaction;
import org.springframework.stereotype.Service;

/**
 * Class extension of {@link MedaInternalConnector}, used as
 * connector for the REST service related to {@link WinningTransaction}
 */

@Service
class WinningTransactionRestConnector
        extends MedaInternalConnector<WinningTransaction, WinningTransaction, WinningTransaction, WinningTransaction> {

}
