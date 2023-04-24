package com.ecommerce.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
@Data
@SuperBuilder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoriesDTO {
    Integer id;
    String name;
    String nameEn;
    String description;
    String createdBy;
    String note;
    private Date createdDate;
    Integer undoStatus;
    Integer delete;
    String updatedBy;
    Date updatedDate;

    public CategoriesDTO(Integer id, String name,String nameEn, String description, String createdBy, String note, Date createdDate, Integer undoStatus, Integer delete, String updatedBy, Date updatedDate) {
        this.id = id;
        this.name = name;
        this.nameEn=nameEn;
        this.description = description;
        this.createdBy = createdBy;
        this.note = note;
        this.createdDate = createdDate;
        this.undoStatus = undoStatus;
        this.delete = delete;
        this.updatedBy = updatedBy;
        this.updatedDate = updatedDate;
    }
}
