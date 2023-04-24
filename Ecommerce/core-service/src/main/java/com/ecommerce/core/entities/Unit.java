package com.ecommerce.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "UNIT")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @Expose
    private Integer id;


    @Column(name = "UNIT_NAME")
    @Expose
    private String unitName;


    @Column(name = "UNIT_NAME_EN")
    @Expose
    private String unitNameEn;


    @Column(name = "UNIT_CODE")
    @Expose
    private String unitCode;


    @Column(name = "DESCRIPTION")
    @Expose
    private String description;


    @Column(name = "DESCRIPTION_EN")
    @Expose
    private String descriptionEn;


    @Column(name = "CREATED_BY")
    @Expose
    private String createdBy;


    @Column(name = "UPDATED_BY")
    @Expose
    private String updatedBy;


    @Column(name = "EMAIL")
    @Expose
    private String email;


    @Column(name = "DELETED")
    @Expose
    private Integer deleted;


    @Column(name = "UNDO_STATUS")
    @Expose
    private Integer undoStatus;


    @Column(name = "CLASSIFY")
    @Expose
    private Integer classify;

    @Column(name = "created_date")
    @Expose
//    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date createdDate;

    @Column(name = "updated_date")
    @Expose
//    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date updatedDate;

    @JsonIgnore
    @ManyToMany(mappedBy = "unit")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    Set<Proof> likes;

    @JsonIgnore
    @ManyToMany(mappedBy = "unit")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    Set<Document> documents;

    @JsonIgnore
    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    List<UserInfo> users;

    @JsonIgnore
    @ManyToMany(mappedBy = "units")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    List<Form> forms;
}
