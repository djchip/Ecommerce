package com.ecommerce.core.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "file_database")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDatabase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "form_key")
    private Integer formKey;

    @Column(name = "unit_id")
    private Integer unitId;

    @Column(name = "path_file")
    private String pathFile;

    @Column(name = "status")
    private Integer status;
}
