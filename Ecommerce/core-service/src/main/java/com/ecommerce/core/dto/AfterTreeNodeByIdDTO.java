package com.ecommerce.core.dto;

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
public class AfterTreeNodeByIdDTO {
    Integer id;
    Integer staId;
    Integer criteriaId;
//    Integer proofID;
    List<FileProofDTO> listEXH;
    String name;
    boolean check=false;
//    String url;
    List<FileProofDTO> children;
    final Integer level = 3;
    Boolean isFile = false;

    public AfterTreeNodeByIdDTO(Integer id, Integer staId, Integer criteriaId, String name) {
        this.id = id;
        this.staId = staId;
        this.criteriaId = criteriaId;
        this.name = name;
    }

    public AfterTreeNodeByIdDTO(List<FileProofDTO> listEXH) {
        this.listEXH = listEXH;
    }

}
