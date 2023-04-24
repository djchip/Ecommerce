package com.ecommerce.core.dto;

import com.ecommerce.core.entities.ExhibitionFile;
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
public class ExhDTO implements Serializable {
    private Integer id;
    private String proofName;
    private String oldProofName;
    private String proofNameEn;
    private Integer documentType;
    private Integer programId;
    private String numberSign;
    private String releaseDate;
    private String signer;
    private Integer field;
    private Integer releaseBy;
    private String description;
    private String descriptionEn;
    private String note;
    private String noteEn;
    private String proofCode;
    private List<Unit> unit;
    private List<ExhibitionFile> listExhFile;
    private Integer exhFile;
    private Integer status;
}
