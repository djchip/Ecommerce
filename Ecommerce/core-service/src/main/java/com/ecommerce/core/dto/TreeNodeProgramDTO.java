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
public class TreeNodeProgramDTO {
    Integer id;
    String name;
    List<TreeNodeStandardDTO> children; //List Standard by program
    Boolean isSelected = true;
    final Integer level = 1;

    public TreeNodeProgramDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
