package com.ecommerce.core.repositories;

import com.ecommerce.core.dto.CriDTO;
import com.ecommerce.core.dto.CriteriaUserDTO;
import com.ecommerce.core.entities.CriteriaUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CriteriaUserRepository extends JpaRepository<CriteriaUser, Integer> {

    @Query(value = "SELECT c FROM CriteriaUser c WHERE c.criteriaId = ?1 AND c.userId = ?2")
    CriteriaUser checkExisted(int criId, int userId);

    @Query(value = "SELECT c.criteriaId FROM CriteriaUser c JOIN UserInfo u ON c.userId = u.id WHERE u.username = ?1")
    List<Integer> getListCriteriaIdByUsername(String username);

    @Query(value = "SELECT c.userId FROM CriteriaUser c WHERE c.criteriaId = ?1")
    List<Integer> getListUserIdByCriteriaId(int id);

    @Query(value = "SELECT new com.ecommerce.core.dto.CriDTO(c.programId || '.' || c.standardId || '.' || c.criteriaId) FROM CriteriaUser c WHERE c.userId = ?1 AND c.criteriaId IS NOT NULL AND c.standardId IS NOT NULL AND c.programId IS NOT NULL AND c.orgId = ?2 AND c.categoryId = ?3")
    List<CriDTO> getListProStaCriDTOByUserId(Integer id, Integer orgId, Integer categoryId);

    @Query(value = "SELECT new com.ecommerce.core.dto.CriteriaUserDTO(c.criteriaId, c.standardId, c.userId, c.programId, c.categoryId, c.orgId) FROM CriteriaUser c JOIN Directory d ON d.id = c.standardId JOIN Programs p ON p.id = c.programId JOIN Organization o ON o.id = c.orgId JOIN Categories ca ON ca.id = c.categoryId LEFT JOIN Criteria cri ON cri.id = c.criteriaId WHERE (cri.delete IS NULL OR cri.delete <> 1) AND d.delete <> 1 AND p.delete <> 1 AND o.deleted <> 1 AND ca.delete <> 1 AND c.userId = ?1 AND c.orgId = ?2 AND c.categoryId = ?3")
    List<CriteriaUserDTO> getListCriteriaUserByUserId(Integer id, Integer orgId, Integer categoryId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM CriteriaUser c WHERE (?1 IS NULL OR c.criteriaId = ?1) AND c.userId = ?2 AND c.programId = ?3 AND (?4 IS NULL OR c.standardId = ?4) AND c.categoryId = ?5 AND c.orgId = ?6")
    void deleteCriteriaUser(Integer criteriaId, Integer userId, Integer programId, Integer standardId, Integer categoryId, Integer orgId);

    @Query(value = "SELECT new com.ecommerce.core.dto.CriDTO(c.programId || '') FROM CriteriaUser c WHERE c.userId = ?1 AND c.standardId IS NULL AND c.criteriaId IS NULL AND c.orgId = ?2 AND c.categoryId = ?3")
    List<CriDTO> getListProDTOByUserId(Integer id, Integer orgId, Integer categoryId);

    @Query(value = "SELECT new com.ecommerce.core.dto.CriDTO(c.programId || '.' || c.standardId) FROM CriteriaUser c WHERE c.userId = ?1 AND (c.criteriaId IS NULL) AND c.orgId = ?2 AND c.categoryId = ?3")
    List<CriDTO> getListProStaDTOByUserId(Integer id, Integer orgId, Integer categoryId);

    @Query(value = "SELECT cu FROM CriteriaUser cu JOIN Organization o ON cu.orgId = o.id JOIN Categories ca ON ca.id = cu.categoryId JOIN Directory d ON d.id = cu.standardId WHERE cu.orgId = ?1 AND cu.categoryId = ?2 AND cu.standardId = ?3")
    List<CriteriaUser> getListUserIdPrivileges(Integer orgId, Integer categoryId, Integer standardId);

    @Query(value = "SELECT c FROM CriteriaUser c JOIN Organization o ON o.id = c.orgId JOIN Categories ca ON ca.id = c.categoryId JOIN UserInfo u ON u.id = c.userId " +
            "JOIN Programs p ON p.id = c.programId JOIN Directory d ON d.id = c.standardId JOIN Criteria cri ON cri.id = c.criteriaId WHERE c.orgId = ?1 AND c.categoryId = ?2 " +
            "AND c.userId = ?3 AND c.programId = ?4 AND (c.standardId IS NULL OR c.standardId = ?5) AND (c.criteriaId IS NULL OR c.criteriaId = ?6) AND o.deleted <> 1 AND " +
            "ca.delete <> 1 AND u.deleted <> 1 AND p.delete <> 1 AND d.delete <> 1 AND cri.delete <> 1")
    CriteriaUser findByOrgIdAndCategoryIdAndUserIdAndProgramIdAndStaIdAndCriId(Integer orgId, Integer categoryId, Integer userId, Integer programId, Integer staId, Integer criId);
}
