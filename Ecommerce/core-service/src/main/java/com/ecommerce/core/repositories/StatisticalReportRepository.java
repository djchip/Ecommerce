package com.ecommerce.core.repositories;

import com.ecommerce.core.entities.StatisticalReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticalReportRepository extends JpaRepository<StatisticalReport, Integer> {

    @Query(value = "SELECT s FROM StatisticalReport s WHERE s.status = 1 AND (?1 IS NULL OR lower(s.name) LIKE %?1% OR lower(s.nameEn) LIKE %?1%) ORDER BY s.updatedDate DESC")
    Page<StatisticalReport> doSearch(String keyword, Pageable paging);

    StatisticalReport findByFormIdAndStatus(Integer formId, Integer status);
}
