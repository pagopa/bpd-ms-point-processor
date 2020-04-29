package it.gov.pagopa.bpd.point_processor.connector.award_period;

import eu.sia.meda.connector.meda.MedaInternalConnector;
import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
class AwardPeriodRestConnector
        extends MedaInternalConnector<Void, List<AwardPeriod>, Void, List<AwardPeriod>> {

}
