package com.ecommerce.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProofDTO {
    Integer id;
    String proofCode;
    String proofName;
    String proofNameEn;
    Integer documentType;
    String documentTypeName;
    String documentTypeNameEn;
    String numberSign;
    LocalDateTime releaseDate;
    String signer;
    Integer field;
    String fieldName;
    String fieldNameEn;
    Integer releaseBy;
    String releaseByName;
    String releaseByNameEn;
    String description;
    String descriptionEn;
    String note;
    String noteEn;
    String standardName;
    String standardNameEn;
    String criteriaName;
    String criteriaNameEn;
    String programName;
    String programNameEn;
    String createdBy;
    Date createdDate;
    String updatedBy;
    Date updatedDate;
    String dirName;
    String dirNameEn;
}
