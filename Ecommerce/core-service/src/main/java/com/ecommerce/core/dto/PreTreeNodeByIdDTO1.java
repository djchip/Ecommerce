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
public class PreTreeNodeByIdDTO1 {

    Integer id;
    Integer parentId;
    String name;
    List<PreTreeNodeByIdDTO1> children;
    Boolean isSelected = true;

    public PreTreeNodeByIdDTO1(Integer id, Integer parentId,String name) {
        this.id = id;
        this.parentId=parentId;
        this.name = name;
    }

}
