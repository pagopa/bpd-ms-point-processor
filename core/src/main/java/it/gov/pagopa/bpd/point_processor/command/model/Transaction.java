package it.gov.pagopa.bpd.point_processor.command.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Model for the inbound transaction to be processed
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"idTrxAcquirer", "acquirerCode", "trxDate"}, callSuper = false)
public class Transaction {

    @NotNull
    @NotBlank
    String idTrxAcquirer;

    @NotNull
    @NotBlank
    @Size(max = 20)
    String acquirerCode;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    OffsetDateTime trxDate;

    @Size(max = 64)
    String hpan;

    @NotNull
    @NotBlank
    @Size(max = 2)
    String operationType;

    @NotNull
    @NotBlank
    @Size(max = 2)
    String circuitType;

    String idTrxIssuer;

    String correlationId;

    @NotNull
    BigDecimal amount;

    @Size(max = 3)
    String amountCurrency;

    @NotNull
    @NotBlank
    @Size(max = 5)
    String mcc;

    String acquirerId;

    @NotBlank
    @NotNull
    @Pattern(regexp = "([0-9]{6}|[0-9]{8})")
    String bin;

    @NotNull
    @NotBlank
    String terminalId;

    @NotNull
    @NotBlank
    String merchantId;

    @NotNull
    @NotBlank
    String fiscalCode;

}
