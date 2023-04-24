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
@Table(name = "criteria")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Criteria {
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

    @Column(name = "created_date")
    @JsonFormat(pattern="dd-MM-yyyy")
    @Expose
    private Date createdDate;

    @Column(name = "updated_date")
    @JsonFormat(pattern="dd-MM-yyyy")
    @Expose
    private Date updatedDate;

    @Column(name = "standard_id")
    @Expose
    private Integer standardId;

    @Column(name = "program_id")
    @Expose
    private Integer programId;

    @Column(name = "order_cri")
    @Expose
    private Integer orderCri;

    @Column(name = "org_id")
    @Expose
    private Integer organizationId;

    @Column(name = "[delete]")
    @Expose
    private Integer delete;

    @Column(name = "undoStatus")
    @Expose
    private Integer undoStatus;

    @Column(name = "categories_id")
    @Expose
    private Integer categoryId;


    @ManyToMany
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinTable(
            name = "criteria_user",
            joinColumns = @JoinColumn(name = "criteriaId", insertable = true, updatable = true),
            inverseJoinColumns = @JoinColumn(name = "userId", insertable = true, updatable = true))
    @Expose
    List<UserInfo> userInfos;
}
