package com.ecommerce.core.repositories;

import java.util.List;
import java.util.Optional;

import com.ecommerce.core.dto.TreeNodeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ecommerce.core.dto.PreTreeNodeDTO;
import com.ecommerce.core.entities.Privileges;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegesRepository extends JpaRepository<Privileges, Integer> {

    @Query(value = "SELECT r FROM Privileges r WHERE r.status <> 0 AND 1=1 AND (?1 IS NULL OR lower(r.code) like %?1% OR lower(r.name) like %?1%) ORDER BY r.updatedDate desc")
    Page<Privileges> doSearch(String keyword, Pageable paging);

    @EntityGraph(attributePaths = {Privileges.Fields.code})
    public Optional<Privileges> findByCode(String roleCode);

    @EntityGraph(attributePaths = {Privileges.Fields.name})
    public Optional<Privileges> findByName(String roleName);

    @Query(value = "select r from Privileges r where r.id <> ?1 and r.name = ?2")
    public Optional<Privileges> findByNameAndId(Integer id, String roleName);
    
    @Query(value = "select new com.ecommerce.core.dto.PreTreeNodeDTO(r.id, m.menuName, r.name) from Privileges r, Menus m where r.menuID = m.id AND r.status = 1 ORDER BY m.menuParentId, m.sortBy")
    public List<PreTreeNodeDTO> getListPrivilegesMenu();
    
    @Query(value = "select new com.ecommerce.core.dto.TreeNodeDTO(c.id, c.menuName) from  Menus c where c.menuLevel=1 ORDER BY c.id ")
    List<TreeNodeDTO> getListTreeNode();

    @Query(value = "SELECT m.URL, group_concat(p.METHOD) FROM privileges p, menus m, role_privileges r WHERE m.ID = p.MENU_ID AND p.ID = r.privileges_id AND  r.role_id in ?1 GROUP BY m.URL", nativeQuery = true)
    List<Object[]> getListPrivilegesAction(List<Integer> roleId);

}
