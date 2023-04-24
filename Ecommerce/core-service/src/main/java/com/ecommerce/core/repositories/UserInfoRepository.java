package com.ecommerce.core.repositories;

import com.ecommerce.core.entities.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {

	@Query(value = "SELECT u FROM UserInfo u WHERE 1=1 AND (?1 IS NULL OR lower(u.username) like %?1% OR lower(u.fullname) like %?1% OR lower(u.email) like %?1%)  and u.deleted <> 1 AND (?2 = -1 OR (SELECT r FROM Roles r WHERE r.id = ?2) member u.role) AND (?3 = -1 OR u.unit.id = ?3) ORDER BY coalesce(u.updatedDate, u.createdDate) desc")
	Page<UserInfo> doSearch(String keyword, Integer roleId, Integer unitId, Pageable paging);

	@Query(value = "SELECT i.ID, i.USERNAME, i.EMAIL, i.FULLNAME, i.PHONE_NUMBER FROM user_info i LEFT JOIN user_role r ON (i.ID = r.user_id) WHERE r.role_id = ?1 and i.deleted != 1  ORDER BY i.FULLNAME ASC", nativeQuery = true)
	List<Object[]> getListUserByRole(Integer roleId);

	@Query(value = "Select f from UserInfo f where f.username = ?1 and f.deleted <> 1")
    public Optional<UserInfo> findByUsername(String userName);

	@Query(value = "Select f from UserInfo f where f.username = ?1 and f.deleted != 1")
	public Optional<UserInfo> findByName(String userName);
	
	@Query(value = "Select f from UserInfo f where f.email = ?1 and f.deleted != 1")
    public Optional<UserInfo> findByEmail(String email);

	@EntityGraph(attributePaths = {UserInfo.Fields.resetPasswordToken})
    public Optional<UserInfo> findByResetPasswordToken(String email);

	@Query(value = "SELECT u FROM UserInfo u WHERE u.unit.id = ?1 AND u.deleted <> 1")
	List<UserInfo> findByUnit(Integer unitId);

	@Query(value = "SELECT u FROM UserInfo u WHERE 1=1 AND u.deleted <> 1")
	List<UserInfo> getAllUser();

//	@Query(value = "SELECT u FROM UserInfo u JOIN UserRole ur ON u.id = ur.userId JOIN Roles r ON ur.roleId = r.id WHERE r.roleCode <> 'ADMIN' AND r.roleCode <> 'Super Admin'")
//	List<UserInfo> getAllUserWithoutAdmin();

	List<UserInfo> findByUsernameAndDeleted(String name, Integer delete);
	List<UserInfo> findByEmailAndAndDeleted(String email, Integer detete);

	List<UserInfo> findByUnitIdAndDeletedNot(Integer id, Integer status);

	@Query(value = "SELECT u FROM UserInfo u WHERE 1=1 and u.deleted <> 1 AND (?1 = -1 OR (SELECT r FROM Roles r WHERE r.id = ?1)  member u.role)")
	List<UserInfo> findByRoleIdAAndAndDeletedNot(Integer id);


	@Query(value = "Select f from UserInfo f where f.username = ?1 and f.deleted != 1")
	List<UserInfo> findByUsername1(String username);

	@Query(value = "Select distinct m.id from user_info u left JOIN user_role r ON (u.ID=r.user_id) left join roles m on(r.role_id = m.ID)  where u.USERNAME =?1  ", nativeQuery = true)
	List<Integer> getUserIdByRolesid(String name);
}
