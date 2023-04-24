package com.ecommerce.core.repositories;

import com.ecommerce.core.dto.ProgramsDTO;
import com.ecommerce.core.dto.TreeNodeProgramDTO;
import com.ecommerce.core.entities.Programs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProgramsRepository extends JpaRepository<Programs, Integer> {
    @Query(value = "SELECT pm from Programs pm ")
    Page<Programs> findAll(Pageable pageable);
//    @Query(value ="SELECT p FROM Programs p WHERE 1=1 AND (?1 IS NULL OR lower(p.name) like %?1%) ORDER BY p.updatedDate desc")
//    Page<Programs> doSearch(String keyword,Pageable paging);
    Programs findByName(String name);
    @Query(value = "SELECT new com.ecommerce.core.dto.ProgramsDTO(c.id, c.name, c.nameEn,u.descriptionEn,u.nameEn,d.nameEn,u.id, u.name, u.description,u.note, u.createdBy,u.createdDate, u.updatedDate, d.name, u.organizationId, u.undoStatus, u.delete,u.updatedBy) FROM Programs u LEFT JOIN Organization d ON (u.organizationId = d.id) left join Categories  c on(u.categoryId=c.id) WHERE 1=1 AND u.id = ?1")
    ProgramsDTO findByIdd(Integer id);

    @Query(value = "SELECT new com.ecommerce.core.dto.ProgramsDTO(c.id, c.name, c.name,u.descriptionEn,u.nameEn,d.name,u.id, u.name, u.description,u.note, u.createdBy,u.createdDate, u.updatedDate, d.name, u.organizationId, u.undoStatus, u.delete,u.updatedBy) FROM Programs u LEFT JOIN Organization d ON (u.organizationId = d.id) LEFT JOIN CriteriaUser cu ON u.id = cu.programId lEFT JOIN UserInfo ui ON ui.id = cu.userId left join Categories c on u.categoryId = c.id LEFT JOIN CriteriaUser cu2 ON cu2.categoryId = c.id WHERE 1=1 AND (?1 IS NULL OR lower(u.name) like %?1% ) AND (?2 IS NULL OR ui.username = ?2) AND u.delete <> 1 GROUP BY u.name, u.nameEn ORDER BY u.updatedDate desc, u.id desc")
    Page<ProgramsDTO> doSearch(String keyword, String username, Pageable paging);

    @Query(value = "SELECT new com.ecommerce.core.dto.ProgramsDTO(c.id, c.nameEn, c.nameEn,u.descriptionEn,u.nameEn,d.nameEn,u.id, u.nameEn, u.descriptionEn,u.note, u.createdBy,u.createdDate, u.updatedDate, d.nameEn, u.organizationId, u.undoStatus, u.delete,u.updatedBy) FROM Programs u LEFT JOIN Organization d ON (u.organizationId = d.id) LEFT JOIN CriteriaUser cu ON u.id = cu.programId lEFT JOIN UserInfo ui ON ui.id = cu.userId left join Categories c on u.categoryId = c.id LEFT JOIN CriteriaUser cu2 ON cu2.categoryId = c.id WHERE 1=1 AND (?1 IS NULL OR lower(u.name) like %?1% ) AND (?2 IS NULL OR ui.username = ?2) AND u.delete <> 1 GROUP BY u.name, u.nameEn ORDER BY u.updatedDate desc, u.id desc")
    Page<ProgramsDTO> doSearchEn(String keyword, String username, Pageable paging);

    @Query(value = "SELECT  u FROM Programs u LEFT JOIN Organization d ON (u.organizationId = d.id)  WHERE 1=1 AND u.id = ?1")
    Programs getById(Integer id);

    @Query(value = "SELECT u FROM Programs u LEFT JOIN Organization d ON (u.organizationId = d.id)WHERE 1=1 AND u.id = ?1")
    Programs getByIdEn(Integer id);

    List<Programs> findByDeleteNot(Integer deleted);

    @Query(value = "SELECT p FROM Programs p LEFT JOIN CriteriaUser c ON c.programId = p.id LEFT JOIN UserInfo u ON u.id = c.userId WHERE p.delete <> 1 AND (?1 IS NULL OR u.username = ?1) GROUP BY p.id, p.name")
    List<Programs> findProgramsByUsername(String username);

    @Query(value = "SELECT p FROM Programs p WHERE p.delete <> 1 AND (YEAR(p.createdDate) = ?1) ORDER BY p.createdDate DESC")
    List<Programs> getAllProgramsByYear(int byYear);

    List<Programs> findByOrganizationIdAndDeleteNot(Integer organizationId, Integer delete);

    @Query(value = "SELECT new com.ecommerce.core.dto.ProgramsDTO(c.id, c.name, c.nameEn,u.descriptionEn,u.nameEn,d.nameEn,u.id, u.name, u.description,u.note, u.createdBy,u.createdDate, u.updatedDate, d.name, u.organizationId, u.undoStatus, u.delete,u.updatedBy) FROM Programs u LEFT JOIN Organization d ON (u.organizationId = d.id) left join Categories  c on(u.categoryId=c.id)WHERE 1=1 AND u.id = ?1")
    Optional<ProgramsDTO> finbyID(Integer id);
    public Optional<Programs> findByNameAndDeleteNot(String name, Integer deleted);

    @Query(value = "SELECT p FROM Programs p JOIN Proof po ON p.id = po.programId WHERE 1=1 AND (?1 = po.id) AND p.delete <> 1")
    Programs findProgramByProofId(Integer id);

    List<Programs> findByNameAndDelete(String name, Integer delete);

    boolean existsByOrganizationIdAndDelete(Integer orgId, Integer delete);

    @Query(value = "SELECT DISTINCT YEAR(p.createdDate) FROM Programs p WHERE p.delete <> 1 ORDER BY YEAR(p.createdDate) ASC")
    List<Integer> getListYearInDataBase();

    @Query(value = "select new com.ecommerce.core.dto.TreeNodeProgramDTO(p.id, p.name) from Programs p WHERE p.delete <> 1 AND (?1 is null OR p.organizationId = (SELECT o.id FROM Organization o WHERE o.id = ?1)) GROUP BY p.id, p.name ORDER BY p.id")
    List<TreeNodeProgramDTO> getListTreeNodeProgramDTOByOrgId(Integer id, String username);

    @Query(value = "select new com.ecommerce.core.dto.TreeNodeProgramDTO(p.id, p.nameEn) from Programs p WHERE p.delete <> 1 AND (?1 is null OR p.organizationId = (SELECT o.id FROM Organization o WHERE o.id = ?1)) GROUP BY p.id, p.name ORDER BY p.id")
    List<TreeNodeProgramDTO> getListTreeNodeProgramDTOByOrgIdEn(Integer id, String username);

    @Query(value = "SELECT p FROM Programs p JOIN Organization o ON o.id = p.organizationId JOIN Categories c ON c.id = p.categoryId WHERE o.id = ?1 AND c.id = ?2")
    List<Programs> findByOrgIdAndCategoryId(Integer orgId, Integer categoryId);
}
