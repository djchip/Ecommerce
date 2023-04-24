package com.ecommerce.core.repositories;

import com.ecommerce.core.entities.RoleMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface RoleMenuRepository extends JpaRepository<RoleMenu,Integer> {
}
