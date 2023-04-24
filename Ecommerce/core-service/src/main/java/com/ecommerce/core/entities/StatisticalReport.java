package com.ecommerce.core.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "statistical_report")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticalReport extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "NAME_EN")
    private String nameEn;

    @Column(name = "IS_NO")
    private Boolean isNo;

    @Column(name = "ITEMS")
    private String items;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "FORM_ID")
    private Integer formId;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;
}
