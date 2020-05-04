package it.gov.pagopa.bpd.point_processor;

import eu.sia.meda.event.BaseEventConnector;
import org.springframework.stereotype.Service;

/**
 * Class extending the MEDA BaseEventConnector, is responsible for calling a Kafka outbound channel with messages
 * containing content in byte[] format class
 */

@Service
public class PointProcessorErrorPublisherConnector
        extends BaseEventConnector<byte[], Boolean, byte[], Void> {

}
