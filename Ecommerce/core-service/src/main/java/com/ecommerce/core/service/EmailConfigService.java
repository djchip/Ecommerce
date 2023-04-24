package com.ecommerce.core.service;

import com.ecommerce.core.entities.EmailConfig;
import com.ecommerce.core.entities.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EmailConfigService extends IRootService<EmailConfig> {

    Page<EmailConfig> doSearch(Pageable paging);

    List<Unit> doSearchMailTo();
}
