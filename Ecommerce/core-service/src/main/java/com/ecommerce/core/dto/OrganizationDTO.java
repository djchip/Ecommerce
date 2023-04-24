package com.ecommerce.core.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationDTO {
    Integer id;
    String name;
    String nameEn;
    String description;
    String descriptionEn;
    boolean encode;
    Integer categoryId;
    String categoryName;
    String categoryNameEn;
    String createdBy;
    Date createdDate;
    String updatedBy;
    Date updatedDate;

    public OrganizationDTO(Integer id, String name,  String nameEn, String description,String descriptionEn, boolean encode, Integer categoryId, String categoryName, String categoryNameEn, String createdBy, Date createdDate, String updatedBy, Date updatedDate) {
        this.id = id;
        this.name = name;
        this.nameEn=nameEn;
        this.description = description;
        this.descriptionEn=descriptionEn;
        this.encode = encode;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryNameEn=categoryNameEn;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.updatedBy = updatedBy;
        this.updatedDate = updatedDate;
    }
}
