package com.ecommerce.core.repositories;

import com.ecommerce.core.entities.ProductImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImagesRepository extends JpaRepository<ProductImages, Integer> {
}
