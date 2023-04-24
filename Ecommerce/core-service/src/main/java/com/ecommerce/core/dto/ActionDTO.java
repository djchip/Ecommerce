package com.ecommerce.core.dto;

import lombok.Data;

@Data
public class ActionDTO {

    boolean add = false;
    boolean update = false;
    boolean delete = false;
    boolean detail = true;
    boolean search = true;
    boolean lock = false;
}
