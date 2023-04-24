

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
public class TreeNodeDTOProof {

    Integer id;
    String name;
    List<PreTreeNodeByIdDTO> children;
    Boolean isSelected = false;
    final Integer level = 1;

    public TreeNodeDTOProof(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
