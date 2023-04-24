package com.ecommerce.core.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "directory")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class Directory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Expose
    private Integer id;

    @Column(name = "name")
    @Expose
    private String name;

    @Column(name = "name_en")
    @Expose
    private String nameEn;

    @Column(name = "code")
    @Expose
    private String code;

    @Column(name = "description")
    @Expose
    private String description;

    @Column(name = "description_en")
    @Expose
    private String descriptionEn;

    @Column(name = "create_by")
    @Expose
    private String create_by;

    @Column(name = "update_by")
    @Expose
    private String update_by;

//    @DateTimeFormat(pattern="dd/MM/yyyy")
    @Column(name = "created_date")
    @Expose
    @JsonFormat(pattern="dd-MM-yyyy")
    private Date createdDate;

//    @DateTimeFormat(pattern="dd/MM/yyyy")
    @Column(name = "updated_date")
    @Expose
    @JsonFormat(pattern="dd-MM-yyyy")
    private Date updatedDate;

    @Column(name = "program_id")
    @Expose
    private Integer programId;

    @Column(name = "order_dir")
    @Expose
    private Integer orderDir;

    @Column(name = "org_id")
    @Expose
    private Integer organizationId;

    @Column(name = "categories_id")
    @Expose
    private Integer categoryId;


    @Column(name = "[delete]")
    @Expose
    private Integer delete;

    @Column(name = "undoStatus")
    @Expose
    private Integer undoStatus;

    @ManyToMany
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinTable(
            name = "directory_program",
            joinColumns = @JoinColumn(name = "dir_id", insertable = true, updatable = true),
            inverseJoinColumns = @JoinColumn(name = "pro_id", insertable = true, updatable = true))
//    @Expose
    List<Programs> programs;

    @ManyToMany
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinTable(
            name = "standard_user",
            joinColumns = @JoinColumn(name = "standardId", insertable = true, updatable = true),
            inverseJoinColumns = @JoinColumn(name = "userId", insertable = true, updatable = true))
    @Expose
    List<UserInfo> userInfos;
}
