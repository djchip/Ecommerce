package com.ecommerce.core.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;

@Entity
@Table(name = "form_unit")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "form_id")
    private Integer formId;

    @Column(name = "unit_id")
    private Integer unitId;
}



