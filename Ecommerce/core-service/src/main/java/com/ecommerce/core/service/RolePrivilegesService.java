package com.ecommerce.core.service;

import java.util.List;

import com.ecommerce.core.dto.RolePrivilegesDTO;
import com.ecommerce.core.dto.TreeNodeDTO;
import com.ecommerce.core.entities.RolePrivileges;

public interface RolePrivilegesService extends IRootService<RolePrivileges>{
	
	List<TreeNodeDTO> setupTreePrivileges();
	void updatePrivileges(RolePrivilegesDTO dto);
	List<RolePrivileges> searchPrivilegesByRoleId(Integer roleId);
	List<Integer> getPrivilegesByRoleId(Integer roleId);
}
