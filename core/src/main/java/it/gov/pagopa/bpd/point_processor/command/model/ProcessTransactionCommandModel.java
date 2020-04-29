package it.gov.pagopa.bpd.point_processor.command.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.header.Headers;

/**
 *  Model containing the inbound message data
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessTransactionCommandModel {

    private Transaction payload;
    private Headers headers;

}
