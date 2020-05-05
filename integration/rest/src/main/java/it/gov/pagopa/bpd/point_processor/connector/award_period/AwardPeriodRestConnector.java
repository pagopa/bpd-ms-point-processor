package it.gov.pagopa.bpd.point_processor.connector.award_period;

import eu.sia.meda.connector.meda.MedaInternalConnector;
import it.gov.pagopa.bpd.point_processor.connector.award_period.model.AwardPeriod;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Class extension of {@link MedaInternalConnector}, used as
 * connector for the REST service related to {@link AwardPeriod}
 */

@Service
class AwardPeriodRestConnector
        extends MedaInternalConnector<Void, List<AwardPeriod>, Void, List<AwardPeriod>> {

}
