package com.ecommerce.core.dto;

import com.ecommerce.core.entities.DocumentFile;
import com.ecommerce.core.entities.Unit;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateDocumentDTO implements Serializable {
    Integer id;
    String documentNumber;
    String documentName;
    String documentNameEn;
    Integer documentType;
    String releaseDate;
    String signer;
    Integer field;
    Integer releaseBy;
    String description;
    String descriptionEn;
    List<DocumentFile> file;
    List<Unit> unit;
}
