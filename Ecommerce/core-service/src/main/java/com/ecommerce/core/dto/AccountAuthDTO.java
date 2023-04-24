package com.ecommerce.core.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AccountAuthDTO {
    private Integer id;

    private String loginId;

    private String accountName;

    private String email;

    private String password;

    private String phone;

    private Integer role;

    private Integer accountType;

    private String accountStatus;

    private Date createdDate;

    private Date editDate;

    private Integer employeeId;

    private Integer departmentId;

    private String modifiedBy;

    private String createdBy;
}
