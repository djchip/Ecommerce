package com.ecommerce.core.repositories;

import java.util.List;
import java.util.Optional;

import com.ecommerce.core.entities.Roles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Integer> {
    @Query(value = "SELECT r FROM Roles r WHERE r.status<>0 AND 1=1 AND (?1 IS NULL OR lower(r.roleCode) like %?1% OR lower(r.roleName) like %?1% OR lower(r.roleNameEn) like %?1%) and r.delete !=1 ORDER BY r.createdDate desc")
    Page<Roles> doSearch(String keyword, Pageable paging);

    @Query(value = "SELECT r FROM Roles r WHERE r.status = 1 and r.delete != 1")
    List<Roles> getListRoles();
    
    @Query(value = "SELECT r FROM Roles r WHERE r.roleCode = ?1 and r.delete != 1")
    public Optional<Roles> findByRoleCode(String roleCode);

    @Query(value = "SELECT r.roleCode FROM Roles r JOIN UserRole ur ON ur.roleId = r.id JOIN UserInfo u ON u.id = ur.userId WHERE u.deleted <> 1 AND r.delete <> 1 AND u.username = ?1")
    List<String> getListRolesCodeByUsername(String username);

    List<Roles> findByRoleCodeAndDelete(String code, Integer delete);


}
