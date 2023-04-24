package com.ecommerce.core.repositories;

import com.ecommerce.core.dto.PrivilegesRoleDTO;
import com.ecommerce.core.entities.RolesPrivileges;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

@Component
public interface RolesPrivilegesRepository extends JpaRepository<RolesPrivileges, Integer> {

//    @Query(value = "SELECT rm from RolesPrivileges rm")
    @Query(value = "SELECT P from Privileges P JOIN RolesPrivileges  RP on P.id = RP.rolesPrivilegesId join Menus M on P.menuID = M.id")
    Page<RolesPrivileges> doSearch(Pageable paging);

    @Query(value = "SELECT new com.ecommerce.core.dto.PrivilegesRoleDTO(RP.rolesPrivilegesId,P.id,P.name,M,P.method,P.url,P.createdBy,P.updatedBy,P.createdDate,P.updatedDate,P.status, roles.roleCode,roles.roleName) from Privileges P JOIN RolesPrivileges  RP on P.id = RP.privileges.id join Menus M on P.menuID = M.id join  Roles  roles on roles.id = RP.roles.id")
    Page<PrivilegesRoleDTO> getMenuFromRolePrivileges(Pageable paging);



    @Query(value = "SELECT rm FROM RolesPrivileges rm where rm.privileges.id = ?1 and rm.roles.id = ?2")
    RolesPrivileges searchRolePrivilegesWithId(Integer menuId, Integer rolesId);
}
