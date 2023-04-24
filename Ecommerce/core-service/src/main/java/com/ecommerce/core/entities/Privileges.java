package com.ecommerce.core.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PRIVILEGES")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Privileges extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "NAME")
    private String name;

    @Column(name = "MENU_ID")
    private Integer menuID;

    @Column(name = "METHOD")
    private String method;

    @Column(name = "URL")
    private String url;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "UPDATED_BY")

    private String updatedBy;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    @Column(name = "UPDATED_DATE")

    private LocalDateTime updatedDate;

    @Column(name = "STATUS")
    private Integer status;
}
