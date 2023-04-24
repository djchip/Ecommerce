package com.ecommerce.core.service.impl;

import com.ecommerce.core.entities.EmailConfig;
import com.ecommerce.core.entities.Unit;
import com.ecommerce.core.repositories.EmailConfigRepository;
import com.ecommerce.core.repositories.UnitRepository;
import com.ecommerce.core.service.EmailConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailConfigServiceImpl implements EmailConfigService {

    @Autowired
    EmailConfigRepository emailConfigRepository;

    @Autowired
    UnitRepository unitRepository;

    @Override
    public Page<EmailConfig> doSearch(Pageable paging) {
        return emailConfigRepository.doSearch(paging);
    }

    @Override
    public List<Unit> doSearchMailTo() {
        return unitRepository.getListMailTo();
    }

    @Override
    public EmailConfig create(EmailConfig entity) {
        return null;
    }

    @Override
    public EmailConfig retrieve(Integer id) {
        return emailConfigRepository.findById(id).orElse(null);
    }

    @Override
    public void update(EmailConfig entity, Integer id) {
        EmailConfig entityUpdate = emailConfigRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tồn tại"));
        emailConfigRepository.save(entityUpdate);
    }

    @Override
    public void delete(Integer id) {

    }
}
