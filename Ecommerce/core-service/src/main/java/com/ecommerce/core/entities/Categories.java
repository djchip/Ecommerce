package com.ecommerce.core.entities;

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
@Table(name = "Categories")
@Data
public class Categories {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @Expose
    private Integer id;
    @Column(name = "NAME")
    @Expose
    @NotNull
    private String name;
    @Column(name = "CREATED_DATE")
    @Expose
    private Date createdDate;
    @Column(name = "DESCRIPTION")
    @Expose
    @NotNull
    private String description;
    @Column(name="CREATED_BY")
    @Expose
    private String createdBy;
    @Column(name="UPDATED_BY")
    @Expose
    private String updatedBy;
    @Column(name="UPDATED_DATE")
    @Expose
    private Date updatedDate;
    @Column(name = "NOTE")
    @Expose
    private String note;
    @Column(name = "DESCRIPTION_EN")
    @Expose
    private String descriptionEn;
    @Column(name = "NAME_EN")
    @Expose
    private String nameEn;
    @Column(name = "UNDO_STATUS")
    @Expose

    private Integer undoStatus;

    @Column(name = "DELETED")
    @Expose
    private Integer delete;

    @JsonIgnore
    @ManyToMany(mappedBy = "categories")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    List<Organization> organizations;
}
