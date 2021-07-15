package it.gov.pagopa.bpd.point_processor.publisher.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OperationType {

    PAGAMENTO("00", "pagamento"),
    STORNO_PAGAMENTO("01", "storno pagamento"),
    PAGAMENTO_CON_APPLEPAY("02", "pagamento con applePay"),
    PAGAMENTO_CON_GOOGLEPAY("03", "pagamento con GooglePay"),
    USI_FUTURI("xx", "usi futuri");

    private String code;
    private String description;

    @JsonCreator
    public static OperationType getFromCode(String code) {
        for (OperationType operationType : OperationType.values()) {
            if (operationType.getCode().equals(code)) {
                return operationType;
            }
        }
        return USI_FUTURI;
    }

    public String getDescription() {
        return description;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

}
