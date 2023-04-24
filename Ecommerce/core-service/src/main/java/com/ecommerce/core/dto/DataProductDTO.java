package com.ecommerce.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataProductDTO {
    private Integer id;
    private String name;
    private String slug;
    private String description;
    private String brand;
    private BigDecimal price;
    private String image;
    private Boolean hasFreeShipping;
    private Double rating;
}
