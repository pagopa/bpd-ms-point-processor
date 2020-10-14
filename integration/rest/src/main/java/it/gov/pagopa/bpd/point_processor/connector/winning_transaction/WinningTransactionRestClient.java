package it.gov.pagopa.bpd.point_processor.connector.winning_transaction;

import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.WinningTransaction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * WinningTransaction Rest Client
 */
@FeignClient(name = "${rest-client.winning-transaction.serviceCode}", url = "${rest-client.winning-transaction.base-url}")
public interface WinningTransactionRestClient {

    /**
     * Method for calling on the endpoint for saving a {@link WinningTransaction} instance
     *
     * @param winningTransaction Instance of {@link WinningTransaction} to be saved
     * @return Instance of {@link WinningTransaction} containing the returing resource of the saving process
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    WinningTransaction saveWinningTransaction(
            @Valid @RequestBody WinningTransaction winningTransaction);


}
