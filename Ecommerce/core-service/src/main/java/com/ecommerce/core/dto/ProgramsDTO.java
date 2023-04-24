package com.ecommerce.core.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ProgramsDTO {
    private int id;
    private String name;
    private String nameEn;
    private String description;
    private String descriptionEn;
    private String note;
    private String createdBy;
    private Date createdDate;
    private Date updatedDate;
    String organizationName;
    String organizationNameEn;
    private int organizationId;
    private int categoryId;
    String categoryName;
    String categoryNameEn;
    Integer undoStatus;
    Integer delete;
    String updatedBy;

    public ProgramsDTO(Integer categoryId, String categoryName, String categoryNameEn, String descriptionEn, String nameEn, String organizationNameEn, int id, String name, String description, String note, String createdBy, Date createdDate, Date updatedDate, String organizationName, int organizationId, Integer undoStatus, Integer delete, String updatedBy) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryNameEn = categoryNameEn;
        this.descriptionEn = descriptionEn;
        this.nameEn = nameEn;
        this.organizationNameEn = organizationNameEn;
        this.id = id;
        this.name = name;
        this.description = description;
        this.note = note;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.organizationName = organizationName;
        this.organizationId = organizationId;
        this.undoStatus = undoStatus;
        this.delete = delete;
        this.updatedBy = updatedBy;
//        this.organizationNameEn=organizationNameEn;
    }
}
