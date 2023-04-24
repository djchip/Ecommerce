package com.ecommerce.core.service.impl;

import com.ecommerce.core.entities.ProgramUser;
import com.ecommerce.core.service.ProgramUserSevice;
import com.ecommerce.core.repositories.ProgramUserRepositoty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProgramUserSeviceImpl implements ProgramUserSevice {
    @Autowired
    private ProgramUserRepositoty programUserRepositoty;

    @Override
    public ProgramUser create(ProgramUser entity) {
        return programUserRepositoty.save(entity);
    }

    @Override
    public ProgramUser retrieve(Integer id) {
        return null;
    }

    @Override
    public void update(ProgramUser entity, Integer id) {

    }

    @Override
    public void delete(Integer id) throws Exception {

    }
}
