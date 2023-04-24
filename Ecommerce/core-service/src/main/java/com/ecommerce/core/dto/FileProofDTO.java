package com.ecommerce.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileProofDTO {
    Integer id;
    Integer parentId;
    String name;
    Boolean isFile = true;
    final Integer level = 4;

    public FileProofDTO(Integer id, Integer parentId, String name) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
    }
}
