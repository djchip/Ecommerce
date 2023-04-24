package com.ecommerce.core.service.impl;

import com.ecommerce.core.entities.StatisticalReport;
import com.ecommerce.core.repositories.StatisticalReportRepository;
import com.ecommerce.core.service.StatisticalReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StatisticalReportServiceImpl implements StatisticalReportService {

    private final int IS_ACTIVE = 1;
    private final int IS_NOT_ACTIVE = 0;

    @Autowired
    private StatisticalReportRepository statisticalReportRepository;

    @Override
    public StatisticalReport create(StatisticalReport entity) {
        entity.setStatus(IS_ACTIVE);
        return statisticalReportRepository.save(entity);
    }

    @Override
    public StatisticalReport retrieve(Integer id) {
        Optional<StatisticalReport> entity = statisticalReportRepository.findById(id);
        if (!entity.isPresent()){
            return  null;
        }
        return entity.get();
    }

    @Override
    public void update(StatisticalReport entity, Integer id) {
        statisticalReportRepository.save(entity);
    }

    @Override
    public void delete(Integer id) throws Exception {
        StatisticalReport entity = retrieve(id);
        entity.setStatus(IS_NOT_ACTIVE);
        statisticalReportRepository.save(entity);
    }

    @Override
    public Page<StatisticalReport> doSearch(String keyword, Pageable paging) {
        return statisticalReportRepository.doSearch(keyword, paging);
    }

    @Override
    public StatisticalReport findByFormIdAndStatus(Integer formId) {
        return statisticalReportRepository.findByFormIdAndStatus(formId, IS_ACTIVE);
    }
}
