package com.ecommerce.core.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import com.ecommerce.core.entities.RolePrivileges;
import com.ecommerce.core.entities.RolePrivilegesId;

@Component
public interface RolePrivilegesRepository extends JpaRepository<RolePrivileges, RolePrivilegesId> {

	@Query(value = "SELECT rm FROM RolePrivileges rm where rm.id.roleId = ?1")
    List<RolePrivileges> searchPrivilegesByRoleId(Integer rolesId);
	
	@Query(value ="SELECT rp FROM RolePrivileges rp, UserInfo u, Privileges p WHERE u.roleId = rp.id.roleId "
			+ "AND rp.id.privilegesId = p.id AND u.username = ?1 AND p.method = ?2 AND p.url = ?3")
	public Optional<RolePrivileges> checkAuthorize(String username, String method, String uri);
}
