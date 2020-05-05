package it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model;

import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.WinningTransactionRestClient;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


/**
 * Resource model for the data recovered through {@link WinningTransactionRestClient}
 */


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"idTrxAcquirer", "acquirerCode", "trxDate"}, callSuper = false)
public class WinningTransaction {

    Integer idTrxAcquirer;

    String acquirerCode;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    OffsetDateTime trxDate;

    String hpan;

    String operationType;

    String circuitType;

    Integer idTrxIssuer;

    Integer correlationId;

    BigDecimal amount;

    String amountCurrency;

    String mcc;

    String mccDescription;

    BigDecimal score;

    Long awardPeriodId;

    Integer acquirerId;

    Integer merchantId;

}
