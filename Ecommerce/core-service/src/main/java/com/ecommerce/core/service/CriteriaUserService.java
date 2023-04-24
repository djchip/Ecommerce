package com.ecommerce.core.service;

import com.ecommerce.core.dto.CriDTO;
import com.ecommerce.core.dto.CriteriaUserDTO;
import com.ecommerce.core.entities.CriteriaUser;

import java.util.List;

public interface CriteriaUserService {
    CriteriaUser save(CriteriaUser criteriaUser);

    boolean checkExisted(int criId, int userId);

    List<Integer> getListCriteriaIdByUsername(String username);

    List<Integer> getListUserIdByCriteriaId(int id);

    List<CriDTO> getListProStaCriDTOByUserId(Integer id, Integer orgId, Integer categoryId);

    List<CriDTO> getListProDTOByUserId(Integer id, Integer orgId, Integer categoryId);

    List<CriDTO> getListProStaDTOByUserId(Integer id, Integer orgId, Integer categoryId);

    List<CriteriaUserDTO> getListCriteriaUserByUserId(Integer id, Integer orgId, Integer categoryId);

    void deleteCriteriaUser(Integer criteriaId, Integer userId, Integer programId, Integer standardId, Integer categoryId, Integer orgId);

    CriteriaUser findByOrgIdAndCategoryIdAndUserIdAndProgramIdAndStaIdAndCriId(Integer orgId, Integer categoryId, Integer userId, Integer programId, Integer staId, Integer criId);
    List<CriteriaUser> getListUserIdPrivileges(Integer orgId, Integer categoryId, Integer standardId);
}
