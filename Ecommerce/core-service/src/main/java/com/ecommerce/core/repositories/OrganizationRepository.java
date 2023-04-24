package com.ecommerce.core.repositories;


import com.ecommerce.core.entities.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface OrganizationRepository  extends JpaRepository<Organization,Integer> {
//    @Query(value = "SELECT u FROM Organization u WHERE 1=1 AND (?1 IS NULL OR lower(u.name) like %?1%) ORDER BY u.updatedDate desc")
//    Page<Organization> doSearch(String keyword, Pageable paging);
//    Programs findByName(String name);

    @Query(value = "SELECT u FROM Organization u LEFT JOIN Categories d ON (u.categoryId = d.id) WHERE 1=1 AND (?1 IS NULL OR lower(u.name) like %?1% ) and u.deleted <> 1 ORDER BY u.updatedDate desc")
    Page<Organization> doSearch(String keyword, Pageable paging);

    @Query(value = "SELECT u FROM Organization u LEFT JOIN Categories d ON u.categoryId = d.id WHERE 1=1 AND u.id = ?1 and u.deleted <> 1  ORDER BY u.updatedDate desc")
    Optional<Organization> finbyID(Integer id);


    @Query(value = "SELECT o FROM Organization o WHERE 1=1 AND o.name = ?1 AND o.deleted <> 1")
    List<Organization> findByNameExisted(String name);

    @Query(value = "SELECT o FROM Organization o WHERE 1=1 AND o.id = ?1 AND o.deleted =?2")
    Organization findByIdAndDeletedNot(Integer id, Integer deleted);

    List<Organization> findByDeletedNot(Integer deleted);

    @Query(value = "SELECT o FROM Organization o WHERE o.deleted <> 1 AND o.encode = false")
    List<Organization> findOrgForCriteria();

    @Query(value = "SELECT o FROM Organization o WHERE 1=1 AND o.deleted <> 1")
    List<Organization> getListOrg();

    List<Organization> findByCategoryIdAndDeletedNot(Integer categoryId, Integer deleted);

    @Query(value = "SELECT o FROM Organization o WHERE 1=1 AND o.deleted <> 1 AND o.name = ?1")
    Organization findOrgByName(String name);

    List<Organization> findByNameAndDeleted(String name,Integer delete);

    boolean existsByCategoryIdAndDeleted(Integer categoryId, Integer deleted);

    boolean existsByIdAndDeleted(Integer id, Integer deleted);

    @Query(value = "SELECT o FROM Organization o WHERE o.deleted <> 1 AND o.id = (SELECT p.organizationId FROM Programs p WHERE p.delete <> 1 AND p.id = ?1)")
    Organization findByProgramId(Integer id);


    @Query(value = "SELECT o.encode FROM Organization o WHERE o.deleted <> 1 ")
    boolean findOrgbyEncode();

}
