package com.ecommerce.core.repositories;

import com.ecommerce.core.entities.RoleMenus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@Component
public interface RolesMenuRepository extends JpaRepository<RoleMenus,Integer> {

    @Query(value = "SELECT rm from RoleMenus rm")
    Page<RoleMenus> doSearch(Pageable paging);

    @Query(value = "SELECT rm FROM RoleMenus rm where rm.menu.id = ?1 and rm.roles.id = ?2")
    RoleMenus searchRoleMenusWithId(Integer menuId, Integer rolesId);
}
