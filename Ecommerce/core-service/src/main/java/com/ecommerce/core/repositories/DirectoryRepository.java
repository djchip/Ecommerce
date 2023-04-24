package com.ecommerce.core.repositories;


import com.ecommerce.core.dto.*;
import com.ecommerce.core.entities.Directory;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DirectoryRepository extends JpaRepository<Directory, Integer> {

//    @Query(value = "SELECT u " +
//            "FROM Directory u WHERE " +
//            "(?1 IS NULL OR lower(u.name) like %?1% OR lower(u.nameEn) like %?1% OR lower(u.description) like %?1% OR lower(u.descriptionEn) like %?1%)" +
//            " AND (?2 IS NULL OR u.organizationId = ?2) AND (?3 IS NULL OR u.organizationId = (SELECT p.organizationId FROM Programs p WHERE p.id = ?3))" +
//            "and u.delete <> 1 ORDER BY u.updatedDate desc, u.id desc")
//    Page<Directory> doSearch(String keyword, Integer orgId, Integer programId, Pageable paging);


    @Query(value = "SELECT new com.ecommerce.core.dto.DirectoryDTO(u.id, u.name, u.nameEn, u.code, u.description, u.descriptionEn, u.create_by, u.update_by, u.createdDate, u.updatedDate, d.nameEn, u.programId, u.orderDir, u.undoStatus, u.delete,d.id,d.name,d.nameEn , c.id, c.name, c.nameEn) " +
            "FROM Directory u JOIN Organization d ON (u.organizationId = d.id) LEFT JOIN CriteriaUser cu ON u.id = cu.standardId LEFT JOIN UserInfo ui ON ui.id = cu.userId left join Categories c on u.categoryId = c.id LEFT JOIN CriteriaUser cu2 ON cu2.categoryId = c.id WHERE " +
            "(?1 IS NULL OR lower(u.name) like %?1% OR lower(u.description) like %?1% OR lower(u.description) like %?1% OR lower(u.descriptionEn) like %?1%)" +
            "AND (?2 is null or u.organizationId = ?2) " +
            "AND (?3 IS NULL OR u.organizationId = (select p.organizationId from Programs p where p.id = ?3)) " +
            "AND (?4 IS NULL OR ui.username = ?4)" +
            "and u.delete <> 1 GROUP BY (u.code) ORDER BY coalesce(u.updatedDate, u.createdDate) desc, u.id desc ")
    Page<Directory> doSearch(String keyword, Integer orgId, Integer programId, String username, Pageable paging);

    @Query(value = "SELECT new com.ecommerce.core.dto.DirectoryDTO(u.id, u.name, u.nameEn, u.code, u.description, u.descriptionEn, u.create_by, u.update_by, u.createdDate, u.updatedDate, d.nameEn, u.programId, u.orderDir, u.undoStatus, u.delete,d.id,d.name,d.nameEn , c.id, c.name, c.nameEn) " +
            "FROM Directory u JOIN Organization d ON (u.organizationId = d.id) LEFT JOIN CriteriaUser cu ON u.id = cu.standardId LEFT JOIN UserInfo ui ON ui.id = cu.userId left join Categories c on u.categoryId = c.id LEFT JOIN CriteriaUser cu2 ON cu2.categoryId = c.id WHERE " +
            "(?1 IS NULL OR lower(u.name) like %?1% OR lower(u.description) like %?1% OR lower(u.description) like %?1% OR lower(u.descriptionEn) like %?1%)" +
            "AND (?2 is null or u.organizationId = ?2) " +
            "AND (?3 IS NULL OR u.organizationId = (select p.organizationId from Programs p where p.id = ?3)) " +
            "AND (?4 IS NULL OR ui.username = ?4)" +
            "and u.delete <> 1 GROUP BY (u.code) ORDER BY coalesce(u.updatedDate, u.createdDate) desc, u.id desc ")
    Page<DirectoryDTO> doSearchExcel(String keyword, Integer orgId, Integer programId, String username, Pageable paging);

    @Query(value = "SELECT new com.ecommerce.core.dto.DirectoryDTO(u.id, u.name, u.nameEn, u.code, u.description, u.descriptionEn, u.create_by, u.update_by, u.createdDate, u.updatedDate, d.nameEn, u.programId, u.orderDir, u.undoStatus, u.delete,d.id,d.name,d.nameEn , c.id, c.name, c.nameEn) " +
            "FROM Directory u LEFT JOIN Organization d ON (u.organizationId = d.id) left join Categories  c on (u.categoryId=c.id) WHERE " +
            "(?1 IS NULL OR lower(u.name) like %?1% OR lower(u.description) like %?1% OR lower(u.description) like %?1% OR lower(u.descriptionEn) like %?1%)" +
            " AND (?2 IS NULL OR u.programId = ?2) and u.delete <> 1 ORDER BY u.updatedDate desc")
    Page<DirectoryDTO> doSearchEn(String keyword, Integer proId, Pageable paging);

    @Query(value = "SELECT new com.ecommerce.core.dto.DirectoryDTO(u.id, u.name, u.nameEn, u.code, u.description, u.descriptionEn, u.create_by, u.update_by, u.createdDate, u.updatedDate, d.name, u.programId, u.orderDir, u.undoStatus, u.delete,d.id, d.name, d.nameEn, c.id, c.name, c.nameEn) " +
            "FROM Directory u LEFT JOIN Organization d ON (u.organizationId = d.id) left join Categories  c on (u.categoryId=c.id) WHERE " +
            "u.delete <> 1  ORDER BY u.updatedDate desc")
    List<DirectoryDTO> findAllDTO();

    @Query(value = "SELECT u FROM Directory u WHERE u.id = ?1 AND (SELECT p FROM Programs p) MEMBER u.programs")
    Directory getById(Integer id);

    @Query(value = "SELECT new com.ecommerce.core.dto.DirectoryDTO(u.id, u.name, u.nameEn, u.code, u.description, u.descriptionEn, u.create_by, u.update_by, u.createdDate, u.updatedDate, d.nameEn, u.programId, u.orderDir, u.undoStatus, u.delete,d.id, d.name,d.nameEn, c.id, c.name, c.nameEn) FROM Directory u LEFT JOIN Organization d ON (u.programId = d.id) left join Categories  c on (u.categoryId=c.id)WHERE 1=1 AND u.id = ?1")
    DirectoryDTO getByIdEn(Integer id);

    @Query(value = "SELECT d FROM Directory d WHERE " +
            " (?1 IS NULL OR (SELECT p FROM Programs p WHERE p.id = ?1) MEMBER d.programs) AND d.delete <> ?2 ORDER BY d.id")
    List<Directory> findByProgram(Integer id, Integer deleted);

    List<Directory> findByOrganizationIdAndDeleteNot(Integer orgId, Integer deleted);

    @Query(value = "SELECT d FROM Directory d LEFT JOIN CriteriaUser c ON c.standardId = d.id LEFT JOIN UserInfo u ON u.id = c.userId WHERE d.delete <> 1 AND d.organizationId = ?1 AND (?2 IS NULL OR u.username = ?2) GROUP BY d.id, d.name")
    List<Directory> findByOrganizationIdAndUsername(Integer orgId, String username);

    @Query(value = "SELECT d FROM Directory d LEFT JOIN CriteriaUser s ON d.id = s.standardId LEFT JOIN UserInfo u ON u.id = s.userId WHERE 1=1 AND d.delete <> 1 AND d.organizationId = ?1 AND u.username = ?2")
    List<Directory> findByOrganizationIdAndUserId(Integer orgId, String userName);

    @Query(value = "select c from Directory c WHERE c.delete <> 1 AND (?1 is null OR c.organizationId = (SELECT p.organizationId FROM Programs p WHERE p.id = ?1)) GROUP BY c.id, c.name ORDER BY c.name")
    List<Directory> findByOrgId(Integer programId);

    //    @EntityGraph(attributePaths = {Directory.Fields.name})
//    @Query(value = "Select f from Directory f where f.name like %?1")
    public Optional<Directory> findByNameAndDeleteNot(String name, Integer deleted);


    @Query(value = "select new com.ecommerce.core.dto.TreeNodeDTO(d.id, d.name) from Directory d LEFT JOIN CriteriaUser c ON d.id = c.standardId LEFT JOIN UserInfo ui ON ui.id = c.userId WHERE d.delete <> 1 AND (?1 is null OR d.organizationId = (SELECT p.organizationId FROM Programs p WHERE p.id = ?1)) AND (?2 IS NULL OR ui.username = ?2) GROUP BY d.id, d.name ORDER BY c.id")
    List<TreeNodeDTO> getListStandardTreeNodeByProgramId(Integer id, String username);


    @Query(value = "select new com.ecommerce.core.dto.TreeNodeDTO(d.id, d.nameEn) from Directory d LEFT JOIN CriteriaUser c ON d.id = c.standardId LEFT JOIN UserInfo ui ON ui.id = c.userId WHERE d.delete <> 1 AND (?1 is null OR d.organizationId = (SELECT p.organizationId FROM Programs p WHERE p.id = ?1)) AND (?2 IS NULL OR ui.username = ?2) GROUP BY d.id, d.nameEn ORDER BY c.id")
    List<TreeNodeDTO> getListStandardTreeNodeByProgramIdEn(Integer id, String username);

    @Query(value = "select  new com.ecommerce.core.dto.TreeNodeDTOProof(c.id, c.name) from Directory c left join CriteriaUser cc on (c.id= cc.standardId) left JOIN UserInfo ui ON ui.id = cc.userId WHERE c.delete <> 1 AND (?1 is null OR c.organizationId = (SELECT p.organizationId FROM Programs p WHERE p.id = ?1)) AND (?2 IS null OR ui.username = ?2) GROUP BY c.id, c.name ORDER BY c.id")
    List<TreeNodeDTOProof> getListStandardTreeNodeDTOByProgramId(Integer id, String username);

    @Query(value = "select  new com.ecommerce.core.dto.TreeNodeStandardDTO(p.id || '.' || d.id, p.id, d.name || ' - ' || d.code) from Directory d JOIN Organization o ON d.organizationId = o.id JOIN Programs p ON p.organizationId = o.id WHERE d.delete <> 1 AND d.organizationId = ?1 ORDER BY d.id")
    List<TreeNodeStandardDTO> getListTreeNodeStandardDTOByOrgId(Integer id, String username);

    @Query(value = "select  new com.ecommerce.core.dto.TreeNodeStandardDTO(p.id || '.' || d.id, p.id, d.nameEn || ' - ' || d.code) from Directory d JOIN Organization o ON d.organizationId = o.id JOIN Programs p ON p.organizationId = o.id WHERE d.delete <> 1 AND d.organizationId = ?1 ORDER BY d.id")
    List<TreeNodeStandardDTO> getListTreeNodeStandardDTOByOrgIdEn(Integer id, String username);

    //lấy selectbox
    @Query(value = "select new com.ecommerce.core.dto.TreeNodeDTO(c.id, c.name) from Directory c JOIN StandardUser s ON c.id = s.standardId JOIN UserInfo ui ON ui.id = s.userId WHERE c.delete <> 1 AND (?1 is null OR c.organizationId = (SELECT p.organizationId FROM Programs p WHERE p.id = ?1)) AND ui.username = ?2 GROUP BY c.id, c.name ORDER BY c.id")
    List<TreeNodeDTO> getListStandardTreeNodeByProgramIdd(Integer id);

    @Query(value = "Select f from Directory f where f.code like %?1")
    public Optional<Directory> findByCode(String code);

    @Query(value = "select d.code from Directory d WHERE d.id = ?1")
    String getExhibitionCode(Integer standardId);

    @Query(value = "select new com.ecommerce.core.dto.ExhCodeAndIdDTO(d.id, 0, d.code) from Directory d WHERE d.id = ?1")
    List<ExhCodeAndIdDTO> getExhibitionCodeWithId(Integer standardId);

    @Query(value = "SELECT ifnull(MAX(order_dir), 0) FROM directory d WHERE 1 = 1", nativeQuery = true)
    Integer getMaxOrder();

    @Query(value = "SELECT d.name FROM Directory d JOIN Proof p ON d.id = p.standardId WHERE p.id = ?1")
    String findNameByProofId(Integer id);

    Boolean existsByCodeAndOrganizationIdAndDeleteIsNot(String code, Integer organizationId, Integer delete);

    List<Directory> findByCodeAndOrganizationIdAndDelete(String code, Integer organizationId, Integer delete);

    List<Directory> findByCodeAndProgramIdAndDelete(String code, Integer programId, Integer delete);

    List<Directory> findByCodeAndOrganizationIdAndDeleteIsNot(String code, Integer organizationId, Integer delete);

    @Query(value = "SELECT new com.ecommerce.core.dto.DirectoryDTO(u.id, u.name, u.nameEn, u.code, u.description, u.descriptionEn, u.create_by, u.update_by, u.createdDate, u.updatedDate, d.nameEn, u.programId, u.orderDir, u.undoStatus, u.delete,d.id, d.name,d.nameEn , c.id, c.name, c.nameEn) FROM Directory u LEFT JOIN Organization d ON (u.organizationId = d.id) left join Categories  c on (u.categoryId=c.id) WHERE 1=1 AND u.id = ?1")
    Optional<DirectoryDTO> finbyID(Integer id);

    @Modifying
    @Transient
    @Query(value = "UPDATE directory d SET code = REPLACE(code, ?1, ?2) WHERE d.org_id = ?3", nativeQuery = true)
    void updateExhCode(String oldExhCode, String newExhCode, int orgId);

    @Query(value = "SELECT d FROM Directory d WHERE " +
            " (?1 IS NULL OR (SELECT p FROM Programs p WHERE p.id = ?1) MEMBER d.programs) AND d.delete <> ?2  and d.code like %?1 ORDER BY d.id")
    List<Directory> findByCodeAndProgramid(String code, Integer programId);

    List<Directory> findAllByDeleteAndOrganizationIdNotNull(Integer deleted);

    List<Directory> findByDeleteNot(Integer deleted);

    boolean existsByOrganizationIdAndDelete(Integer orgId, Integer deleted);

    @Query(value = "SELECT d.id FROM Directory d WHERE d.delete <> 1 AND d.create_by = ?1")
    List<Integer> findAllStaIdByUsername(String username);

    @Query(value = "SELECT d FROM Directory d WHERE d.delete <> 1 AND d.organizationId = ?1")
    List<Directory> findByOrganizationId(Integer id);

    @Query(value = "SELECT d FROM Directory d LEFT JOIN CriteriaUser c ON c.standardId = d.id LEFT JOIN UserInfo u ON u.id = c.userId WHERE d.delete <> 1 AND d.organizationId = ?1 AND d.categoryId = ?2 AND (?3 IS NULL OR u.username = ?3) GROUP BY d.id, d.name")
    List<Directory> findByOrganizationIdAndUsername(Integer orgId, Integer categoryId, String username);

    @Query(value = "SELECT d FROM Directory d left join Programs p on(d.organizationId= p.organizationId) left join CriteriaUser cc on (cc.standardId = d.id ) left  join UserInfo ui on (ui.id= cc.userId) WHERE 1=1 AND (?1 IS NULL OR p.id = ?1) and (?2 is null or ui.username = ?2) and d.delete <> 1  ")
    List<Directory> findDirectoryByProgramIdAndDeleteNot(Integer programId, String userFromToken);

    @Query(value = "SELECT d FROM Directory d WHERE d.delete <> 1 AND d.organizationId = ?1 AND d.categoryId = ?2")
    List<Directory> getListStandardByOrgIdAndCategoryId(Integer orgId,Integer categoryId);


    Directory findByIdAndAndDelete(Integer id, Integer deleted);

}
