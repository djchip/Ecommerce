package com.ecommerce.core.service;

import com.ecommerce.core.dto.RoleMenuDTO;

import java.util.List;

public interface RoleMenuSerrvice {
    void updateRoleMenu(RoleMenuDTO dto);
    List<Integer> getMenuByRoleId(Integer roleId);
}
