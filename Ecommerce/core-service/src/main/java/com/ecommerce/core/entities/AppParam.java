package com.ecommerce.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "APP_PARAM")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppParam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "NAME")
    private String name;

    @Column(name = "NAME_EN")
    private String nameEn;

    @Column(name = "ORGANIZATION_ID")
    private Integer organizationId;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "DELETED")
    private Integer deleted;

    @Column(name = "undoStatus")
    private Integer undoStatus;

    @Column(name = "SELECTED_FORMAT")
    private Integer selectedFormat;

    @Column(name = "created_date")
//    @JsonFormat(pattern="dd-MM-yyyy")
    private Date createdDate;

    @Column(name = "updated_date")
//    @JsonFormat(pattern="dd-MM-yyyy")
    private Date updatedDate;

    @Column(name = "EN_CODE")
    private boolean enCode;
}