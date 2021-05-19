package it.gov.pagopa.bpd.point_processor.service;

import eu.sia.meda.BaseTest;
import it.gov.pagopa.bpd.point_processor.connector.award_period.AwardPeriodRestClient;
import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;
import it.gov.pagopa.bpd.point_processor.exception.AwardPeriodNotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Class for unit-testing {@link AwardPeriodConnectorService}
 */
public class AwardPeriodConnectorServiceTest extends BaseTest {

    public static final OffsetDateTime TODAY = OffsetDateTime.now();

    @Mock
    private AwardPeriodRestClient awardPeriodRestClientMock;

    private AwardPeriodConnectorService awardPeriodConnectorService;


    @Before
    public void initTest() {
        Mockito.reset(awardPeriodRestClientMock);
        awardPeriodConnectorService =
                new AwardPeriodConnectorServiceImpl(awardPeriodRestClientMock);

        BDDMockito.when(awardPeriodRestClientMock.findAll(AwardPeriodRestClient.Ordering.START_DATE))
                .thenAnswer(invocationOnMock -> awardPeriodRestClientMock.findAll().stream()
                        .sorted(Comparator.comparing(AwardPeriod::getStartDate))
                        .collect(Collectors.toList()));
    }


    @Test
    public void testSave_Ok() throws AwardPeriodNotFoundException {
        BDDMockito.doReturn(Collections.singletonList(getAwardPeriod(0)))
                .when(awardPeriodRestClientMock)
                .findAll();


        AwardPeriod awardPeriod = awardPeriodConnectorService
                .getAwardPeriod(getTrxDate());

        Assert.assertEquals(getAwardPeriod(0), awardPeriod);
        BDDMockito.verify(awardPeriodRestClientMock, Mockito.atLeastOnce())
                .findAll(Mockito.any(AwardPeriodRestClient.Ordering.class));
    }

    protected AwardPeriod getAwardPeriod(long bias) {
        return AwardPeriod.builder()
                .awardPeriodId(bias)
                .startDate(LocalDate.of(TODAY.getYear(), TODAY.getMonth(), 1)
                        .minusMonths(bias))
                .endDate(LocalDate.of(TODAY.getYear(), TODAY.getMonth(), 1)
                        .minusMonths(bias)
                        .plusMonths(1)
                        .minusDays(1))
                .gracePeriod(TODAY.getDayOfMonth())
                .status("ACTIVE")
                .build();
    }

    protected OffsetDateTime getTrxDate() {
        return TODAY;
    }

    @Test(expected = AwardPeriodNotFoundException.class)
    public void testSave_Ok_ZeroResults() throws AwardPeriodNotFoundException {
        BDDMockito.doReturn(Collections.emptyList())
                .when(awardPeriodRestClientMock)
                .findAll();

        AwardPeriod awardPeriod = awardPeriodConnectorService
                .getAwardPeriod(getTrxDate());

        Assert.assertNull(awardPeriod);
        BDDMockito.verify(awardPeriodRestClientMock, Mockito.atLeastOnce())
                .findAll(Mockito.any(AwardPeriodRestClient.Ordering.class));
    }

    @Test(expected = AwardPeriodNotFoundException.class)
    public void testSave_KO_Connector() throws AwardPeriodNotFoundException {

        BDDMockito.doAnswer(invocationOnMock -> {
            throw new AwardPeriodNotFoundException("Test");
        }).when(awardPeriodRestClientMock)
                .findAll();

        awardPeriodConnectorService.getAwardPeriod(TODAY);

        BDDMockito.verify(awardPeriodRestClientMock, Mockito.atLeastOnce())
                .findAll(Mockito.any(AwardPeriodRestClient.Ordering.class));
    }

    @Test
    public void testSave_OK_TwoAwards_First_Period() throws AwardPeriodNotFoundException {
        AwardPeriod firstPeriod = getAwardPeriod(0);
        AwardPeriod secondPeriod = getAwardPeriod(1);

        BDDMockito.doReturn(Arrays.asList(firstPeriod, secondPeriod))
                .when(awardPeriodRestClientMock)
                .findAll();

        AwardPeriod awardPeriod = awardPeriodConnectorService
                .getAwardPeriod(getTrxDate());

        Assert.assertEquals(getAwardPeriod(0), awardPeriod);
        BDDMockito.verify(awardPeriodRestClientMock, Mockito.atLeastOnce())
                .findAll(Mockito.any(AwardPeriodRestClient.Ordering.class));
    }

    @Test
    public void testSave_OK_TwoAwards_First_Period_Grace() throws AwardPeriodNotFoundException {
        AwardPeriod firstPeriod = getAwardPeriod(0);
        AwardPeriod secondPeriod = getAwardPeriod(1);

        BDDMockito.doReturn(Arrays.asList(firstPeriod, secondPeriod))
                .when(awardPeriodRestClientMock)
                .findAll();

        AwardPeriod awardPeriod = awardPeriodConnectorService
                .getAwardPeriod(getTrxDate().minusMonths(1));

        Assert.assertEquals(getAwardPeriod(1), awardPeriod);
        BDDMockito.verify(awardPeriodRestClientMock, Mockito.atLeastOnce())
                .findAll(Mockito.any(AwardPeriodRestClient.Ordering.class));
    }

    @Test
    public void testSave_OK_TwoAwards_Second_Period() throws AwardPeriodNotFoundException {
        AwardPeriod firstPeriod = getAwardPeriod(0);
        AwardPeriod secondPeriod = getAwardPeriod(1);

        BDDMockito.doReturn(Arrays.asList(firstPeriod, secondPeriod))
                .when(awardPeriodRestClientMock)
                .findAll();

        AwardPeriod awardPeriod = awardPeriodConnectorService
                .getAwardPeriod(getTrxDate().minusMonths(1));

        Assert.assertEquals(secondPeriod, awardPeriod);
        BDDMockito.verify(awardPeriodRestClientMock, Mockito.atLeastOnce())
                .findAll(AwardPeriodRestClient.Ordering.START_DATE);
    }

//    @Test
//    public void myTest() {
//        AwardPeriod ap1 = new AwardPeriod();
//        ap1.setStartDate(LocalDate.of(2020, 12, 01));
//
//        AwardPeriod ap2 = getAwardPeriod(1);
//        AwardPeriod ap3 = getAwardPeriod(2);
//
//        BDDMockito.doReturn(Arrays.asList(ap1, ap2), ap3)
//                .when(awardPeriodRestClientMock)
//                .findAll();
//
//        awardPeriodConnectorService.getAwardPeriod()
//    }

}