package com.ecommerce.core.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;

@Entity
@Table(name = "roles_privileges")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolesPrivileges {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roles_privileges_id")
    private Integer rolesPrivilegesId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "privileges_id")
    private Privileges privileges;

    //    @Column(name = "roles_id")
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "roles_id")
    private Roles roles;

}
