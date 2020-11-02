package it.gov.pagopa.bpd.point_processor.publisher;

import eu.sia.meda.event.BaseEventConnectorTest;
import it.gov.pagopa.bpd.point_processor.publisher.PointProcessorErrorPublisherConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.nio.charset.StandardCharsets;

@Import({PointProcessorErrorPublisherConnector.class})
@TestPropertySource(
        locations = "classpath:config/testPointProcessorErrorPublisher.properties",
        properties = {
                "connectors.eventConfigurations.items.PointProcessorErrorPublisherConnector.bootstrapServers=${spring.embedded.kafka.brokers}"
        })
public class PointProcessorErrorPublisherConnectorTest
        extends BaseEventConnectorTest<byte[], Boolean, byte[], Void, PointProcessorErrorPublisherConnector> {

    @Value("${connectors.eventConfigurations.items.PointProcessorErrorPublisherConnector.topic}")
    private String topic;

    @Autowired
    private PointProcessorErrorPublisherConnector errorPublisherConnector;

    @Override
    protected PointProcessorErrorPublisherConnector getEventConnector() {
        return errorPublisherConnector;
    }

    @Override
    protected byte[] getRequestObject() {
        return "prova".getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected String getTopic() {
        return topic;
    }
}
