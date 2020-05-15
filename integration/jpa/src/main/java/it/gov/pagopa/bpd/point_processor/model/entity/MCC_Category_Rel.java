package it.gov.pagopa.bpd.point_processor.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"mcc"}, callSuper = false)
@Table(name = "bpd_mcc_category_rel")
public class MCC_Category_Rel implements Serializable {

    @Id
    @Column(name = "mcc_s")
    private String mcc;

    @Column(name= "mcc_category_id_s")
    private String mccCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mcc_category_id_s",
                referencedColumnName = "mcc_category_id_s",
                insertable = false, updatable = false)
    MCC_Category mcc_category;

}




