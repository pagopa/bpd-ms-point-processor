package it.gov.pagopa.bpd.point_processor.publisher.model;

import it.gov.pagopa.bpd.point_processor.publisher.model.enums.OperationType;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;


/**
 * Resource model for the data published through {@link it.gov.pagopa.bpd.point_processor.publisher.SaveTransactionPublisherConnector}
 */


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"idTrxAcquirer", "acquirerCode", "trxDate"}, callSuper = false)
public class WinningTransaction {

    @NotNull
    @NotBlank
    String idTrxAcquirer;

    @NotNull
    @NotBlank
    @Size(max = 20)
    String acquirerCode;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    OffsetDateTime trxDate;

    @NotNull
    @NotBlank
    String hpan;

    @NotNull
    OperationType operationType;

    @NotNull
    @NotBlank
    @Size(min = 2, max = 2)
    @Pattern(regexp = "[0-9]{2}")
    String circuitType;

    String idTrxIssuer;

    String correlationId;

    @NotNull
    BigDecimal amount;

    @Size(max = 3)
    String amountCurrency;

    @NotNull
    @NotBlank
    String mcc;

    BigDecimal score;

    Long awardPeriodId;

    @NotNull
    @NotBlank
    String acquirerId;

    @NotNull
    @NotBlank
    String merchantId;

    @NotBlank
    @NotNull
    @Pattern(regexp = "([0-9]{6}|[0-9]{8})")
    String bin;

    @NotNull
    @NotBlank
    String terminalId;

    @NotNull
    @NotBlank
    String fiscalCode;

}
