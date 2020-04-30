package it.gov.pagopa.bpd.point_processor.service;

import eu.sia.meda.BaseTest;
import it.gov.pagopa.bpd.point_processor.connector.award_period.AwardPeriodRestClient;
import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.OffsetDateTime;
import java.util.Collections;

public class AwardPeriodConnectorServiceTest extends BaseTest {

    @Mock
    private AwardPeriodRestClient awardPeriodRestClientMock;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private AwardPeriodConnectorService awardPeriodConnectorService;

    @Before
    public void initTest() {
        Mockito.reset(awardPeriodRestClientMock);
        awardPeriodConnectorService =
                new AwardPeriodConnectorServiceImpl(awardPeriodRestClientMock);
    }

    @Test
    public void testSave_Ok() {

        try {

            BDDMockito.doReturn(Collections.singletonList(getAwardPeriod()))
                    .when(awardPeriodRestClientMock)
                    .getAwardPeriods(getRequestParam());

            AwardPeriod awardPeriod = awardPeriodConnectorService
                    .getAwardPeriod(getRequestParam());

            Assert.assertEquals(getAwardPeriod(), awardPeriod);
            BDDMockito.verify(awardPeriodRestClientMock, Mockito.atLeastOnce())
                    .getAwardPeriods(Mockito.eq(getRequestParam()));

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void testSave_Ok_ZeroResults() {

        try {

            BDDMockito.doReturn(Collections.emptyList())
                    .when(awardPeriodRestClientMock)
                    .getAwardPeriods(getRequestParam());

            AwardPeriod awardPeriod = awardPeriodConnectorService
                    .getAwardPeriod(getRequestParam());

            Assert.assertNull(awardPeriod);
            BDDMockito.verify(awardPeriodRestClientMock, Mockito.atLeastOnce())
                    .getAwardPeriods(Mockito.eq(getRequestParam()));

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void testSave_KO_Connector() {

        BDDMockito.doAnswer(invocationOnMock -> {
            throw new Exception();
        }).when(awardPeriodRestClientMock)
                .getAwardPeriods(null);

        expectedException.expect(Exception.class);
        awardPeriodConnectorService.getAwardPeriod(null);

        BDDMockito.verify(awardPeriodRestClientMock, Mockito.atLeastOnce())
                .getAwardPeriods(Mockito.any());

    }

    protected AwardPeriod getAwardPeriod() {
        return AwardPeriod.builder()
                .awardPeriodId(1L)
                .startDate(OffsetDateTime.parse("2020-04-01T16:22:45.304Z").toLocalDate())
                .endDate(OffsetDateTime.parse("2020-04-30T16:22:45.304Z").toLocalDate())
                .build();
    }

    protected OffsetDateTime getRequestParam() {
        return OffsetDateTime.parse("2020-04-09T16:22:45.304Z");
    }

}