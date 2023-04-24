package com.ecommerce.core.repositories;

import com.ecommerce.core.dto.IdFileDTO;
import com.ecommerce.core.dto.ListDocumentDTO;
import com.ecommerce.core.entities.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {

    /*@Query(value = "SELECT d FROM Document d WHERE 1=1 AND (?1 IS NULL OR lower(d.documentNumber) like %?1% OR lower(d.documentName) like %?1% OR lower(d.description) like %?1%) and d.delete !=1 ORDER BY d.updatedDate desc")
    Page<Document> doSearch(String keyword, Pageable paging);*/

    @Query(value = "SELECT new com.ecommerce.core.dto.ListDocumentDTO(d.id, d.documentNumber, d.documentName, d.documentNameEn, d.documentType, a.name, a.nameEn, d.releaseDate, d.signer, d.field, b.name, b.nameEn, d.releaseBy, u.unitName, u.unitNameEn, d.description, d.descriptionEn, d.createdBy, d.createdDate, d.updatedBy, d.updatedDate) " +
            "FROM Document d LEFT JOIN AppParam  a ON (d.documentType = a.id) " +
            "LEFT JOIN AppParam  b ON (d.field = b.id) " +
            "LEFT JOIN Unit  u ON (d.releaseBy = u.id) " +
            " WHERE d.delete <> 1 AND 1=1 AND (d.documentName LIKE %?1% OR d.documentNumber LIKE %?1%) ORDER BY d.updatedDate desc")
    Page<ListDocumentDTO> doSearch(String keyword, Pageable paging);

    @Query(value = "SELECT new com.ecommerce.core.dto.ListDocumentDTO(d.id, d.documentNumber, d.documentName, d.documentNameEn, d.documentType, a.name, a.nameEn, d.releaseDate, d.signer, d.field, b.name, b.nameEn, d.releaseBy, u.unitName, u.unitNameEn, d.description, d.descriptionEn, d.createdBy, d.createdDate, d.updatedBy, d.updatedDate) " +
            "FROM Document d LEFT JOIN AppParam  a ON (d.documentType = a.id) " +
            "LEFT JOIN AppParam  b ON (d.field = b.id) " +
            "LEFT JOIN Unit  u ON (d.releaseBy = u.id) " +
            " WHERE 1=1 AND d.delete <> 1 AND d.releaseBy = ?1 ORDER BY d.updatedDate desc")
    Page<ListDocumentDTO> doSearchByUnit(Integer unitId, Pageable paging);

    @Query(value = "SELECT ANY_VALUE(d.DOCUMENT_NUMBER), d.DOCUMENT_NAME, ANY_VALUE(d.DOCUMENT_NAME_EN), ANY_VALUE(d.DESCRIPTION), ANY_VALUE(d.DESCRIPTION_EN), group_concat(df.file_name), ANY_VALUE(ap.name) as 'type', ANY_VALUE(a.name) as 'field', ANY_VALUE(d.RELEASE_DATE) " +
            "FROM document d left outer join document_file df ON d.ID = df.document_id " +
            "left outer join app_param a ON d.FIELD = a.id " +
            "left outer join app_param ap ON d.DOCUMENT_TYPE = ap.id " +
            "where d.DELETED <> 1 and d.RELEASE_BY = ?1 group by DOCUMENT_NAME", nativeQuery = true)
    List<Object[]> getListByUnit(Integer unitId);

    @EntityGraph(attributePaths = {Document.Fields.documentNumber})
    public Optional<Document> findByDocumentNumber(String documentNumber);

    @Query(value = "SELECT d FROM Document d WHERE d.delete <> 1 AND d.documentName = ?1")
    public Document findByDocumentName(String documentName);

    @Query(value = "SELECT new com.ecommerce.core.dto.ListDocumentDTO(d.id, d.documentNumber, d.documentName, d.documentNameEn, d.documentType, a.name, a.nameEn, d.releaseDate, d.signer, d.field, b.name, b.nameEn, d.releaseBy, u.unitName, u.unitNameEn, d.description, d.descriptionEn, d.createdBy, d.createdDate, d.updatedBy, d.updatedDate) " +
            "FROM Document d LEFT JOIN AppParam  a ON (d.documentType = a.id) " +
            "LEFT JOIN AppParam  b ON (d.field = b.id) " +
            "LEFT JOIN Unit  u ON (d.releaseBy = u.id) " +
            " WHERE d.delete <> 1 AND 1=1 AND d.id = ?1")
    ListDocumentDTO getDetailDocument(Integer id);

    @Query(value = "SELECT new com.ecommerce.core.dto.ListDocumentDTO(d.id, d.documentNumber, d.documentName, d.documentNameEn, d.documentType, a.name, a.nameEn, d.releaseDate, d.signer, d.field, b.name, b.nameEn, d.releaseBy, u.unitName, u.unitNameEn, d.description, d.descriptionEn, d.createdBy, d.createdDate, d.updatedBy, d.updatedDate) " +
            "FROM Document d LEFT JOIN AppParam  a ON (d.documentType = a.id) " +
            "LEFT JOIN AppParam  b ON (d.field = b.id) " +
            "LEFT JOIN Unit  u ON (d.releaseBy = u.id) " +
            " WHERE d.delete <> 1 AND 1=1 ORDER BY d.updatedDate desc")
    List<ListDocumentDTO> getListDocumentDTO();

    @Query(value = "SELECT d FROM Document d WHERE 1=1 AND d.delete <> 1 AND d.documentType = ?1")
    List<Document> findDocumentUsingDocumentType(Integer documentTypeId);

    @Query(value = "SELECT d FROM Document d WHERE 1=1 AND d.delete <> 1 AND d.field = ?1")
    List<Document> findDocumentUsingField(Integer fieldId);

    List<Document> findByDocumentNameAndDelete(String name, Integer Delete);

    boolean existsByFieldAndDelete(Integer appParamId, Integer deleted);

    boolean existsByDocumentTypeAndDelete(Integer appParamId, Integer deleted);

    @Query(value = "SELECT df.fileName FROM Document d JOIN DocumentFile df ON d.id = df.documentId WHERE d.delete <> 1 AND d.id = ?1")
    String getFilenameById(Integer id);

    @Query(value = "SELECT new com.ecommerce.core.dto.IdFileDTO(d.id, df.fileName) FROM Document d JOIN DocumentFile df ON d.id = df.documentId WHERE d.delete <> 1 AND d.id = ?1")
    IdFileDTO getListIdAndFilename(Integer id);
}
