package com.ecommerce.core.repositories;

import com.ecommerce.core.entities.ProgramUser;
import com.ecommerce.core.entities.Programs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramUserRepositoty extends JpaRepository<ProgramUser, Integer> {

    @Query(value = "SELECT p FROM Programs p WHERE p.delete <> 1 AND p.organizationId = ?1")
    List<Programs> findByOrgId(Integer id);
}
