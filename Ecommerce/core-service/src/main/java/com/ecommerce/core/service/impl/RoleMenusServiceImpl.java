package com.ecommerce.core.service.impl;

import com.ecommerce.core.entities.RoleMenus;
import com.ecommerce.core.repositories.RolesMenuRepository;
import com.ecommerce.core.service.RoleMenusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleMenusServiceImpl implements RoleMenusService {
    @Autowired
    RolesMenuRepository repo;
;

    @Override
    public RoleMenus create(RoleMenus entity) {
        return repo.save(entity);
    }

    @Override
    public RoleMenus retrieve(Integer id) {
        Optional<RoleMenus> roleMenus = repo.findById(id);

        return roleMenus.get();
    }

    @Override
    public void update(RoleMenus entity, Integer id) {
        repo.save(entity);
    }

    @Override
    public void delete(Integer id) {
        repo.deleteById(id);
    }

    @Override
    public Page<RoleMenus> doSearch(Pageable paging) {
        return repo.doSearch(paging);
    }

    @Override
    public RoleMenus searchRoleMenusWithId(Integer menuId, Integer rolesId) {
        return repo.searchRoleMenusWithId(menuId,rolesId);
    }

}
