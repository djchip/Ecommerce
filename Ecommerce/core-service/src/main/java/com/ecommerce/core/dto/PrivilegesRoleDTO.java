package com.ecommerce.core.dto;

import com.ecommerce.core.entities.Menus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrivilegesRoleDTO {

    private Integer rolesPrivilegesId;

    private Integer id;

    private String name;

    private Menus menuID;

    private String method;

    private String url;

    private String createdBy;

    private String updatedBy;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    private Integer status;

    private String roleCode;

    private String roleName;



}
