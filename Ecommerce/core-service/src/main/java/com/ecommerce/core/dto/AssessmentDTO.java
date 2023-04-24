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
public class AssessmentDTO {
    Integer id;
    String name;
    String nameEn;
    String description;
    String descriptionEn;
    String createdBy;
    Date createdDate;
    String updatedBy;
    Date updatedDate;
    Integer evaluated;
    String file;
    String comment;
     Integer reportType;
    Integer programId;
    String programName;
    String programNameEn;
    Integer directoryId;
    String directoryName;
    String directoryNameEn;
    Integer criteriaId;
    String criteriaName;
    String criteriaNameEn;

}
