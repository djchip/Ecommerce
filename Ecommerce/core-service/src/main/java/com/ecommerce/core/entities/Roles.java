package com.ecommerce.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "roles")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Roles extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Expose
    private Integer id;

    @Column(name = "ROLE_CODE")
    @Expose
    private String roleCode;

    @Column(name = "ROLE_NAME")
    @Expose
    private String roleName;

    @Expose
    @Column(name = "ROLE_NAME_EN")
    private String roleNameEn;

    @Expose
    @Column(name = "CREATED_BY")
    private String createdBy;

    @Expose
    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Expose
    @Column(name = "STATUS")
    private Integer status;

    @Expose
    @Column(name = "UNDO_STATUS")
    private Integer undoStatus;

    @Expose
    @Column(name = "DELETED")
    private Integer delete;

    @JsonIgnore
    @ManyToMany(mappedBy = "role")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<UserInfo> likes;
}
