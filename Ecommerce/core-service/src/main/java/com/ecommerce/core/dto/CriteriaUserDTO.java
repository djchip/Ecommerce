package com.ecommerce.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CriteriaUserDTO {
    private Integer criteriaId;
    private Integer standardId;
    private Integer userId;
    private Integer programId;
    private Integer categoryId;
    private Integer orgId;
}
