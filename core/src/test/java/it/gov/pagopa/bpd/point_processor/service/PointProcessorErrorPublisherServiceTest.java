package it.gov.pagopa.bpd.point_processor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sia.meda.BaseTest;
import eu.sia.meda.event.transformer.ErrorEventRequestTransformer;
import eu.sia.meda.event.transformer.SimpleEventResponseTransformer;
import it.gov.pagopa.bpd.point_processor.PointProcessorErrorPublisherConnector;
import it.gov.pagopa.bpd.point_processor.connector.winning_transaction.model.WinningTransaction;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class PointProcessorErrorPublisherServiceTest extends BaseTest {

    @Mock
    private PointProcessorErrorPublisherConnector pointProcessorErrorPublisherConnectorMock;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Spy
    private ErrorEventRequestTransformer errorEventRequestTransformer;

    @Spy
    SimpleEventResponseTransformer simpleEventResponseTransformer;

    @Autowired
    ObjectMapper objectMapper;

    private PointProcessorErrorPublisherService pointProcessorErrorPublisherService;

    @Before
    public void initTest() {
        Mockito.reset(pointProcessorErrorPublisherConnectorMock);
        pointProcessorErrorPublisherService =
                new PointProcessorErrorPublisherServiceImpl(
                        pointProcessorErrorPublisherConnectorMock,
                        errorEventRequestTransformer,
                        simpleEventResponseTransformer);
    }

    @Test
    public void testSendError_Ok() {

        try {

            BDDMockito.doReturn(true)
                    .when(pointProcessorErrorPublisherConnectorMock)
                    .call(Mockito.eq(getPayload()), Mockito.any(), Mockito.any());

            Boolean callResult = pointProcessorErrorPublisherService
                    .publishErrorEvent(getPayload(), null, null);

            Assert.assertTrue(callResult);
            BDDMockito.verify(pointProcessorErrorPublisherConnectorMock, Mockito.atLeastOnce())
                    .call(Mockito.eq(getPayload()),Mockito.any(),Mockito.any());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void testSave_KO_Connector() {

        BDDMockito.doAnswer(invocationOnMock -> {
            throw new Exception();
        }).when(pointProcessorErrorPublisherConnectorMock)
          .call(Mockito.eq(getPayload()), Mockito.any(), Mockito.any());


        expectedException.expect(Exception.class);
        pointProcessorErrorPublisherService
                .publishErrorEvent(getPayload(), null, null);

        BDDMockito.verify(pointProcessorErrorPublisherConnectorMock, Mockito.atLeastOnce())
                .call(Mockito.eq(getPayload()),Mockito.any(),Mockito.any());

    }

    @SneakyThrows
    protected byte[] getPayload(){
        return objectMapper.writeValueAsBytes(WinningTransaction.builder()
                .idTrxAcquirer(1)
                .acquirerCode("001")
                .trxDate(OffsetDateTime.parse("2020-04-09T16:22:45.304Z"))
                .amount(BigDecimal.valueOf(1313.13))
                .operationType("00")
                .hpan("hpan")
                .merchantId(0)
                .circuitType("00")
                .mcc("813")
                .idTrxIssuer(0)
                .amountCurrency("833")
                .correlationId(1)
                .acquirerId(0)
                .awardPeriodId(1L)
                .score(BigDecimal.ONE)
                .build());
    }

}