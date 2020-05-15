package it.gov.pagopa.bpd.point_processor;

import eu.sia.meda.connector.jpa.config.ArchJPAConfigurationService;
import eu.sia.meda.core.properties.PropertiesManager;
import it.gov.pagopa.bpd.common.BaseJpaIntegrationTest;
import it.gov.pagopa.bpd.point_processor.config.JpaConfig;
import it.gov.pagopa.bpd.point_processor.model.entity.MCC_Category;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@Import({
        JpaConfig.class,
        ArchJPAConfigurationService.class,
        PropertiesManager.class
})
@TestPropertySource(properties = {
        "connectors.jpaConfigurations.connection.show-sql=true",
        "spring.main.allow-bean-definition-overriding=true",
        "connectors.jpaConfigurations.connection.mocked:true",
        "connectors.jpaConfigurations.connection.path:postgres/"
})
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class MCC_CategoryDAOTest extends BaseJpaIntegrationTest {

    @Autowired
    private MCC_CategoryDAO dao;

    @Test
    @Rollback
    @Transactional
    public void testFindByMerchantCategoryCodes_Mcc_OK() {
        MCC_Category mcc_category = dao.findByMerchantCategoryCodes_Mcc("0000");
        Assert.assertNotNull(mcc_category);
        Assert.assertEquals(getCategory(), mcc_category);
    }

    @Test
    @Rollback
    @Transactional
    public void testFindByMerchantCategoryCodes_Mcc_OK_NoCategory() {
        MCC_Category mcc_category = dao.findByMerchantCategoryCodes_Mcc("0001");
        Assert.assertNull(mcc_category);
    }

    @Test
    @Rollback
    @Transactional
    public void testFindByMerchantCategoryCodes_Mcc_OK_NoRel() {
        MCC_Category mcc_category = dao.findByMerchantCategoryCodes_Mcc("0002");
        Assert.assertNull(mcc_category);
    }

    private MCC_Category getCategory() {
        MCC_Category mcc_category = new MCC_Category();
        mcc_category.setMccCategoryId("0");
        mcc_category.setMultiplierScore(BigDecimal.valueOf(0.10));
        mcc_category.setMccCategoryDescription("test");
        return mcc_category;
    }
}
