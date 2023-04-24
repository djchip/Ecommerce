package com.ecommerce.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;

@Entity
@Table(name = "database_object")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatabaseObject extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Expose
    private Integer id;

    @Column(name = "code")
    @Expose
    private String code;

    @Column(name = "name")
    @Expose
    private String name;

    @Column(name = "name_en")
    @Expose
    private String nameEn;

    @Column(name = "description")
    @Expose
    private String description;

    @Column(name = "description_en")
    @Expose
    private String descriptionEn;

    @Column(name = "status")
    @Expose
    private Integer status;

    @Column(name = "created_by")
    @Expose
    private String createdBy;

    @Column(name = "updated_by")
    @Expose
    private String updatedBy;

    @Column(name = "db_name")
    @Expose
    private String dbName;

//    @JsonIgnore
//    @ManyToMany(mappedBy = "objects")
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    List<Form> forms;
}
