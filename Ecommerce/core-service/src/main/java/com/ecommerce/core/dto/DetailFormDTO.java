package com.ecommerce.core.dto;

import com.ecommerce.core.entities.Unit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailFormDTO implements Serializable {
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

    private Integer startTitle;

    private Integer formKey;

    private String pathFileDatabase;

    private List<Unit> units;
}
