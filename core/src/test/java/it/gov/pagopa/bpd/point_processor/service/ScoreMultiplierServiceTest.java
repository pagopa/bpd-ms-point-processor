package it.gov.pagopa.bpd.point_processor.service;

import eu.sia.meda.BaseTest;
import it.gov.pagopa.bpd.point_processor.MCC_CategoryDAO;
import it.gov.pagopa.bpd.point_processor.model.entity.MCC_Category;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;

public class ScoreMultiplierServiceTest extends BaseTest {

    @Mock
    private MCC_CategoryDAO mcc_categoryDAOMock;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ScoreMultiplierService scoreMultiplierService;

    @Before
    public void initTest() {
        Mockito.reset(mcc_categoryDAOMock);
        scoreMultiplierService = new ScoreMultiplierServiceImpl(mcc_categoryDAOMock);
    }

    @Test
    public void test_getScore_OK() {

        BDDMockito.doReturn(getDAOEntity()).when(mcc_categoryDAOMock)
                .findByMerchantCategoryCodes_Mcc(Mockito.eq("0000"));
        BigDecimal score =scoreMultiplierService.getScoreMultiplier("0000");
        Assert.assertNotNull(score);
        Assert.assertEquals(BigDecimal.valueOf(0.10), score);
        BDDMockito.verify(mcc_categoryDAOMock).findByMerchantCategoryCodes_Mcc(Mockito.eq("0000"));
    }

    @Test
    public void test_getScore_OK_ZeroForNullEntity() {
        BDDMockito.doReturn(null).when(mcc_categoryDAOMock)
                .findByMerchantCategoryCodes_Mcc(Mockito.eq("0001"));
        BigDecimal score =scoreMultiplierService.getScoreMultiplier("0001");
        Assert.assertNotNull(score);
        Assert.assertEquals(BigDecimal.ZERO, score);
        BDDMockito.verify(mcc_categoryDAOMock).findByMerchantCategoryCodes_Mcc(Mockito.eq("0001"));
    }

    @Test
    public void test_getScore_OK_ZeroForNullScore() {
        MCC_Category mcc_category = getDAOEntity();
        mcc_category.setMultiplierScore(null);
        BDDMockito.doReturn(mcc_category).when(mcc_categoryDAOMock)
                .findByMerchantCategoryCodes_Mcc(Mockito.eq("0002"));
        BigDecimal score =scoreMultiplierService.getScoreMultiplier("0002");
        Assert.assertNotNull(score);
        Assert.assertEquals(BigDecimal.ZERO, score);
        BDDMockito.verify(mcc_categoryDAOMock).findByMerchantCategoryCodes_Mcc(Mockito.eq("0002"));
    }

    @Test
    public void test_getScore_KO_Exception() {
        MCC_Category mcc_category = getDAOEntity();
        BDDMockito.doAnswer(invocationOnMock -> {
            throw new Exception("error");
        }).when(mcc_categoryDAOMock)
                .findByMerchantCategoryCodes_Mcc(Mockito.eq("0003"));
        expectedException.expect(Exception.class);
        scoreMultiplierService.getScoreMultiplier("0003");
        BDDMockito.verify(mcc_categoryDAOMock).findByMerchantCategoryCodes_Mcc(Mockito.eq("0003"));
    }

    private MCC_Category getDAOEntity() {
        MCC_Category mcc_category = new MCC_Category();
        mcc_category.setMccCategoryDescription("test");
        mcc_category.setMultiplierScore(BigDecimal.valueOf(0.10));
        mcc_category.setMccCategoryId("1");
        return mcc_category;
    }

}