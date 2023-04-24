package com.ecommerce.core.service;

import com.ecommerce.core.entities.StatisticalReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StatisticalReportService extends IRootService<StatisticalReport>{

    Page<StatisticalReport> doSearch(String keyword, Pageable paging);

    StatisticalReport findByFormIdAndStatus(Integer formId);
}
