package com.ecommerce.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "MENUS")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor
//@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })

public class Menus extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "ID")

    private Integer id;

    @Column(name = "MENU_CODE")
    private String menuCode;

    @Column(name = "MENU_NAME")
    private String menuName;

    @Column(name = "MENU_LEVEL")
    private Integer menuLevel;

    @Column(name = "URL")
    private String url;

    @Column(name = "ICON")
    private String icon;
    
    @Column(name = "SORT_BY")
    private Integer sortBy;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "UPDATED_BY")
    private String updatedBy;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "MENU_PARENT_ID")
    private Integer menuParentId;
    
    @Column(name = "PATH_TO_ROOT")
    private String pathToRoot;

    @Column(name = "TRANSLATE")
    private String translate;
}
