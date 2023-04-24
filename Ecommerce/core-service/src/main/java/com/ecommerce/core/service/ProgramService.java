package com.ecommerce.core.service;

import com.ecommerce.core.entities.ProgramUser;
import com.ecommerce.core.entities.Programs;

import java.util.List;

public interface ProgramService {
    ProgramUser save(ProgramUser programUser);

    List<Programs> findByOrgId(Integer id);
}
