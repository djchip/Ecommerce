package com.ecommerce.core.service.impl;

import com.ecommerce.core.entities.StandardUser;
import com.ecommerce.core.repositories.StandardUserRepository;
import com.ecommerce.core.service.StandardUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StandardUserServiceImpl implements StandardUserService {
    @Autowired
    StandardUserRepository repo;

    @Override
    public StandardUser save(StandardUser standardUser) {
        return repo.save(standardUser);
    }

    @Override
    public boolean checkExisted(int staId, int userId) {
        StandardUser checkExisted;
        checkExisted = repo.checkExisted(staId, userId);
        if(checkExisted != null){
            return false;
        }
        return true;
    }

    @Override
    public List<Integer> getListStandardIdByUsername(String username) {
        return repo.getListStandardIdByUsername(username);
    }
}
