package com.ecommerce.core.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategory category;

    @Column(name = "name")
    private String name;

    @Column(name = "slug ")
    private String slug;

    @Column(name = "description")
    private String description;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "sold")
    private Integer sold; // Số lượng sản phẩm đã bán.

    @Column(name = "featured")
    private Boolean featured; //Sản phẩm nổi bật: 0 - không, 1 - có

    @Column(name = "active")
    private boolean active;

    @Column(name = "units_in_stock")
    private int unitsInStock; //Số lượng sản phẩm còn lại

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "image")
    private String image;

    @Column(name = "rating")
    private Double rating;

}
