package it.gov.pagopa.bpd.point_processor.service;

import eu.sia.meda.event.BaseEventConnector;
import eu.sia.meda.event.service.BaseErrorPublisherService;
import eu.sia.meda.event.transformer.ErrorEventRequestTransformer;
import eu.sia.meda.event.transformer.SimpleEventResponseTransformer;
import it.gov.pagopa.bpd.point_processor.config.PointProcessorErrorPublisherConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the TransactionManagerErrorPublisherService, extends the BaseErrorPublisherService,
 * the connector has the responsibility to send error related messages on the appropriate queue
 */

@Service
class PointProcessorErrorPublisherServiceImpl
        extends BaseErrorPublisherService
        implements PointProcessorErrorPublisherService {

    private final PointProcessorErrorPublisherConnector pointProcessorErrorPublisherConnector;

    @Autowired
    public PointProcessorErrorPublisherServiceImpl(
            PointProcessorErrorPublisherConnector pointProcessorErrorPublisherConnector,
            ErrorEventRequestTransformer errorEventRequestTransformer,
            SimpleEventResponseTransformer simpleEventResponseTransformer) {
        super(errorEventRequestTransformer, simpleEventResponseTransformer);
        this.pointProcessorErrorPublisherConnector = pointProcessorErrorPublisherConnector;
    }

    @Override
    protected BaseEventConnector<byte[], Boolean, byte[], Void> getErrorPublisherConnector() {
        return pointProcessorErrorPublisherConnector;
    }

}
