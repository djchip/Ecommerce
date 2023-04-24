package com.ecommerce.core.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="product_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "path_image")
    private String pathImage;

    @Column(name = "product_id")
    private Integer productId;
}
