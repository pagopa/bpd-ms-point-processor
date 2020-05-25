package it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model;

import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.OldWinningTransactionRestClient;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.enums.OperationType;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


/**
 * Resource model for the data recovered through {@link OldWinningTransactionRestClient}
 */


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"idTrxAcquirer", "acquirerCode", "trxDate"}, callSuper = false)
public class WinningTransaction {

    String idTrxAcquirer;

    String acquirerCode;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    OffsetDateTime trxDate;

    String hpan;

    OperationType operationType;

    String circuitType;

    String idTrxIssuer;

    String correlationId;

    BigDecimal amount;

    String amountCurrency;

    String mcc;

    String mccDescription;

    BigDecimal score;

    Long awardPeriodId;

    String acquirerId;

    String merchantId;

}
