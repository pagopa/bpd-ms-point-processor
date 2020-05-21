package it.gov.pagopa.bpd.point_processor.service;

import eu.sia.meda.BaseTest;
import it.gov.pagopa.bpd.point_processor.connector.award_period.OldAwardPeriodRestClient;
import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;

/**
 * Class for unit-testing {@link AwardPeriodConnectorService}
 */
public class AwardPeriodConnectorServiceTest extends BaseTest {

    @Mock
    private OldAwardPeriodRestClient oldAwardPeriodRestClientMock;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private AwardPeriodConnectorService awardPeriodConnectorService;

    @Before
    public void initTest() {
        Mockito.reset(oldAwardPeriodRestClientMock);
        awardPeriodConnectorService =
                new AwardPeriodConnectorServiceImpl(oldAwardPeriodRestClientMock);
    }

    @Test
    public void testSave_Ok() {

        try {

            BDDMockito.doReturn(Collections.singletonList(getAwardPeriod()))
                    .when(oldAwardPeriodRestClientMock)
                    .getAwardPeriods();

            AwardPeriod awardPeriod = awardPeriodConnectorService
                    .getAwardPeriod(getRequestParam());

            Assert.assertEquals(getAwardPeriod(), awardPeriod);
            BDDMockito.verify(oldAwardPeriodRestClientMock, Mockito.atLeastOnce())
                    .getAwardPeriods();

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void testSave_Ok_ZeroResults() {

        try {

            BDDMockito.doReturn(Collections.emptyList())
                    .when(oldAwardPeriodRestClientMock)
                    .getAwardPeriods();

            AwardPeriod awardPeriod = awardPeriodConnectorService
                    .getAwardPeriod(getRequestParam());

            Assert.assertNull(awardPeriod);
            BDDMockito.verify(oldAwardPeriodRestClientMock, Mockito.atLeastOnce())
                    .getAwardPeriods();

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void testSave_KO_Connector() {

        BDDMockito.doAnswer(invocationOnMock -> {
            throw new Exception();
        }).when(oldAwardPeriodRestClientMock)
                .getAwardPeriods();

        expectedException.expect(Exception.class);
        awardPeriodConnectorService.getAwardPeriod(null);

        BDDMockito.verify(oldAwardPeriodRestClientMock, Mockito.atLeastOnce())
                .getAwardPeriods();

    }

    @Test
    public void testSave_OK_TwoAwards_First_Period() {

        try {

            AwardPeriod firstPeriod = getAwardPeriod();
            AwardPeriod secondPeriod = getAwardPeriod();
            secondPeriod.setStartDate(OffsetDateTime.parse("2020-05-01T16:22:45.304Z").toLocalDate());
            secondPeriod.setEndDate(OffsetDateTime.parse("2020-05-30T16:22:45.304Z").toLocalDate());

            BDDMockito.doReturn(Arrays.asList(firstPeriod, secondPeriod))
                    .when(oldAwardPeriodRestClientMock)
                    .getAwardPeriods();

            AwardPeriod awardPeriod = awardPeriodConnectorService
                    .getAwardPeriod(getRequestParam());

            Assert.assertEquals(getAwardPeriod(), awardPeriod);
            BDDMockito.verify(oldAwardPeriodRestClientMock, Mockito.atLeastOnce())
                    .getAwardPeriods();

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void testSave_OK_TwoAwards_First_Period_Grace() {

        try {

            LocalDate date = LocalDate.parse("2020-05-05");

            AwardPeriod firstPeriod = getAwardPeriod();
            AwardPeriod secondPeriod = getAwardPeriod();
            secondPeriod.setStartDate(OffsetDateTime.parse("2020-05-01T16:22:45.304Z").toLocalDate());
            secondPeriod.setEndDate(OffsetDateTime.parse("2020-05-30T16:22:45.304Z").toLocalDate());

            BDDMockito.doReturn(Arrays.asList(firstPeriod, secondPeriod))
                    .when(oldAwardPeriodRestClientMock)
                    .getAwardPeriods();

            AwardPeriod awardPeriod = awardPeriodConnectorService
                    .getAwardPeriod(date);

            Assert.assertEquals(getAwardPeriod(), awardPeriod);
            BDDMockito.verify(oldAwardPeriodRestClientMock, Mockito.atLeastOnce())
                    .getAwardPeriods();

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void testSave_OK_TwoAwards_Second_Period() {

        try {

            LocalDate date = LocalDate.parse("2020-05-09");

            AwardPeriod firstPeriod = getAwardPeriod();
            AwardPeriod secondPeriod = getAwardPeriod();
            secondPeriod.setAwardPeriodId(2L);
            secondPeriod.setStartDate(OffsetDateTime.parse("2020-05-01T16:22:45.304Z").toLocalDate());
            secondPeriod.setEndDate(OffsetDateTime.parse("2020-05-30T16:22:45.304Z").toLocalDate());

            BDDMockito.doReturn(Arrays.asList(firstPeriod, secondPeriod))
                    .when(oldAwardPeriodRestClientMock)
                    .getAwardPeriods();

            AwardPeriod awardPeriod = awardPeriodConnectorService
                    .getAwardPeriod(date);

            Assert.assertEquals(secondPeriod, awardPeriod);
            BDDMockito.verify(oldAwardPeriodRestClientMock, Mockito.atLeastOnce())
                    .getAwardPeriods();

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

    protected LocalDate getRequestParam() {
        return LocalDate.parse("2020-04-09");
    }

}