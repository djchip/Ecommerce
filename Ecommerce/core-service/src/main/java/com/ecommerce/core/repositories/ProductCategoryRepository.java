package com.ecommerce.core.repositories;

import com.ecommerce.core.dto.ProductCategoryDTO;
import com.ecommerce.core.entities.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer>{

    @Query(value = "SELECT new com.ecommerce.core.dto.ProductCategoryDTO(p.id, p.categoryName, p.slug, pc.parentId, pc.categoryName, p.description, p.active, p.updatedBy, p.createdBy, p.createdDate, p.updatedDate) FROM ProductCategory p LEFT JOIN ProductCategory pc ON p.parentId = pc.id WHERE (?1 IS NULL OR lower(p.categoryName) like %?1% OR lower(p.description) like %?1%) AND p.active = true ORDER BY p.updatedDate desc")
    Page<ProductCategoryDTO> doSearch(String keyword, Pageable paging);

    List<ProductCategory> findAllByActiveIsTrue();

    ProductCategory findByCategoryNameAndActiveIsTrue(String categoryName);

    @Query(value = "SELECT new com.ecommerce.core.dto.ProductCategoryDTO(p.id, p.categoryName, p.slug, pc.parentId, pc.categoryName, p.description, p.active, p.updatedBy, p.createdBy, p.createdDate, p.updatedDate) FROM ProductCategory p LEFT JOIN ProductCategory pc ON p.parentId = pc.id WHERE p.active = true AND (p.id = ?1)")
    ProductCategoryDTO findProductCategoryDTOById(Integer id);

    ProductCategory findByIdAndActiveTrue(Integer id);

    @Query(value = "SELECT p FROM ProductCategory p WHERE p.active = true AND p.parentId = ?1")
    List<ProductCategory> findByParentId(Integer parentId);

}
