package com.ecommerce.core.repositories;

import com.ecommerce.core.dto.AssessmentDTO;
import com.ecommerce.core.entities.Assessment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Integer> {

    @Query(value = "SELECT a FROM Assessment a WHERE a.temp = 0 AND  (?1 IS NULL OR lower(a.name) like %?1% OR lower(a.nameEn) like %?1% OR lower(a.description) like %?1% OR lower(a.descriptionEn) like %?1% OR lower(a.content) like %?1%) and a.delete <>1  ORDER BY a.updatedDate DESC")
    Page<Assessment> doSearch(String keyword, String user, Integer reporttype, Pageable paging);

    @Query(value = "SELECT a FROM Assessment a WHERE a.temp = 0 AND  (?1 IS NULL OR lower(a.name) like %?1% OR lower(a.nameEn) like %?1% OR lower(a.description) like %?1% OR lower(a.descriptionEn) like %?1% OR lower(a.content) like %?1%)  AND (?2 is null or a.programId = ?2)  AND (?3 is null or a.directoryId = ?3) AND (?4 is null or a. criteriaId = ?4) and a.reportType=?5 and a.delete <>1  ORDER BY a.updatedDate DESC")
    Page<Assessment> doSearch1(String keyword, Integer programId, Integer directoryId, Integer criteriaId, Integer reporttype, String user, Pageable paging);

    @Query(value = "SELECT DISTINCT new com.ecommerce.core.dto.AssessmentDTO(a.id, a.name, a.nameEn, a.description, a.descriptionEn, a.createdBy, a.createdDate, a.updatedBy, a.updatedDate,a.evaluated,a.file, a.comment, a.reportType , p.id, p.name, p.nameEn, d.id, d.name, d.nameEn, c.id, c.name, c.nameEn) FROM Assessment a inner JOIN AssessmentExperts ae on (a.id=ae.assessmentId and ?1=ae.userId)  left join Programs p on (a.programId = p.id ) left join Directory d on ( a.directoryId=d.id) left join Criteria  c on (a.criteriaId= c.id ) WHERE a.temp = 0 AND (?2 IS NULL OR lower(a.name) like %?2% OR lower(a.nameEn) like %?2% OR lower(a.description) like %?2% OR lower(a.descriptionEn) like %?2%) "
            + "AND (?3 IS NULL OR a.programId = ?3) " +
            "ORDER BY a.updatedDate DESC")
    Page<AssessmentDTO> doSearchEN(Integer userId, String keyword, Integer programId, Pageable paging);

    @Query(value = "SELECT new com.ecommerce.core.dto.AssessmentDTO(a.id, a.name, a.nameEn, a.description, a.descriptionEn, a.createdBy, a.createdDate, a.updatedBy, a.updatedDate,a.evaluated,a.file, a.comment, a.reportType, p.id, p.name, p.nameEn, d.id, d.name, d.nameEn, c.id, c.name, c.nameEn) FROM Assessment a left join Programs p on (a.programId = p.id ) left join Directory d on ( a.directoryId=d.id) left join Criteria  c on (a.criteriaId= c.id )WHERE a.id = ?1")
    Optional<AssessmentDTO> getDetailVN(Integer id);

    @Query(value = "SELECT new com.ecommerce.core.dto.AssessmentDTO(a.id, a.name, a.nameEn, a.description, a.descriptionEn, a.createdBy, a.createdDate, a.updatedBy, a.updatedDate,a.evaluated,a.file, a.comment, a.reportType, p.id, p.name, p.nameEn, d.id, d.name, d.nameEn, c.id, c.name, c.nameEn) FROM Assessment a left join Programs p on (a.programId = p.id ) left join Directory d on ( a.directoryId=d.id) left join Criteria  c on (a.criteriaId= c.id ) WHERE a.id = ?1")
    Optional<AssessmentDTO> getDetailEN(Integer id);

    @EntityGraph(attributePaths = {Assessment.Fields.name})
    Optional<Assessment> findByNameAndDelete(String name, Integer deleted);

    @Query(value = "SELECT new com.ecommerce.core.dto.AssessmentDTO(a.id, a.name, a.nameEn, a.description, a.descriptionEn, a.createdBy, a.createdDate, a.updatedBy, a.updatedDate,a.evaluated,a.file, a.comment, a.reportType , p.id, p.name, p.nameEn, d.id, d.name, d.nameEn, c.id, c.name, c.nameEn)" +
            " FROM Assessment a  inner JOIN AssessmentExperts ae on (a.id=ae.assessmentId and ?1=ae.userId) left join Programs p on (a.programId = p.id ) left join Directory d on ( a.directoryId=d.id) left join Criteria  c on (a.criteriaId= c.id ) " +
            "WHERE a.temp = 0 AND (?2 IS NULL OR lower(a.name) like %?2% OR lower(a.nameEn) like %?2% OR lower(a.description) like %?2% OR lower(a.descriptionEn) like %?2%) AND (?3 IS NULL OR a.programId = ?3) " +
            " AND a.delete <> 1 ORDER BY a.updatedDate DESC")
    Page<AssessmentDTO> doSearch(Integer userId, String keyword, Integer programId, Pageable paging);

    @Query(value = "SELECT a FROM Assessment a WHERE a.temp = 0 AND (?1 IS NULL OR lower(a.name) like %?1% OR lower(a.nameEn) like %?1% OR lower(a.description) like %?1% OR lower(a.descriptionEn) like %?1%) AND (?2 is null or a.programId = ?2)  AND (?3 is null or a.directoryId = ?3) AND (?4 is null or a.directoryId = ?4)and a.reportType=?5 and a.delete != 1 ORDER BY a.updatedDate DESC")
    Page<Assessment> doSearchEN(String keyword, Integer programId, Integer directoryId, Integer criteriaId, Integer reporttype, String user, Pageable paging);

    @Query(value = "SELECT ifnull(MAX(order_ass), 0) FROM assessment", nativeQuery = true)
    Integer getMaxOrder();

    @Query(value = "SELECT a FROM Assessment a WHERE a.temp = 1")
    List<Assessment> getAssessmentTemp();

    @Query(value = "SELECT a.id FROM Assessment a WHERE  a.createdBy = ?1 and a.delete !=1")
    List<Integer> getAssessmentByCreatedBy(String name);
}
