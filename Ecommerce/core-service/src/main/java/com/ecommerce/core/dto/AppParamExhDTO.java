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
public class AppParamExhDTO {
    Integer id;
    String code;
    String name;
    String nameEn;
    String createdBy;
    String updatedBy;
    Date createdDate;
    Date updatedDate;
    Integer organizationId;
    String orgName;
    String orgNameEn;
}
