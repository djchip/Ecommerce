package com.ecommerce.core.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;

@Entity
@Table(name = "roles_menu")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleMenus{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_menus_id")
    private Integer roleMenuId;

//    @Column(name = "menu_id")
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "menu_id")
    private Menus menu;

//    @Column(name = "roles_id")
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "roles_id")
    private Roles roles;

}
