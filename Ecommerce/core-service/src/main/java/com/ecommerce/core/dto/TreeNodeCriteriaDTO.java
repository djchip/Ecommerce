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
public class TreeNodeCriteriaDTO {
    String id;
    String name;
    Integer parentId;
    final Integer level = 3;
    boolean isSelected = true;

    public TreeNodeCriteriaDTO(String data, String name, Integer parentId) {
        this.id = data;
        this.name = name;
        this.parentId = parentId;
    }
}
