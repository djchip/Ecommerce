package com.ecommerce.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CriteriaDTO {
    Integer id;
    String name;
    String nameEn;
    String code;
    String description;
    String create_by;
    String update_by;
    Date createdDate;
    Date updatedDate;
    String standarName;
    String orgramName;
    Integer standardId;
    Integer programId;
    Integer delete;
    Integer organizationId;
    String orgNameEng;
    String dirNameEng;
    Integer categoryId;
    String categoryName;
    String categoryNameEn;


}
