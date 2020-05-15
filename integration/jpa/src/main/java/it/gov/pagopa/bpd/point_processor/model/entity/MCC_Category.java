package it.gov.pagopa.bpd.point_processor.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@ToString(exclude = {"merchantCategoryCodes"})
@EqualsAndHashCode(of = {"mccCategoryId"}, callSuper = false)
@Table(name = "bpd_mcc_category")
public class MCC_Category implements Serializable {

    @Id
    @Column(name = "mcc_category_id_s")
    private String mccCategoryId;

    @Column(name = "mcc_category_description_s")
    private String mccCategoryDescription;

    @Column(name="multiplier_score_d")
    private BigDecimal multiplierScore;

    @OneToMany(fetch = FetchType.LAZY, mappedBy="mcc_category")
    private List<MCC_Category_Rel> merchantCategoryCodes;

}
