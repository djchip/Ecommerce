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
public class FormCopyDTO implements Serializable {
    private Integer id;
    private String name;
    private String nameEn;
    private Integer yearOfApplication;
    private List<Unit> units;
    private Date uploadTime;
}
