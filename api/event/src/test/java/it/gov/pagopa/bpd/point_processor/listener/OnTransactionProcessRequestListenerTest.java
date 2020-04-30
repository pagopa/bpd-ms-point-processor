package it.gov.pagopa.bpd.point_processor.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sia.meda.event.service.ErrorPublisherService;
import eu.sia.meda.eventlistener.BaseEventListenerTest;
import it.gov.pagopa.bpd.point_processor.command.ProcessTransactionCommand;
import it.gov.pagopa.bpd.point_processor.command.model.Transaction;
import it.gov.pagopa.bpd.point_processor.factory.ProcessTransactionCommandModelFactory;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Test class for the OnTransactionSaveRequestListener method
 */

@Import({OnTransactionProcessRequestListener.class})
@TestPropertySource(
        locations = "classpath:config/testTransactionRequestListener.properties",
        properties = {
                "listeners.eventConfigurations.items.OnTransactionProcessRequestListener.bootstrapServers=${spring.embedded.kafka.brokers}"
        })
public class OnTransactionProcessRequestListenerTest extends BaseEventListenerTest {


    @Value("${listeners.eventConfigurations.items.OnTransactionProcessRequestListener.topic}")
    private String topic;

    @SpyBean
    ObjectMapper objectMapperSpy;

    @SpyBean
    OnTransactionProcessRequestListener onTransactionProcessRequestListenerSpy;

    @SpyBean
    ProcessTransactionCommandModelFactory processTransactionCommandModelFactorySpy;

    @MockBean
    BeanFactory beanFactoryMock;

    @MockBean
    ProcessTransactionCommand processTransactionCommandMock;


    @Before
    public void setUp() throws Exception {

        Mockito.reset(
                onTransactionProcessRequestListenerSpy,
                processTransactionCommandModelFactorySpy,
                beanFactoryMock, processTransactionCommandMock);
        Mockito.doReturn(true).when(processTransactionCommandMock).execute();

    }

    @Override
    protected Object getRequestObject() {
        return Transaction.builder()
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
                .build();
    }

    @Override
    protected String getTopic() {
        return topic;
    }

    @Override
    protected void verifyInvocation(String json) {
        try {
            BDDMockito.verify(processTransactionCommandModelFactorySpy, Mockito.atLeastOnce())
                    .createModel(Mockito.any());
            BDDMockito.verify(objectMapperSpy, Mockito.atLeastOnce())
                    .readValue(Mockito.anyString(), Mockito.eq(Transaction.class));
            BDDMockito.verify(processTransactionCommandMock, Mockito.atLeastOnce()).execute();
        } catch (Exception e){
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Override
    protected ErrorPublisherService getErrorPublisherService() {
        return null;
    }

}