package com.ecommerce.core.entities;

import com.google.gson.annotations.Expose;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "organization")
@Setter
@Getter
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @Expose
    private Integer id;
    
    @Column(name = "NAME")
    @Expose
    private String name;
    
    @Column(name = "CATEGORY_ID")

    private int categoryId;

    @Column(name = "DESCRIPTION")
    @Expose
    private String description;

    @Column(name = "encode_by")
    @Expose
    private boolean encode;
    
    @Column(name="CREATED_BY")
    @Expose
    private String createdBy;
    
    @Column(name="UPDATED_BY")
    @Expose
    private String updatedBy;

    @Column(name = "DESCRIPTION_EN")
    @Expose
    private String descriptionEn;

    @Column(name = "NAME_EN")
    @Expose
    private String nameEn;

    @Column(name = "DELETED")
    @Expose
    private Integer deleted;

    @Column(name = "UNDO_STATUS")
    @Expose
    private Integer undoStatus;

    @Column(name = "created_date")
    @Expose
//    @JsonFormat(pattern="dd-MM-yyyy")
    private Date createdDate;

    @Column(name = "updated_date")
    @Expose
//    @JsonFormat(pattern="dd-MM-yyyy")
    private Date updatedDate;


    @ManyToMany
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinTable(
            name = "organization_categories",
            joinColumns = @JoinColumn(name = "organization_id", insertable = true, updatable = true),
            inverseJoinColumns = @JoinColumn(name = "categories_id", insertable = true, updatable = true))
    @Expose
    List<Categories> categories;

}
