package com.ecommerce.core.repositories;

import com.ecommerce.core.dto.MenuObjDTO;
import com.ecommerce.core.dto.TreeNodeDTO;
import com.ecommerce.core.entities.Menus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenusRepository extends JpaRepository<Menus, Integer> {

    @Query(value = "SELECT distinct u FROM Menus u ORDER BY u.menuParentId, u.sortBy")
    List<Menus> findWithOrder();
    
    @Query(value = "SELECT distinct u FROM Menus u WHERE u.menuParentId is not null ORDER BY u.menuParentId, u.sortBy")
    List<Menus> findAllChilds();

//	@Query(value = "SELECT u FROM Menus u, RoleMenu r WHERE u.id = r.menuId AND r.roleId IN (?1) ORDER BY u.menuLevel desc, u.id")
//	List<Menus> findWithOrderAndRoleIds(List<Integer> roleIds);
	
	@Query(value = "SELECT distinct(u) FROM Menus u, Privileges p, RolePrivileges r WHERE u.id = p.menuID AND r.id.privilegesId = p.id AND r.id.roleId IN (?1) ORDER BY u.menuParentId desc , u.sortBy  ")
	List<Menus> findWithOrderAndRoleIds(List<Integer> roleIds);

//    @Query(value = "SELECT m from Menus m where m.menuLevel = 1 OR (m.menuLevel = 2 AND m.url = '')", nativeQuery = false)
    @Query(value = "SELECT m from Menus m where m.menuLevel = 1 or (m.menuLevel = 2 AND m.url = '') ORDER BY m.sortBy", nativeQuery = false)
    List<Menus> getMenuParrent();

    @Query(value = "SELECT m from Menus m where m.url = '' ", nativeQuery = false)
    List<Menus> getMenuParrentUrl();

    @Query(value = "SELECT new com.ecommerce.core.dto.MenuObjDTO( u.id,u.menuCode, u.menuName, u.menuLevel, b.menuName, u.url, u.translate )  FROM Menus u LEFT JOIN Menus b ON ( u.menuParentId = b.id )  WHERE 1=1 AND (?1 IS NULL OR lower(u.menuCode) like %?1% OR lower(u.menuName) like %?1%) ORDER BY u.updatedDate desc ")
    Page<MenuObjDTO> doSearch(String keyword, Pageable paging);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Menus u WHERE u.id = ?1 OR u.pathToRoot like %?2%")
    void deleteMenu(Integer id, String pathToRoot);

    @EntityGraph(attributePaths = {Menus.Fields.menuCode})
    public Optional<Menus> findByMenuCode(String menuCode);

    @EntityGraph(attributePaths = {Menus.Fields.menuName})
    public Optional<Menus> findByMenuName(String menuName);

    @Query(value = "select new com.ecommerce.core.dto.TreeNodeDTO(c.id, c.menuName) from  Menus c where c.menuLevel=1 ORDER BY c.id ")
    List<TreeNodeDTO> getListTreeNode();


}
