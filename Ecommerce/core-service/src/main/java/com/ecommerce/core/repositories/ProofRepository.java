package com.ecommerce.core.repositories;

import com.ecommerce.core.dto.ProofDTO;
import com.ecommerce.core.entities.Proof;
import com.ecommerce.core.entities.Unit;
import com.ecommerce.core.dto.AfterTreeNodeByIdDTO;
import com.ecommerce.core.dto.FileProofDTO;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProofRepository extends JpaRepository<Proof, Integer> {

    @Query(value = "SELECT new com.ecommerce.core.dto.ProofDTO(p.id, p.proofCode, p.proofName, p.proofNameEn, p.documentType, a.name, a.nameEn, p.numberSign, p.releaseDate, p.signer, p.field, b.name, b.nameEn, p.releaseBy, u.unitName, u.unitNameEn, p.description, p.descriptionEn, p.note, p.noteEn, d.name, d.nameEn, c.name, c.nameEn, pro.name, pro.nameEn, p.createdBy, p.createdDate, p.updatedBy, p.updatedDate, d.name, d.nameEn) " +
            "FROM Proof p LEFT JOIN Directory d ON (p.standardId = d.id) " +
            "LEFT JOIN Criteria c ON (p.criteriaId = c.id) " +
            "LEFT JOIN Programs pro ON (p.programId = pro.id) " +
            "LEFT JOIN AppParam a ON (p.documentType = a.id) " +
            "LEFT JOIN AppParam b ON (p.field = b.id) " +
            "LEFT JOIN Unit u ON (p.releaseBy = u.id) " +
            "LEFT JOIN CriteriaUser cu ON (cu.criteriaId = p.criteriaId) " +
            "LEFT JOIN CriteriaUser cu1 ON (cu1.standardId = p.standardId) " +
            "LEFT JOIN UserInfo ui1 ON (ui1.id = cu.userId) " +
            "LEFT JOIN UserInfo ui2 ON (ui2.id = cu1.userId) " +
            "LEFT JOIN Categories ca ON ca.id = pro.categoryId " +
            "WHERE 1=1 AND (?1 IS NULL OR p.programId = ?1) AND (?2 IS NULL OR p.standardId = ?2) AND (?3 IS NULL OR p.criteriaId = ?3) AND (?4 IS NULL OR ui1.username = ?4 OR ui2.username = ?4) AND p.deleted <> 1 " +
            "GROUP BY p.id, p.proofCode, p.proofName, p.proofNameEn, p.documentType, a.name, a.nameEn, p.numberSign, p.releaseDate, p.signer, p.field, b.name, b.nameEn, p.releaseBy, u.unitName, u.unitNameEn, p.description, p.descriptionEn, p.note, p.noteEn, d.name, d.nameEn, c.name, c.nameEn, pro.name, pro.nameEn, p.createdBy, p.createdDate, p.updatedBy, p.updatedDate, d.name, d.nameEn " +
            "ORDER BY p.updatedDate desc, p.createdDate desc, p.id desc ")
    Page<ProofDTO> doSearch(Integer programId, Integer standardId, Integer criteriaId, String username, Pageable pageable);

    @Query(value = "SELECT p.ID, p.PROOF_CODE, p.PROOF_NAME, p.PROOF_NAME_EN, p.DOCUMENT_TYPE, a.NAME as namea, a.NAME_EN as namea_en, p.NUMBER_SIGN, p.RELEASE_DATE, p.SIGNER, p.FIELD, b.NAME as nameb, b.NAME_EN as nameb_en, p.RELEASE_BY, u.UNIT_NAME, u.UNIT_NAME_EN, p.DESCRIPTION, p.DESCRIPTION_EN, p.NOTE, p.NOTE_EN, d.name as named, d.name_en as named_en, c.name as namec, c.name_en as namec_en, pro.PROGRAM_NAME, pro.NAME_EN as namep, p.CREATED_BY, p.CREATED_DATE, p.UPDATED_BY, p.UPDATED_DATE" +
            " FROM proof p LEFT JOIN directory d ON (p.STANDARD_ID = d.id) " +
            " LEFT JOIN criteria c ON (p.CRITERIA_ID = c.id)" +
            " LEFT JOIN program  pro ON (p.program_Id = pro.id) " +
            " LEFT JOIN app_param  a ON (p.document_Type = a.id) " +
            " LEFT JOIN app_param  b ON (p.field = b.id) " +
            " LEFT JOIN unit u ON (p.release_By = u.id) " +
            "LEFT JOIN criteria_user cu ON (cu.criteriaId = p.CRITERIA_ID) " +
            "LEFT JOIN criteria_user cu1 ON (cu1.standardId = p.STANDARD_ID) " +
            "LEFT JOIN user_info  ui1 ON (ui1.id = cu.userId) " +
            "LEFT JOIN user_info  ui2 ON (ui2.id = cu1.userId) " +
            "LEFT JOIN proof_exh_file pe ON (p.id = pe.proof_id) " +
            "LEFT JOIN exhibition_file e ON (pe.exh_file_id = e.id)" +
            "WHERE 1=1 AND p.deleted <> 1 AND (?1 IS NULL OR p.PROGRAM_ID = ?1) AND (?2 IS NULL OR p.STANDARD_ID = ?2) AND (?3 IS NULL OR p.CRITERIA_ID = ?3) AND ((MATCH(p.PROOF_NAME, p.PROOF_CODE) AGAINST (?4) OR MATCH(e.file_content) AGAINST (?4))) AND (?5 IS NULL OR ui1.USERNAME = ?5 OR ui2.USERNAME = ?5) " +
            "GROUP BY p.ID, p.PROOF_CODE, p.PROOF_NAME, p.PROOF_NAME_EN, p.DOCUMENT_TYPE, a.NAME, a.NAME_EN, p.NUMBER_SIGN, p.RELEASE_DATE, p.SIGNER, p.FIELD, b.NAME, b.NAME_EN, p.RELEASE_BY, u.UNIT_NAME, u.UNIT_NAME_EN, p.DESCRIPTION, p.DESCRIPTION_EN, p.NOTE, p.NOTE_EN, d.name, d.name_en, c.name, c.name_en, pro.PROGRAM_NAME, pro.NAME_EN, p.CREATED_BY, p.CREATED_DATE, p.UPDATED_BY, p.UPDATED_DATE #{#pageable}", countQuery = "SELECT COUNT(*)" +
            " FROM proof p LEFT JOIN directory d ON (p.STANDARD_ID = d.id) " +
            " LEFT JOIN criteria c ON (p.CRITERIA_ID = c.id)" +
            " LEFT JOIN program  pro ON (p.program_Id = pro.id) " +
            " LEFT JOIN app_param  a ON (p.document_Type = a.id) " +
            " LEFT JOIN app_param  b ON (p.field = b.id) " +
            " LEFT JOIN unit u ON (p.release_By = u.id) " +
            "LEFT JOIN criteria_user cu ON (cu.standardId = p.STANDARD_ID) " +
            "LEFT JOIN criteria_user cu1 ON (cu1.criteriaId = p.CRITERIA_ID) " +
            "LEFT JOIN user_info  ui1 ON (ui1.id = cu.userId) " +
            "LEFT JOIN user_info  ui2 ON (ui2.id = cu1.userId) " +
            "LEFT JOIN categories  ca ON (ca.id = pro.categories_id) " +
            " LEFT JOIN proof_exh_file pe ON (p.id = pe.proof_id)" +
            " LEFT JOIN exhibition_file e ON (pe.exh_file_id = e.id)" +
            " WHERE 1=1 AND p.deleted <> 1 AND (?1 IS NULL OR p.PROGRAM_ID = ?1) AND (?2 IS NULL OR p.STANDARD_ID = ?2) AND (?3 IS NULL OR p.CRITERIA_ID = ?3) AND ((MATCH(p.PROOF_NAME, p.PROOF_CODE) AGAINST (?4) OR MATCH(e.file_content) AGAINST (?4))) AND (?5 IS NULL OR ui1.USERNAME = ?5 OR ui2.USERNAME = ?5) " +
            "GROUP BY p.ID, p.PROOF_CODE, p.PROOF_NAME, p.PROOF_NAME_EN, p.DOCUMENT_TYPE, a.NAME, a.NAME_EN, p.NUMBER_SIGN, p.RELEASE_DATE, p.SIGNER, p.FIELD, b.NAME, b.NAME_EN, p.RELEASE_BY, u.UNIT_NAME, u.UNIT_NAME_EN, p.DESCRIPTION, p.DESCRIPTION_EN, p.NOTE, p.NOTE_EN, d.name, d.name_en, c.name, c.name_en, pro.PROGRAM_NAME, pro.NAME_EN, p.CREATED_BY, p.CREATED_DATE, p.UPDATED_BY, p.UPDATED_DATE", nativeQuery = true)
    Page<Object[]> doSearchContent(Integer programId, Integer standardId, Integer criteriaId, String keyword, String username, Pageable pageable);

//    Page<Object[]> doSearchWithNavtiveQuery(Integer programId, Integer standardId, Integer criteriaId, String username, Pageable pageable);
    @Query(value = "SELECT new com.ecommerce.core.dto.ProofDTO(p.id, p.proofCode, p.proofName, p.proofNameEn, p.documentType, a.name, a.nameEn, p.numberSign, p.releaseDate, p.signer, p.field, b.name, b.nameEn, p.releaseBy, u.unitName, u.unitNameEn, p.description, p.descriptionEn, p.note, p.noteEn, d.name, d.nameEn, c.name, c.nameEn, pro.name, pro.nameEn, p.createdBy, p.createdDate, p.updatedBy, p.updatedDate, d.name, d.nameEn) " +
            "FROM Proof p LEFT JOIN Directory d ON (p.standardId = d.id) " +
            "LEFT JOIN Criteria  c ON (p.criteriaId = c.id) " +
            "LEFT JOIN Programs  pro ON (p.programId = pro.id) " +
            "LEFT JOIN AppParam  a ON (p.documentType = a.id) " +
            "LEFT JOIN AppParam  b ON (p.field = b.id) " +
            "LEFT JOIN Unit u ON (p.releaseBy = u.id) " +
            "WHERE p.deleted <> 1 AND 1=1 AND p.id = ?1")
    ProofDTO getDetailProof(Integer id);

    @Query(value = "SELECT p FROM Proof p WHERE p.proofName = ?1 AND p.status = 0")
    List<Proof> findByProofName(String proofName);

    @Query(value = "SELECT p FROM Proof p WHERE 1=1 AND p.proofCode = ?1 AND p.deleted <> 1")
    Optional<Proof> findByProofCode(String proofCode);

    @Query(value = "SELECT p FROM Proof p WHERE p.proofCode = ?1 and p.programId = ?2 and p.deleted = 0")
    List<Proof> getAllProofNeedFile(String proofCode, Integer programId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Proof p SET p.exhFile = ?1 WHERE p.id = ?2")
    void updateExhFile(Integer id,Integer proofId);

    @Query(value = "SELECT MAX(p.orderOfStandard) FROM Proof p WHERE p.standardId = ?1 AND p.deleted <> 1")
    Integer getMaxOrderOfStandard(Integer stanId);

    @Query(value = "SELECT MAX(p.orderOfCriteria) FROM Proof p WHERE p.criteriaId = ?1 AND p.deleted <> 1")
    Integer getMaxOrderOfCriteria(Integer criId);

    @Query(value = "SELECT u FROM Unit u JOIN UserInfo ui ON u.id = ui.unit.id WHERE u.deleted <> 1 AND 1=1 AND ui.username = ?1")
    Unit getUnitByUsername(String creater);

    @Query(value = "SELECT p FROM Proof p WHERE p.standardId = ?1 AND p.status = 0")
    List<Proof> findByStandardId(Integer id);

    @Query(value = "SELECT p FROM Proof p WHERE p.criteriaId = ?1 AND p.status = 0")
    List<Proof> findByCriteriaId(Integer id);

    List<Proof> findByReleaseByAndDeletedNot(Integer unitId, Integer deleted);

    Boolean existsByProgramIdAndStandardIdAndCriteriaIdAndProofCodeAndDeleted(Integer programId, Integer standardId, Integer criteriaId, String code, Integer status);

    List<Proof> findByProgramIdAndStandardIdAndProofCodeAndDeleted(Integer programId, Integer standardId, String code, Integer deleted);

    Boolean existsByProgramIdAndStatus(Integer programId, Integer status);

    Boolean existsByCriteriaIdAndStatus(Integer criteriaId, Integer status);

    List<Proof> findByCriteriaIdAndDeletedNot(Integer criteriaId, Integer status);


    @Query(value = "SELECT p FROM Proof p JOIN Criteria c ON p.criteriaId = c.id WHERE 1=1 AND p.deleted <> 1 AND c.delete <> 1 AND c.id = ?1")
    List<Proof> checkExistedByCriteria(Integer criteriaId);

    Boolean existsByStandardIdAndStatus(Integer standardId, Integer status);

    List<Proof> findByStandardIdAndDeletedNot(Integer standardId, Integer status);

    List<Proof> findByProgramIdAndDeletedNot(Integer proId, Integer deleted);

//    List<Proof> findByOrganizationIdAndDeletedNot(Integer organizationId, Integer deleted);

    Page<Proof> findByProgramIdAndExhFileIsNullAndDeletedOrderByUpdatedDateDesc(Integer programId, Integer deleted ,Pageable paging);

    List<Proof> findByDeletedNot(Integer deleted);

    @Query(value = "SELECT p FROM Proof p WHERE 1=1 AND p.deleted <> 1 AND p.documentType = ?1")
    List<Proof> findProofUsingDocumentType(Integer documentTypeId);

    @Query(value = "SELECT p FROM Proof p WHERE 1=1 AND p.deleted <> 1 AND p.field = ?1")
    List<Proof> findProofUsingField(Integer fieldId);

    @Modifying
    @Transient
    @Query(value = "UPDATE proof SET proof_code = REPLACE(proof_code, ?1, ?2) WHERE PROGRAM_ID IN (SELECT p.id FROM program p WHERE p.organization_id = ?3)", nativeQuery = true)
    void updateExhCode(String oldExhCode, String newExhCode, int orgId);

    boolean existsByCriteriaIdAndDeleted(Integer criterionId, Integer deleted);

    boolean existsByDocumentTypeAndDeleted(Integer appParamId, Integer deleted);

    boolean existsByFieldAndDeleted(Integer appParamId, Integer deleted);

    @Query(value = "select   new com.ecommerce.core.dto.AfterTreeNodeByIdDTO(p.id, p.standardId, p.criteriaId, p.proofName) from Proof p WHERE p.deleted = 0 AND (p.programId = ?1)  ORDER BY p.criteriaId, p.id")
    List<AfterTreeNodeByIdDTO> getListProofTreeNode(Integer id);

    List<Proof> findByProofNameAndDeleted(String name, Integer delete);

    @Query(value = "select new com.ecommerce.core.dto.FileProofDTO(ee.id, p.id, ee.fileName) from Proof p join ProofExhFile e on p.id = e.proofId JOIN ExhibitionFile  ee ON ee.id = e.exhFileId WHERE 1=1 and p.deleted = 0 and p.programId = ?1  ")
    List<FileProofDTO> getALLProff(Integer id);

    boolean existsByUnitIdAndDeleted(Integer criterionId, Integer deleted);

    @Query(value = "SELECT p FROM Proof p LEFT JOIN Unit  u ON (p.releaseBy = u.id)   WHERE  u.id = ?1 AND p.deleted <> 1")
    List<Proof> findByUnitId(Integer id);

    @Query(value = "SELECT p FROM Proof p WHERE p.deleted <> 1 AND p.programId = ?1")
    List<Proof> getListProofByProgramId(int id);

    @Query(value = "SELECT p FROM Proof p WHERE p.deleted <> 1 AND p.programId IN (SELECT pr.id FROM Programs pr WHERE pr.organizationId = ?1)")
    List<Proof> findByOrganizationId(Integer id);

}
