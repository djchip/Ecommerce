package com.ecommerce.core.repositories;


import java.util.List;
import java.util.Optional;

import com.ecommerce.core.dto.*;
import com.ecommerce.core.entities.Criteria;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CriteriaRepository extends JpaRepository<Criteria,Integer> {


    @Query(value = "SELECT distinct new com.ecommerce.core.dto.CriteriaDTO(c.id, c.name, c.nameEn, c.code, c.description, c.create_by, c.update_by, c.createdDate, c.updatedDate, d.name, o.name, c.standardId, c.programId, c.delete, c.organizationId, o.nameEn, d.nameEn, ca.id, ca.name, ca.nameEn) " +
            "FROM Criteria c LEFT JOIN Directory d ON c.standardId = d.id LEFT JOIN Organization o ON c.organizationId = o.id LEFT JOIN CriteriaUser cu ON cu.criteriaId = c.id LEFT JOIN UserInfo u ON cu.userId = u.id LEFT JOIN Programs p ON p.id = cu.programId LEFT JOIN Categories ca ON c.categoryId = ca.id WHERE " +
            "(?1 IS NULL OR lower(c.name) like %?1% OR lower(c.description) like %?1% ) " +
            "AND (?2 IS NULL OR p.id = ?2) " +
            "AND (?3 IS NULL OR c.standardId = ?3) " +
            "AND (?4 is null or o.id = ?4) " +
            "AND (?5 IS NULL OR u.username = ?5) " +
            "and c.delete <> 1 ORDER BY coalesce(c.updatedDate, c.createdDate) desc, c.id desc")
    Page<CriteriaDTO> doSearch(String keyword, Integer proId, Integer stanId, Integer orgId, String userName, Pageable paging);


    @Query(value = "SELECT distinct new com.ecommerce.core.dto.CriteriaDTO(c.id, c.nameEn, c.nameEn, c.code, c.descriptionEn, c.create_by, c.update_by, c.createdDate, c.updatedDate, d.nameEn, o.nameEn, c.standardId, c.programId, c.delete, c.organizationId, o.nameEn, d.nameEn, ca.id, ca.nameEn, ca.nameEn) " +
            "FROM Criteria c LEFT JOIN Directory d ON c.standardId = d.id LEFT JOIN Organization o ON c.organizationId = o.id LEFT JOIN CriteriaUser cu ON cu.criteriaId = c.id LEFT JOIN UserInfo u ON cu.userId = u.id LEFT JOIN Programs p ON p.id = cu.programId LEFT JOIN Categories ca ON c.categoryId = ca.id WHERE " +
            "(?1 IS NULL OR lower(c.name) like %?1% OR lower(c.description) like %?1% ) " +
            "AND (?2 IS NULL OR p.id = ?2) " +
            "AND (?3 IS NULL OR c.standardId = ?3) " +
            "AND (?4 is null or o.id = ?4) " +
            "AND (?5 IS NULL OR u.username = ?5) " +
            "and c.delete <> 1 ORDER BY coalesce(c.updatedDate, c.createdDate) desc, c.id desc")
    Page<CriteriaDTO> doSearchEn(String keyword, Integer proId, Integer stanId, Integer orgId, String userName, Pageable paging);

    @Query(value = "SELECT new com.ecommerce.core.dto.CriteriaDTO(u.id, u.name, u.nameEn, u.code, u.description, u.create_by, u.update_by, u.createdDate, u.updatedDate, d.name, p.name, u.standardId, u.programId, u.delete, u.organizationId, u.nameEn, d.nameEn, c.id, c.name, c.nameEn) FROM Criteria u left join Categories  c on (u.categoryId=c.id) LEFT JOIN Directory d ON (u.standardId = d.id) LEFT JOIN Organization  p ON (u.organizationId = p.id) WHERE 1=1 AND u.id = ?1")
    CriteriaDTO getById(Integer id);

    @Query(value = "SELECT new com.ecommerce.core.dto.CriteriaDTO(u.id, u.name, u.nameEn, u.code, u.descriptionEn, u.create_by, u.update_by, u.createdDate, u.updatedDate, d.nameEn, p.nameEn, u.standardId, u.programId, u.delete, u.organizationId, u.nameEn, d.nameEn, c.id, c.name, c.nameEn) FROM Criteria u left join Categories  c on (u.categoryId=c.id) LEFT JOIN Directory d ON (u.standardId = d.id) LEFT JOIN Organization  p ON (u.organizationId = p.id) WHERE 1=1 AND u.id = ?1")
    CriteriaDTO getByIdEn(Integer id);
    public Optional<Criteria> findByNameAndDeleteNot(String name, Integer deleted);

    @Query(value = "select  new com.ecommerce.core.dto.PreTreeNodeByIdDTO(c.id, c.standardId, c.name) from Criteria c  left JOIN Directory d ON c.standardId = d.id LEFT JOIN CriteriaUser s ON s.standardId = c.id LEFT JOIN UserInfo ui ON ui.id = s.userId WHERE d.delete <> 1 AND c.delete <> 1 AND (?1 is null OR c.organizationId = (SELECT p.organizationId FROM Programs p WHERE p.id = ?1)) AND (?2 IS NULL OR ui.username = ?2) GROUP BY c.id, c.name ORDER BY c.standardId, c.id")
    List<PreTreeNodeByIdDTO> getListCriteriaTreeNode(Integer programId, String username);

    @Query(value = "select  new com.ecommerce.core.dto.PreTreeNodeByIdDTO(c.id, c.standardId, c.nameEn) from Criteria c  left JOIN Directory d ON c.standardId = d.id LEFT JOIN CriteriaUser s ON s.standardId = c.id LEFT JOIN UserInfo ui ON ui.id = s.userId WHERE d.delete <> 1 AND c.delete <> 1 AND (?1 is null OR c.organizationId = (SELECT p.organizationId FROM Programs p WHERE p.id = ?1)) AND (?2 IS NULL OR ui.username = ?2) GROUP BY c.id, c.nameEn ORDER BY c.standardId, c.id")
    List<PreTreeNodeByIdDTO> getListCriteriaTreeNodeEn(Integer programId, String username);


    @Query(value = "select  new com.ecommerce.core.dto.TreeNodeCriteriaDTO(p.id || '.' || d.id || '.' || c.id, c.name || ' - ' || c.code, c.standardId) from Criteria c JOIN Directory d ON c.standardId = d.id JOIN Organization o ON o.id = c.organizationId JOIN Programs p ON p.organizationId IN o.id WHERE d.delete <> 1 AND c.delete <> 1 AND (?1 is null OR c.organizationId = o.id) ORDER BY c.standardId, c.id")
    List<TreeNodeCriteriaDTO> getListTreeNodeCriteriaDTOByOrgId(Integer programId, String username);

    @Query(value = "select  new com.ecommerce.core.dto.TreeNodeCriteriaDTO(p.id || '.' || d.id || '.' || c.id, c.nameEn || ' - ' || c.code, c.standardId) from Criteria c JOIN Directory d ON c.standardId = d.id JOIN Organization o ON o.id = c.organizationId JOIN Programs p ON p.organizationId IN o.id WHERE d.delete <> 1 AND c.delete <> 1 AND (?1 is null OR c.organizationId = o.id) ORDER BY c.standardId, c.id")
    List<TreeNodeCriteriaDTO> getListTreeNodeCriteriaDTOByOrgIdEn(Integer programId, String username);

    @Query(value = "select new com.ecommerce.core.dto.ExhCodeAndStandIdDTO(d.id, d.code || '.' || c.code) from Criteria c, Directory d WHERE c.standardId = d.id AND c.id = ?1")
    List<ExhCodeAndStandIdDTO> getExhibitionCode(Integer criteriaId);
    
    @Query(value = "select new com.ecommerce.core.dto.ExhCodeAndIdDTO(d.id, c.id, c.code) from Criteria c, Directory d WHERE c.standardId = d.id AND c.id = ?1")
    List<ExhCodeAndIdDTO> getExhibitionCodeWithId(Integer criteriaId);

    @Query(value = "SELECT ifnull(MAX(order_cri), 0) FROM criteria c WHERE c.standard_id = ?1", nativeQuery = true)
    Integer getMaxOrder(Integer stanId);

    @Query(value = "SELECT c.name FROM Criteria c JOIN Proof p ON c.id = p.criteriaId WHERE p.id = ?1")
    String findNameById(Integer id);

    Boolean existsByCodeAndOrganizationIdAndStandardIdAndDeleteNot(String code, Integer programId, Integer standardId, Integer delete);


    List<Criteria> findByOrganizationIdAndDeleteNot(Integer organizationId, Integer delete);
    List<Criteria> findByProgramIdAndDeleteNot(Integer programId, Integer delete);

    List<Criteria> findByCodeAndOrganizationIdAndStandardIdAndDelete(String code, Integer programId, Integer standardId, Integer delete);


    List<Criteria> findByStandardIdAndDeleteNot(Integer id, Integer deleted);
    @Query(value = "SELECT c FROM Criteria c WHERE 1=1 AND c.delete <> 1 AND (c.standardId is null OR c.standardId = ?1) GROUP BY c.id, c.name ORDER BY c.name")
    List<Criteria> getListCriteriaBySta(Integer id);

    @Query(value = "SELECT new com.ecommerce.core.dto.CriteriaDTO(u.id, u.name, u.nameEn, u.code, u.description, u.create_by, u.update_by, u.createdDate, u.updatedDate, d.name, p.name, u.standardId, u.programId, u.delete, u.organizationId, u.name, d.nameEn, c.id, c.name, c.nameEn) FROM Criteria u  left join Categories  c on (u.categoryId=c.id) LEFT JOIN Directory d ON (u.standardId = d.id) LEFT JOIN Programs  p ON (u.programId = p.id) WHERE 1=1 AND u.id = ?1")
    Optional<CriteriaDTO> finbyID(Integer id);

    @Modifying
    @Transient
    @Query(value = "UPDATE criteria c SET code = REPLACE(code, ?1, ?2) WHERE c.org_id = ?3", nativeQuery = true)
    void updateExhCode(String oldExhCode, String newExhCode, int orgId);

    List<Criteria> findByCodeAndOrganizationIdAndProgramIdAndDelete(String code, Integer Orgranid, Integer Program, Integer Delete);

    List<Criteria> findAllByDeleteAndStandardIdNotNullAndOrganizationIdNotNull(Integer deleted);

    @Query(value = "select new com.ecommerce.core.dto.TreeNodeDTO(c.id, c.name) from Criteria c  WHERE c.delete = 0 AND (?1 is null OR c.organizationId = (SELECT p.organizationId FROM Programs p WHERE p.id = ?1))  ORDER BY c.standardId, c.id")
    List<TreeNodeDTO> getListCriteriaTreeNode1(Integer programId);

    //lấy selectbox
    @Query(value = "select new com.ecommerce.core.dto.TreeNodeDTO(c.id, c.name) from Criteria c WHERE c.delete <> 1 AND (?1 is null OR c.organizationId = (SELECT d.organizationId FROM Directory d WHERE d.id = ?1)) GROUP BY c.id, c.name ORDER BY c.id")

    List<TreeNodeDTO> getListCriteriaTreeNodeByProgramId(Integer id);

    boolean existsByOrganizationIdAndDelete(Integer orgId, Integer delete);

    @Query(value = "select new com.ecommerce.core.dto.PreTreeNodeByIdDTO1(c.id, c.standardId, c.name) from Criteria c JOIN Directory d ON c.standardId = d.id JOIN StandardUser s ON s.standardId = d.id JOIN UserInfo ui ON ui.id = s.userId WHERE d.delete <> 1 AND c.delete <> 1 AND (?1 is null OR c.organizationId = (SELECT p.organizationId FROM Programs p WHERE p.id = ?1)) AND ui.username = ?2 ORDER BY c.standardId, c.id")
    List<PreTreeNodeByIdDTO1> getListCriteriaTreeNode1(Integer programId, String username);

    @Query(value = "SELECT c.id FROM Criteria c WHERE c.delete <> 1 AND c.create_by = ?1")
    List<Integer> findAllCriIdByUsername(String username);

    @Query(value = "SELECT c FROM Criteria c WHERE c.organizationId = ?1 AND c.delete <> 1")
    List<Criteria> findByOrganizationId(Integer id);

    @Query(value = "SELECT c FROM Criteria c WHERE c.delete <> 1 ")
    List<Criteria> findAllByDeleteNot();

    @Query(value = "select  c from Criteria c left join Directory d on (c.standardId= d.id)left join CriteriaUser cc on (cc.criteriaId = c.id ) left  join UserInfo ui on (ui.id= cc.userId) where (?1 is null or d.id=?1)  and (?2 is null or ui.username = ?2) and c.delete != 1")
    List<Criteria> findCriteriaByStandardIdAndAndDeleteNot(Integer id, String userFromToken);
    @Query(value = "SELECT c FROM Criteria c WHERE c.delete <> 1 AND c.organizationId = ?1 AND c.categoryId = ?2")
    List<Criteria> getListCriteriaByOrgIdAndCategoryId(Integer orgId, Integer categoryId);

    @Query(value = "SELECT c FROM Criteria c JOIN Directory d ON c.standardId = d.id JOIN Organization o ON c.organizationId = o.id JOIN Categories ca ON c.categoryId = ca.id WHERE c.organizationId = ?1 AND c.categoryId = ?2 AND c.standardId = ?3")
    List<Criteria> getListCriteriaByOrgIdAndCategoryIdAndStandardId(Integer orgId, Integer categoryId, Integer standardId);


}
