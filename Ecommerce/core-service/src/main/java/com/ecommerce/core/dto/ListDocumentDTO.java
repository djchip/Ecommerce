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
public class ListDocumentDTO {
    Integer id;
    String documentNumber;
    String documentName;
    String documentNameEn;
    Integer documentType;
    String documentTypeName;
    String documentTypeNameEn;
    Date releaseDate;
    String signer;
    Integer field;
    String fieldName;
    String fieldNameEn;
    Integer releaseBy;
    String releaseByName;
    String releaseByNameEn;
    String description;
    String descriptionEn;
    String createdBy;
    LocalDateTime createdDate;
    String updatedBy;
    LocalDateTime updatedDate;

}
