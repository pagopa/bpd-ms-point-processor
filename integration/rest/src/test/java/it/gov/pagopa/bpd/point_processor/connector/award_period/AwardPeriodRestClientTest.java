package it.gov.pagopa.bpd.point_processor.connector.award_period;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.bpd.common.connector.BaseFeignRestClientTest;
import it.gov.pagopa.bpd.point_processor.connector.award_period.config.AwardPeriodRestConnectorConfig;
import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.lang.String.format;

@TestPropertySource(
        locations = "classpath:config/award_period/rest-client.properties",
        properties = "spring.application.name=bpd-ms-point-processor-integration-rest")
@Import({AwardPeriodRestConnectorConfig.class})
public class AwardPeriodRestClientTest extends BaseFeignRestClientTest {


    static {
        SERIVICE_PORT_ENV_VAR_NAME = "BPD_MS_AWARD_PERIOD_PORT";
    }

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AwardPeriodRestClient restClient;

    @Test
    public void getAwardPeriods_Ok_NotEmpty() throws IOException {

        final String awardPeriodId = "1";

        InputStream mockedJson = getClass()
                .getClassLoader()
                .getResourceAsStream(format("award_period/activesMock.json", awardPeriodId));

        final JsonNode jsonNode = objectMapper.readTree(mockedJson);

        stubFor(get(urlEqualTo("/bpd/award-periods/actives"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .withBody(jsonNode.toString())));

        final List<AwardPeriod> actualResponse = restClient.getAwardPeriods();

        Assert.assertFalse(actualResponse.isEmpty());
    }
}