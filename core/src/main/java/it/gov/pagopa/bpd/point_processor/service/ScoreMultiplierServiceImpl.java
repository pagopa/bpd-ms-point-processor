package it.gov.pagopa.bpd.point_processor.service;

import it.gov.pagopa.bpd.point_processor.connector.jpa.MCC_CategoryDAO;
import it.gov.pagopa.bpd.point_processor.connector.jpa.model.MCC_Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
class ScoreMultiplierServiceImpl implements ScoreMultiplierService {

    private final MCC_CategoryDAO mcc_categoryDAO;

    public BigDecimal getScoreMultiplier(String mcc) {
        //MCC_Category mcc_category = mcc_categoryDAO.findByMerchantCategoryCodes_Mcc(mcc);
        MCC_Category mcc_category = new MCC_Category();
        mcc_category.setMultiplierScore(BigDecimal.ONE);
        return mcc_category != null && mcc_category.getMultiplierScore() != null ?
                mcc_category.getMultiplierScore() : BigDecimal.ZERO;
    }

}
