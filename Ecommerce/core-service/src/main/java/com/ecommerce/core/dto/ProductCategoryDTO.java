package com.ecommerce.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductCategoryDTO implements Serializable {
    private Integer id;
    private String categoryName;
    private String slug;
    private Integer parentId;
    private String parentCategory;
    private String description;
    private boolean active;
    private String updatedBy;
    private String createdBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
