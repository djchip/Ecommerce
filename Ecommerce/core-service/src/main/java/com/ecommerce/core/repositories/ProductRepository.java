package com.ecommerce.core.repositories;

import com.ecommerce.core.dto.DataProductDTO;
import com.ecommerce.core.dto.ProductDTO;
import com.ecommerce.core.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query(value = "SELECT new com.ecommerce.core.dto.ProductDTO(p.id, p.name, pc.id, " +
            "pc.categoryName, p.slug, p.description, p.discount, p.unitPrice, p.sold, " +
            "p.featured, p.active, p.unitsInStock, p.updatedBy, p.createdBy, p.createdDate, " +
            "p.updatedDate) FROM Product p LEFT JOIN ProductCategory pc ON " +
            "p.category.id = pc.id " +
            "WHERE (?1 IS NULL OR lower(p.name) like %?1% OR lower(p.description) like %?1%) " +
            "AND (?2 IS NULL OR p.category.id = ?2) AND pc.active = true " +
            "ORDER BY coalesce(p.updatedDate, p.createdDate) desc")
    Page<ProductDTO> doSearch(String keyword, Integer categoryId, Pageable paging);

    Product findByName(String name);

    Optional<Product> findById(Integer id);

    @Query(value = "SELECT p FROM Product p WHERE p.category.id = ?1")
    List<Product> findByCategoryId(Integer id);

    @Query(value = "SELECT new com.ecommerce.core.dto.DataProductDTO(p.id, p.name, p.slug, p.description, p.category.categoryName, p.unitPrice, p.image, true, p.rating) FROM Product p WHERE p.active = true")
    List<DataProductDTO> getListProduct();
}
