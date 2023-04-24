package com.ecommerce.core.service;

import com.ecommerce.core.entities.RoleMenus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface RoleMenusService extends IRootService<RoleMenus> {

    Page<RoleMenus> doSearch(Pageable paging);

    RoleMenus searchRoleMenusWithId(Integer menuId, Integer rolesId);

}
