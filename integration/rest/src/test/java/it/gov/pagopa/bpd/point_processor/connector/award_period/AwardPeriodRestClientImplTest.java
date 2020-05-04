package it.gov.pagopa.bpd.point_processor.connector.award_period;

import eu.sia.meda.connector.meda.ArchMedaInternalConnectorConfigurationService;
import eu.sia.meda.connector.rest.BaseRestConnectorTest;
import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Test class for {@link AwardPeriodRestClient}
 */

@Import({
        AwardPeriodRestClientImpl.class,
        AwardPeriodRestConnector.class,
        ArchMedaInternalConnectorConfigurationService.class
})
@TestPropertySource(
        locations = {
                "classpath:config/AwardPeriodRestConnector.properties"
        },
        properties = {
                "connectors.medaInternalConfigurations.items.AwardPeriodRestConnector.mocked=true",
                "connectors.medaInternalConfigurations.items.AwardPeriodRestConnector.randomMock=false",
                "connectors.medaInternalConfigurations.items.AwardPeriodRestConnector.path=award-periods/findAll"
        })
public class AwardPeriodRestClientImplTest extends BaseRestConnectorTest {

    @Autowired
    AwardPeriodRestClient awardPeriodRestClient;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void getAwardPeriods_Ok_NotEmpty() {
        try {
            List<AwardPeriod> awardPeriods = awardPeriodRestClient
                    .getAwardPeriods(OffsetDateTime.parse("2020-04-10T14:59:59.245Z"));
            Assert.assertFalse(awardPeriods.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void getAwardPeriods_Ok_Empty() {
        try {
            List<AwardPeriod> awardPeriods = awardPeriodRestClient
                    .getAwardPeriods(OffsetDateTime.parse("2020-04-11T14:59:59.245Z"));
            Assert.assertTrue(awardPeriods.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void getAwardPeriods_KO_Null() {
        expectedException.expect(NullPointerException.class);
        awardPeriodRestClient.getAwardPeriods(null);
    }

}