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
public class UnitDTO {
    Integer id;
    String unitName;
    String unitNameEn;
    String unitCode;
    String description;
    String descriptionEn;
    String createdBy;
    String updatedBy;
    String email;
    Data createdDate;
    Date updatedDate;
}
