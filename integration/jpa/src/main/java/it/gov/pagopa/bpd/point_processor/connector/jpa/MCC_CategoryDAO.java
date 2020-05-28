package it.gov.pagopa.bpd.point_processor.connector.jpa;

import it.gov.pagopa.bpd.common.connector.jpa.CrudJpaDAO;
import it.gov.pagopa.bpd.point_processor.connector.jpa.model.MCC_Category;
import org.springframework.stereotype.Repository;

/**
 * Data Access Object to manage all CRUD operations to the database
 */
@Repository
public interface MCC_CategoryDAO extends CrudJpaDAO<MCC_Category, String> {

    MCC_Category findByMerchantCategoryCodes_Mcc(String mcc);

}
