package com.ecommerce.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormDTO {
    private Integer id;

    private String name;

    private String nameEn;

    private String fileName;

    private Date uploadTime;

    private Integer deleted;

    private Integer yearOfApplication;

    private Integer status;

    private String pathFile;

    private Integer numTitle;

    private Integer formKey;

    private String pathFileDatabase;

    private Integer unitId;
}