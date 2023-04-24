package com.ecommerce.core.service;

import com.ecommerce.core.dto.PrivilegesRoleDTO;
import com.ecommerce.core.entities.RolesPrivileges;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface RolesPrivilegesService extends IRootService<RolesPrivileges>{

    Page<RolesPrivileges> doSearch(Pageable paging);

    RolesPrivileges searchRolePrivilegesWithId(Integer menuId, Integer rolesId);

    Page<PrivilegesRoleDTO> getMenuFromRolePrivileges(Pageable paging);
}
