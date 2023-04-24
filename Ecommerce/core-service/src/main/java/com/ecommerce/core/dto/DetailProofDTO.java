package com.ecommerce.core.dto;

import com.ecommerce.core.entities.ExhibitionFile;
import com.ecommerce.core.entities.Unit;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailProofDTO {
    ProofDTO entity;
    List<ExhibitionFile> file;
    List<Unit> unit;
    String programName;
    String programNameEn;
}
