package com.ecommerce.core.service.impl;

import com.ecommerce.core.entities.ProgramUser;
import com.ecommerce.core.entities.Programs;
import com.ecommerce.core.repositories.ProgramUserRepositoty;
import com.ecommerce.core.service.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgramServiceImpl implements ProgramService {

    @Autowired
    ProgramUserRepositoty repositoty;

    @Override
    public ProgramUser save(ProgramUser programUser) {
        return repositoty.save(programUser);
    }

    @Override
    public List<Programs> findByOrgId(Integer id) {
        return repositoty.findByOrgId(id);
    }
}
