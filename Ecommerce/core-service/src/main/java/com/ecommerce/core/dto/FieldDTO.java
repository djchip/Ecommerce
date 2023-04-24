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
public class FieldDTO {
    Integer id;
    String fieldName;
    String fieldNameEn;
    Integer fieldStatus;
    String createdBy;
    String updatedBy;
    Date createdDate;
    Date updatedDate;
    Integer group;
}
