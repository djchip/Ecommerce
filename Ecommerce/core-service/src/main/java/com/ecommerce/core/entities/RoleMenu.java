package com.ecommerce.core.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;

@Entity
@Table(name = "role_menu")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "MENU_ID")
    private Integer menuId;
    @Column(name = "ROLE_ID")
    private Integer roleId;

    public RoleMenu(Integer menuId, Integer roleId) {
        this.menuId = menuId;
        this.roleId = roleId;
    }
}
