package it.gov.pagopa.bpd.point_processor.mapper;

import it.gov.pagopa.bpd.point_processor.command.model.Transaction;
import it.gov.pagopa.bpd.point_processor.publisher.model.WinningTransaction;
import it.gov.pagopa.bpd.point_processor.publisher.model.enums.OperationType;
import org.springframework.stereotype.Service;

/**
 * Class to be used to map a {@link Transaction} from an* {@link WinningTransaction}
 */

@Service
public class TransactionMapper {

    /**
     * @param transaction instance of an  {@link Transaction}, to be mapped into a {@link WinningTransaction}
     * @return {@link Transaction} instance from the input transaction,
     */
    public WinningTransaction map(
            Transaction transaction) {

        WinningTransaction winningTransaction = null;

        if (transaction != null) {
            winningTransaction = WinningTransaction.builder().build();
            winningTransaction.setTrxDate(transaction.getTrxDate());
            winningTransaction.setTerminalId(transaction.getTerminalId());
            winningTransaction.setMerchantId(transaction.getMerchantId());
            winningTransaction.setIdTrxIssuer(transaction.getIdTrxIssuer());
            winningTransaction.setMcc(transaction.getMcc());
            winningTransaction.setIdTrxAcquirer(transaction.getIdTrxAcquirer());
            winningTransaction.setHpan(transaction.getHpan());
            winningTransaction.setCorrelationId(transaction.getCorrelationId());
            winningTransaction.setCircuitType(transaction.getCircuitType());
            winningTransaction.setBin(transaction.getBin());
            winningTransaction.setAmountCurrency(transaction.getAmountCurrency());
            winningTransaction.setAcquirerCode(transaction.getAcquirerCode());
            winningTransaction.setAcquirerId(transaction.getAcquirerId());
            winningTransaction.setFiscalCode(transaction.getFiscalCode());
            winningTransaction.setAmount(transaction.getAmount());
            winningTransaction.setOperationType(OperationType.getFromCode(transaction.getOperationType()));
        }

        return winningTransaction;

    }

}
