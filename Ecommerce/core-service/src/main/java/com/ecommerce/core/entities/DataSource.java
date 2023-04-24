package com.ecommerce.core.entities;

import com.ecommerce.core.constants.TypeEnum;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "data_source")
@Data
public class DataSource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "FORM_NAME")
    private String formName;

    @Column(name = "TYPE")
    private TypeEnum type;

    @Column(name = "VALUE")
    private String value;

    @Column(name = "YEAR")
    private Integer year;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    @Column(name = "CREATED_UNIT")
    private Integer createdUnit;

    @Column(name = "ROW_IDX")
    private Integer rowIdx;

    @Column(name = "COL_IDX")
    private Integer colIdx;

    @Column(name = "IS_ACTIVE")
    private Integer isActive;

    @Column(name = "FORM_KEY")
    private Integer formKey;

}
