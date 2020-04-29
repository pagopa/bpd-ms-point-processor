package it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"idTrxAcquirer", "acquirerCode", "trxDate"}, callSuper = false)
public class WinningTransaction {

    @NotNull
    Integer idTrxAcquirer;

    @NotNull
    @NotBlank
    @Size(max = 20)
    String acquirerCode;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    OffsetDateTime trxDate;

    @Size(max = 64)
    String hpan;

    @Size(max = 5)
    String operationType;

    @Size(max = 5)
    String circuitType;

    Integer idTrxIssuer;

    Integer correlationId;

    BigDecimal amount;

    @Size(max = 3)
    String amountCurrency;

    @Size(max = 5)
    String mcc;

    @Size(max = 40)
    String mccDescription;

    BigDecimal score;

    Boolean awardedTransaction;

    Long awardPeriodId;

    Integer acquirerId;

    Integer merchantId;

}
