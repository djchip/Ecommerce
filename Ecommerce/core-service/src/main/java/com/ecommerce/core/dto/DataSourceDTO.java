package com.ecommerce.core.dto;

import lombok.Data;

@Data
public class DataSourceDTO {
    private Double sumValue;
    private Double avgValue;
    private Integer countValue;

    private String pathFile;
    private Integer numTitle;
    private Integer formKey;
    private Integer startTitle;

}
