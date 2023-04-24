package com.ecommerce.core.service;

import com.ecommerce.core.entities.StandardUser;

import java.util.List;

public interface StandardUserService {
    StandardUser save(StandardUser standardUser);

    boolean checkExisted(int staId, int userId);

    List<Integer> getListStandardIdByUsername(String username);
}
