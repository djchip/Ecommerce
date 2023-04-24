package com.ecommerce.core.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;

@Entity
@Table(name = "organization_categories")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationCategories {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "organization_id")
    private Integer OrganizationId;

    @Column(name = "categories_id")
    private Integer CategoriesId;
}
