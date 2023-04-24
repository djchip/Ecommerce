package com.ecommerce.core.dto;

import com.ecommerce.core.entities.ProductImages;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO implements Serializable {
    private Integer id;
    private String name;
    private Integer categoryId;
    private String categoryName;
    private String slug;
    private String description;
    private Double discount;
    private BigDecimal unitPrice;
    private Integer sold;
    private Boolean featured;
    private boolean active;
    private int unitsInStock;
    private String updatedBy;
    private String createdBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
//    private List<ProductImages> productImages;

//    public ProductDTO(Integer id, String name, Integer categoryId, String categoryName, String slug, String description, Double discount, BigDecimal unitPrice, Integer sold, Integer featured, boolean active, int unitsInStock, String updatedBy, String createdBy, LocalDateTime createdDate, LocalDateTime updatedDate, List<ProductImages> productImages) {
//        this.id = id;
//        this.name = name;
//        this.categoryId = categoryId;
//        this.categoryName = categoryName;
//        this.slug = slug;
//        this.description = description;
//        this.discount = discount;
//        this.unitPrice = unitPrice;
//        this.sold = sold;
//        this.featured = featured;
//        this.active = active;
//        this.unitsInStock = unitsInStock;
//        this.updatedBy = updatedBy;
//        this.createdBy = createdBy;
//        this.createdDate = createdDate;
//        this.updatedDate = updatedDate;
//        this.productImages = productImages;
//    }
}
