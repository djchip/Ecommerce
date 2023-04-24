package com.ecommerce.core.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Program")
@Data
//@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })

public class Programs{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @Expose
    private Integer id;
    @Column(name = "Program_NAME")
    @NotNull
    @Expose
    private String name;
    @Column(name = "CREATED_DATE")
    @Expose
    @JsonFormat(pattern="dd-MM-yyyy")
    private Date createdDate;
    @Column(name = "DESCRIPTION")
    @NotNull
    @Expose
    private String description;
    @Column(name="CREATED_BY")
    @Expose
    private String createdBy;
    @Column(name="UPDATED_BY")
    @Expose
    private String updatedBy;
    @Column(name="UPDATED_DATE")
    @Expose
    @JsonFormat(pattern="dd-MM-yyyy")
    private Date updatedDate;
    @Column(name = "NOTE")
    @Expose
    private String note;
    
    @Column(name = "organization_id")
    @Expose
    private Integer organizationId;
    @Column(name = "DESCRIPTION_EN")
    @Expose
    private String descriptionEn;
    @Column(name = "NAME_EN")
    @Expose
    private String nameEn;

    @Column(name = "[delete]")
    @Expose
    private Integer delete;
    @Column(name = "undoStatus")
    @Expose
    private Integer undoStatus;

    @Column(name = "category_id")
    @Expose
    private Integer categoryId;

    @JsonIgnore
    @ManyToMany(mappedBy = "programs")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Expose
    List<Directory> directories;
}

