package com.ecommerce.core.repositories;

import com.ecommerce.core.entities.OrganizationCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationCategoriesRepository extends JpaRepository<OrganizationCategories,Integer> {
}
