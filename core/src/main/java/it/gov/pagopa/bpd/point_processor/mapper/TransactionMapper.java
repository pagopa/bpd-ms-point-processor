package it.gov.pagopa.bpd.point_processor.mapper;

import it.gov.pagopa.bpd.point_processor.command.model.Transaction;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.WinningTransaction;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Class to be used to map a {@link Transaction} from an* {@link WinningTransaction}
 */

@Service
public class TransactionMapper {

    /**
     *
     * @param transaction
     *              instance of an  {@link Transaction}, to be mapped into a {@link WinningTransaction}
     * @return  {@link Transaction} instance from the input transaction, normalized and with an hashed PAN
     */
    public WinningTransaction map(Transaction transaction, BigDecimal awardScore, Long awardPeriodId) {

        WinningTransaction winningTransaction = null;

        if (transaction != null) {
            winningTransaction = WinningTransaction.builder().build();
            BeanUtils.copyProperties(transaction, winningTransaction);
            winningTransaction.setScore(awardScore);
            winningTransaction.setAwardPeriodId(awardPeriodId);
            winningTransaction.setAwardedTransaction(!awardScore.equals(0L));
        }

        return winningTransaction;

    }

}
