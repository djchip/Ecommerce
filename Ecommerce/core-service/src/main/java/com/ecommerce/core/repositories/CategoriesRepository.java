package com.ecommerce.core.repositories;

import com.ecommerce.core.dto.CategoriesDTO;
import com.ecommerce.core.entities.Categories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Integer> {
    @Query(value = "SELECT pm from Categories pm ")
    Page<Categories> findAll(Pageable pageable);

    @Query(value = "SELECT pm from Categories pm where pm.delete !=1")
    List<Categories> listSelectbox();

    Categories findByName(String name);

    @Query(value ="SELECT new com.ecommerce.core.dto.CategoriesDTO(p.id, p.nameEn,p.nameEn, p.descriptionEn, p.createdBy,p.note,p.createdDate, p.delete,p.undoStatus,p.updatedBy,p.updatedDate)  FROM Categories p WHERE 1=1 AND (?1 IS NULL  OR lower(p.nameEn) like %?1% ) and p.delete !=1 ORDER BY p.updatedDate desc")
    Page<CategoriesDTO> doSearchEn(String keyword, Pageable paging);

    @Query(value ="SELECT new com.ecommerce.core.dto.CategoriesDTO(p.id, p.name,p.name, p.description, p.createdBy,p.note,p.createdDate, p.delete, p.undoStatus,p.updatedBy,p.updatedDate)  FROM Categories p WHERE 1=1 AND (?1 IS NULL  OR lower(p.name) like %?1% ) and p.delete !=1 ORDER BY p.updatedDate desc")
    Page<CategoriesDTO> doSearch(String keyword, Pageable paging);

    @Query(value = "SELECT new com.ecommerce.core.dto.CategoriesDTO(p.id, p.name,p.nameEn, p.description, p.createdBy,p.note,p.createdDate, p.delete, p.undoStatus,p.updatedBy,p.updatedDate) FROM Categories p WHERE 1=1 AND p.id = ?1")
    Optional<CategoriesDTO> finbyID(Integer id);

    @Query(value = "SELECT p FROM Categories p WHERE 1=1 AND p.id = ?1")
    Optional<Categories> finbyId(Integer id);

    public Optional<Categories> findByNameAndDeleteNot(String name, Integer deleted);

    List<Categories> findByNameAndDelete(String name, Integer delete);


    @Query(value = "SELECT distinct c FROM Categories c JOIN OrganizationCategories oc ON c.id = oc.CategoriesId JOIN Organization o ON o.id = oc.OrganizationId WHERE c.delete <> 1 AND o.id = ?1")
    List<Categories> getListCategoryByOrgId(Integer id);

    @Query(value = "SELECT  c FROM Categories c  join OrganizationCategories cc on (c.id = cc.CategoriesId)  join Organization o on (cc.OrganizationId = o.id) where o.id = ?1 and c.delete <> 1  group by c.id, c.name")    List<Categories> getCategoriesByOrganizationId(Integer id);


}
