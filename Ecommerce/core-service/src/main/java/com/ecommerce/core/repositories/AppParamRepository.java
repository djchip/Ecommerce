package com.ecommerce.core.repositories;

import com.ecommerce.core.entities.AppParam;
import com.ecommerce.core.dto.AppParamDTO;
import com.ecommerce.core.dto.AppParamExhDTO;
import com.ecommerce.core.dto.DocumentTypeAndFieldDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppParamRepository extends JpaRepository<AppParam, Integer> {

    @Query(value = "SELECT new com.ecommerce.core.dto.DocumentTypeAndFieldDTO(a.id, a.code, a.name, a.nameEn, a.createdBy, a.updatedBy, a.createdDate, a.updatedDate) FROM AppParam a WHERE a.deleted <> 1 AND a.code = 'DOCUMENT'")
    List<DocumentTypeAndFieldDTO> getListDocumentType();

    @Query(value = "SELECT new com.ecommerce.core.dto.DocumentTypeAndFieldDTO(a.id, a.code, a.name, a.nameEn, a.createdBy, a.updatedBy, a.createdDate, a.updatedDate) FROM AppParam a WHERE a.deleted <> 1 AND a.code = 'FIELD'")
    List<DocumentTypeAndFieldDTO> getListField();

    List<AppParam> findByNameAndDeleted(String name, Integer delete);

    @Query(value = "SELECT new com.ecommerce.core.dto.AppParamExhDTO(a.id, a.code, a.name, a.nameEn, a.createdBy, a.updatedBy, a.createdDate, a.updatedDate, o.id, o.name, o.nameEn) FROM AppParam a JOIN Organization o ON a.organizationId = o.id WHERE 1=1 AND a.deleted <> 1 AND (a.code = 'EXH_CODE') AND (?1 IS NULL OR lower(a.name) like %?1%) and a.code = 'EXH_CODE' ORDER BY a.updatedDate desc")
    List<AppParamDTO> getListExhCode();

    @Query(value = "SELECT a FROM AppParam a WHERE a.id = ?1")
    AppParam getAppParamById(Integer id);

    @Query(value =

            "SELECT a FROM AppParam a WHERE 1=1 AND a.deleted <> 1 AND (a.code = 'DOCUMENT') AND (?1 IS NULL OR lower(a.name) like %?1%) ORDER BY a.updatedDate desc")
    Page<AppParam> doSearchDocumentType(String keyword, Pageable paging);

    @Query(value = "SELECT a FROM AppParam a WHERE 1=1 AND a.deleted <> 1 AND (a.code = 'FIELD') AND (?1 IS NULL OR lower(a.name) like %?1%) ORDER BY a.updatedDate desc")
    Page<AppParam> doSearchField(String keyword, Pageable paging);

    @Query(value = "SELECT new com.ecommerce.core.dto.AppParamExhDTO(a.id, a.code, a.name, a.nameEn, a.createdBy, a.updatedBy, a.createdDate, a.updatedDate, o.id, o.name, o.nameEn) FROM AppParam a JOIN Organization o ON a.organizationId = o.id WHERE 1=1 AND a.deleted <> 1 AND (a.code = 'EXH_CODE') AND (?1 IS NULL OR lower(a.name) like %?1%) ORDER BY a.updatedDate desc")
    Page<AppParam> doSearchExhCode(String keyword, Pageable paging);

    @Query(value = "SELECT a FROM AppParam a WHERE 1=1 AND a.deleted <> 1 AND (a.code = 'DATE_FORMAT') AND (?1 IS NULL OR lower(a.name) like %?1%) ORDER BY a.updatedDate desc")
    Page<AppParam> doSearchDateFormat(String keyword, Pageable paging);

    @Query(value = "SELECT new com.ecommerce.core.dto.AppParamExhDTO(a.id, a.code, a.name, a.nameEn, a.createdBy, a.updatedBy, a.createdDate, a.updatedDate, o.id, o.name, o.nameEn) FROM AppParam a JOIN Organization o ON a.organizationId = o.id WHERE 1=1 AND a.deleted <> 1 AND (a.code = 'EXH_CODE') AND (?1 IS NULL OR lower(a.name) like %?1%)  and a.name = ?1 ORDER BY a.updatedDate desc")
    Optional<AppParamDTO> findDocumentTypeByName(String name);

    @Query(value = "SELECT a FROM AppParam a WHERE a.deleted <> 1 AND (a.code = 'DOCUMENT') AND a.name = ?1")
    Optional<AppParam> checkExistedDocumentType(String name);

    @Query(value = "SELECT a FROM AppParam a WHERE a.deleted <> 1 AND (a.code = 'FIELD') AND a.name = ?1")
    Optional<AppParam> checkExistedField(String name);

    @Query(value = "SELECT a FROM AppParam a WHERE 1=1 AND a.deleted <> 1 AND a.name = ?1")
    Optional<AppParam> findAppParamByName(String name);

    @Query(value = "SELECT a FROM AppParam a WHERE 1=1 AND a.deleted <> 1 AND a.organizationId = ?1")
    Optional<AppParam> findAppParamByOrganization(Integer organizationId);

    List<AppParam> findByOrganizationIdAndDeletedIsNotOrderByCreatedBy(Integer organizationId, Integer deleted);

    @Query(value = "SELECT a FROM AppParam a WHERE 1=1 AND a.deleted <> 1 AND a.code = 'DATE_FORMAT'")
    List<AppParam> getListDateTimeFormat();

    @Query(value = "SELECT a FROM AppParam a WHERE 1=1 AND a.deleted <> 1 AND a.id <> ?1")
    List<AppParam> getListNotSelectedFormat(Integer id);

    @Query(value = "SELECT a FROM AppParam a WHERE 1=1 AND a.deleted <> 1 AND a.selectedFormat = 1")
    AppParam getDateFormatSelected();

    @Query(value = "SELECT a FROM AppParam a WHERE a.organizationId = ?1 AND a.deleted <> 1")
    AppParam getCodeByOrg(Integer orgId);

    @Query(value = "SELECT new com.ecommerce.core.dto.AppParamExhDTO(a.id, a.code, a.name, a.nameEn, a.createdBy, a.updatedBy, a.createdDate, a.updatedDate, o.id, o.name, o.nameEn) FROM AppParam a JOIN Organization o ON a.organizationId = o.id WHERE 1=1 AND a.deleted <> 1 and a.id = ?1")
    AppParamExhDTO findByIdd(Integer id);

    @Query(value = "SELECT new com.ecommerce.core.dto.AppParamExhDTO(a.id, a.code, a.name, a.nameEn, a.createdBy, a.updatedBy, a.createdDate, a.updatedDate, o.id, o.name, o.nameEn) FROM AppParam a JOIN Organization o ON a.organizationId = o.id WHERE 1=1 AND a.deleted <> 1 AND (a.code = 'EXH_CODE') AND (?1 IS NULL OR lower(a.name) like %?1%) and a.code = 'EXH_CODE' ORDER BY a.updatedDate desc")

    Optional<AppParamDTO> finbyID(Integer id);

    List<AppParam> findByDeletedNot(Integer deleted);

    @Query(value = "SELECT a FROM AppParam a JOIN Organization o ON a.organizationId = o.id WHERE 1=1 and o.id = ?1 AND a.deleted <> 1 AND (a.code = 'EXH_CODE')")
    AppParam findByOrganizationId(Integer organizationId);
}
