package com.ecommerce.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataLabelDTO {

    Integer id;
    String value;
    Integer colIdx;
}
