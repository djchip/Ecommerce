package com.ecommerce.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_info")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Expose
    private Integer id;

    @Column(name = "USERNAME")
    @Expose
    private String username;

    @Column(name = "PASSWORD")
    @Expose
    private String password;

    @Column(name = "FULLNAME")
    @Expose
    private String fullname;

    @Column(name = "EMAIL")
    @Expose
    private String email;

    @Column(name = "ROLE_ID")
    @Expose
    private Integer roleId;

    @Column(name = "PHONE_NUMBER")
    @Expose
    private String phoneNumber;

    @Column(name = "CREATED_BY")
    @Expose
    private String createdBy;

    @Column(name = "UPDATED_BY")
    @Expose
    private String updatedBy;

    @Column(name = "STATUS")
    @Expose
    private Integer status;

    @Column(name = "VERIFY_TOKEN")
    @Expose
    private String verifyToken;

    @Column(name = "VERIFY_TOKEN_CREATED_DATE")
    @Expose
    private LocalDateTime verifyTokenCreatedDate;

    @Column(name = "RESET_PASSWORD_TOKEN")
    @Expose
    private String resetPasswordToken;

    @Column(name = "UNDO_STATUS")
    @Expose
    private Integer undoStatus;

    @Column(name = "DELETED")
    @Expose
    private Integer deleted;

    @ManyToMany
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", insertable = true, updatable = true),
            inverseJoinColumns = @JoinColumn(name = "role_id", insertable = true, updatable = true))
    @Expose
    List<Roles> role;
    
    @ManyToOne 
    @JoinColumn(name = "unit_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Expose
    private Unit unit;
}
