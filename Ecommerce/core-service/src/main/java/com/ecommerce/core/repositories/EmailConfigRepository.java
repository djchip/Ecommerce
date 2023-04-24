package com.ecommerce.core.repositories;

import com.ecommerce.core.entities.EmailConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmailConfigRepository extends JpaRepository<EmailConfig, Integer> {
    @Query(value = "SELECT u FROM EmailConfig u order by u.id")
    Page<EmailConfig> doSearch(Pageable paging);
}
