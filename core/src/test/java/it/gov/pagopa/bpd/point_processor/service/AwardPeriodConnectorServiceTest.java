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
import java.util.Arrays;
import java.util.Collections;

/**
 * Class for unit-testing {@link AwardPeriodConnectorService}
 */
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
                    .getActiveAwardPeriods();

            AwardPeriod awardPeriod = awardPeriodConnectorService
                    .getAwardPeriod(getTrx());

            Assert.assertEquals(getAwardPeriod(), awardPeriod);
            BDDMockito.verify(awardPeriodRestClientMock, Mockito.atLeastOnce())
                    .getActiveAwardPeriods();

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
                    .getActiveAwardPeriods();

            AwardPeriod awardPeriod = awardPeriodConnectorService
                    .getAwardPeriod(getTrx());

            Assert.assertNull(awardPeriod);
            BDDMockito.verify(awardPeriodRestClientMock, Mockito.atLeastOnce())
                    .getActiveAwardPeriods();

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
                .getActiveAwardPeriods();

        expectedException.expect(Exception.class);
        awardPeriodConnectorService.getAwardPeriod(null);

        BDDMockito.verify(awardPeriodRestClientMock, Mockito.atLeastOnce())
                .getActiveAwardPeriods();

    }

    @Test
    public void testSave_OK_TwoAwards_First_Period() {

        try {

            AwardPeriod firstPeriod = getAwardPeriod();
            AwardPeriod secondPeriod = getAwardPeriod();
            secondPeriod.setStartDate(OffsetDateTime.parse("2020-05-01T16:22:45.304Z").toLocalDate());
            secondPeriod.setEndDate(OffsetDateTime.parse("2020-05-30T16:22:45.304Z").toLocalDate());

            BDDMockito.doReturn(Arrays.asList(firstPeriod, secondPeriod))
                    .when(awardPeriodRestClientMock)
                    .getActiveAwardPeriods();

            AwardPeriod awardPeriod = awardPeriodConnectorService
                    .getAwardPeriod(getTrx());

            Assert.assertEquals(getAwardPeriod(), awardPeriod);
            BDDMockito.verify(awardPeriodRestClientMock, Mockito.atLeastOnce())
                    .getActiveAwardPeriods();

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void testSave_OK_TwoAwards_First_Period_Grace() {

        try {
            OffsetDateTime trxDate = OffsetDateTime.parse("2020-05-04T16:22:45.304Z");

            AwardPeriod firstPeriod = getAwardPeriod();
            AwardPeriod secondPeriod = getAwardPeriod();
            secondPeriod.setStartDate(OffsetDateTime.parse("2020-05-01T16:22:45.304Z").toLocalDate());
            secondPeriod.setEndDate(OffsetDateTime.parse("2020-05-30T16:22:45.304Z").toLocalDate());

            BDDMockito.doReturn(Arrays.asList(firstPeriod, secondPeriod))
                    .when(awardPeriodRestClientMock)
                    .getActiveAwardPeriods();

            AwardPeriod awardPeriod = awardPeriodConnectorService
                    .getAwardPeriod(trxDate);

            Assert.assertEquals(getAwardPeriod(), awardPeriod);
            BDDMockito.verify(awardPeriodRestClientMock, Mockito.atLeastOnce())
                    .getActiveAwardPeriods();

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void testSave_OK_TwoAwards_Second_Period() {

        try {
            OffsetDateTime trxDate = OffsetDateTime.parse("2020-05-11T16:22:45.304Z");

            AwardPeriod firstPeriod = getAwardPeriod();
            AwardPeriod secondPeriod = getAwardPeriod();
            secondPeriod.setAwardPeriodId(2L);
            secondPeriod.setStartDate(OffsetDateTime.parse("2020-05-01T16:22:45.304Z").toLocalDate());
            secondPeriod.setEndDate(OffsetDateTime.parse("2020-05-30T16:22:45.304Z").toLocalDate());

            BDDMockito.doReturn(Arrays.asList(firstPeriod, secondPeriod))
                    .when(awardPeriodRestClientMock)
                    .getActiveAwardPeriods();

            AwardPeriod awardPeriod = awardPeriodConnectorService
                    .getAwardPeriod(trxDate);

            Assert.assertEquals(secondPeriod, awardPeriod);
            BDDMockito.verify(awardPeriodRestClientMock, Mockito.atLeastOnce())
                    .getActiveAwardPeriods();

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    protected AwardPeriod getAwardPeriod() {
        return AwardPeriod.builder()
                .awardPeriodId(1L)
                .startDate(OffsetDateTime.parse("2020-04-01T16:22:45.304Z").toLocalDate())
                .endDate(OffsetDateTime.parse("2020-04-30T16:22:45.304Z").toLocalDate())
                .gracePeriod(5)
                .build();
    }


    protected OffsetDateTime getTrx() {
        return OffsetDateTime.parse("2020-04-08T16:22:45.304Z");
    }

}