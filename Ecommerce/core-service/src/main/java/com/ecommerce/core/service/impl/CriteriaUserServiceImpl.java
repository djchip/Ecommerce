package com.ecommerce.core.service.impl;

import com.ecommerce.core.entities.CriteriaUser;
import com.ecommerce.core.repositories.CriteriaUserRepository;
import com.ecommerce.core.service.CriteriaUserService;
import com.ecommerce.core.dto.CriDTO;
import com.ecommerce.core.dto.CriteriaUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CriteriaUserServiceImpl implements CriteriaUserService {
    @Autowired
    CriteriaUserRepository repository;

    @Override
    public CriteriaUser save(CriteriaUser criteriaUser) {
        return repository.save(criteriaUser);
    }

    @Override
    public boolean checkExisted(int criId, int userId) {
        CriteriaUser checkExisted;
        checkExisted = repository.checkExisted(criId, userId);
        if(checkExisted != null){
            return false;
        }
        return true;
    }

    @Override
    public List<Integer> getListCriteriaIdByUsername(String username) {
        return repository.getListCriteriaIdByUsername(username);
    }

    @Override
    public List<Integer> getListUserIdByCriteriaId(int id) {
        return repository.getListUserIdByCriteriaId(id);
    }

    @Override
    public List<CriDTO> getListProStaCriDTOByUserId(Integer id, Integer orgId, Integer categoryId) {
        return repository.getListProStaCriDTOByUserId(id, orgId, categoryId);
    }

    @Override
    public List<CriDTO> getListProDTOByUserId(Integer id, Integer orgId, Integer categoryId) {
        return repository.getListProDTOByUserId(id, orgId, categoryId);
    }

    @Override
    public List<CriDTO> getListProStaDTOByUserId(Integer id, Integer orgId, Integer categoryId) {
        return repository.getListProStaDTOByUserId(id, orgId, categoryId);
    }

    @Override
    public List<CriteriaUserDTO> getListCriteriaUserByUserId(Integer id, Integer orgId, Integer categoryId) {
        return repository.getListCriteriaUserByUserId(id, orgId, categoryId);
    }

    @Transactional
    @Override
    public void deleteCriteriaUser(Integer criteriaId, Integer userId, Integer programId, Integer standardId, Integer categoryId, Integer orgId) {
        repository.deleteCriteriaUser(criteriaId, userId, programId, standardId, categoryId, orgId);
    }

    @Override
    public CriteriaUser findByOrgIdAndCategoryIdAndUserIdAndProgramIdAndStaIdAndCriId(Integer orgId, Integer categoryId, Integer userId, Integer programId, Integer staId, Integer criId) {
        return repository.findByOrgIdAndCategoryIdAndUserIdAndProgramIdAndStaIdAndCriId(orgId, categoryId, userId, programId,staId,criId);
    }

    @Override
    public List<CriteriaUser> getListUserIdPrivileges(Integer orgId, Integer categoryId, Integer standardId) {
        return repository.getListUserIdPrivileges(orgId, categoryId, standardId);
    }
}
