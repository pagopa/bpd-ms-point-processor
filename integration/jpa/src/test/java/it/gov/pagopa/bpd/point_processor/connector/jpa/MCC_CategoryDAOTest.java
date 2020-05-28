package it.gov.pagopa.bpd.point_processor.connector.jpa;

import it.gov.pagopa.bpd.common.connector.jpa.BaseJpaIntegrationTest;
import it.gov.pagopa.bpd.point_processor.connector.jpa.model.MCC_Category;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class MCC_CategoryDAOTest extends BaseJpaIntegrationTest {

    @Autowired
    private MCC_CategoryDAO dao;

    @Test
    public void testFindByMerchantCategoryCodes_Mcc_OK() {
        MCC_Category mcc_category = dao.findByMerchantCategoryCodes_Mcc("0000");
        Assert.assertNotNull(mcc_category);
        Assert.assertEquals(getCategory(), mcc_category);
    }

    @Test
    public void testFindByMerchantCategoryCodes_Mcc_OK_NoCategory() {
        MCC_Category mcc_category = dao.findByMerchantCategoryCodes_Mcc("0001");
        Assert.assertNotNull(mcc_category);
    }

    @Test
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
