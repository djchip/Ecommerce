package com.ecommerce.core.repositories;


import com.ecommerce.core.entities.Form;
import com.ecommerce.core.dto.FormDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface FormRepository extends JpaRepository<Form,Integer> {

    @Query(value = "SELECT new com.ecommerce.core.dto.FormDTO(u.id, u.name, u.nameEn, u.fileName, u.uploadTime, u.deleted, u.yearOfApplication, d.status, u.pathFile, u.numTitle, u.formKey, d.pathFile, d.unitId) " +
            "FROM Form u JOIN FileDatabase d ON u.formKey = d.formKey WHERE 1=1 AND (?4 is null or d.unitId = ?4) AND (?1 is null or lower(u.name) like %?1%) AND u.deleted <> 1 " +
            "AND (?2 IS NULL or u.yearOfApplication = ?2) AND (?3 IS NULL OR u.uploadTime = ?3) ORDER BY coalesce(u.updateDate, u.createDate) desc")
    Page<FormDTO> doSearch(String keyword, Integer yearOfApplication, Date uploadTime, Integer unitId, Pageable paging);


    @Query(value = "SELECT u " +
            "FROM Form u where 1=1 and (?1 is null or lower(u.name) like %?1%) AND u.deleted <> 1 " +
            "AND (?2 IS NULL or u.yearOfApplication = ?2) AND (?3 IS NULL OR u.uploadTime = ?3) ORDER BY coalesce(u.updateDate, u.createDate) desc")
    Page<Form> doSearch(String keyword, Integer yearOfApplication, Date uploadTime, Pageable paging);

    @Query(value = "SELECT f FROM Form f JOIN FormUnit fu ON f.id = fu.formId JOIN Unit u ON u.id = fu.unitId WHERE 1=1 AND (?1 IS NULL OR lower(f.description) like %?1% OR lower(f.name) like %?1%) AND f.deleted <> 1 " +
            "AND (?2 IS NULL or f.yearOfApplication = ?2) AND (?3 IS NULL OR f.uploadTime = ?3) AND u.id = (SELECT ui.unit.id FROM UserInfo ui WHERE ui.username = ?4) ORDER BY coalesce(f.updateDate, f.createDate) desc")
    Page<Form> doSearchDataBase(String keyword, Integer yearOfApplication, Date uploadTime, String username, Pageable paging);

    @Query(value = "SELECT f FROM Form f WHERE 1=1 AND f.deleted <> 1 AND (SELECT u FROM Unit u WHERE u.id = ?1 AND u.deleted <> 1) MEMBER f.units ORDER BY f.updateDate desc")
    Page<Form> doSearchByUnit(Integer unitId, Pageable paging);

    @Query(value = "SELECT f.form_name, ANY_VALUE(f.form_name_en), ANY_VALUE(f.form_des), ANY_VALUE(f.form_des_en), ANY_VALUE(f.file_name), ANY_VALUE(u.UNIT_NAME), group_concat(o.name), ANY_VALUE(f.updated_date) FROM form f left outer join form_unit fu ON f.form_id = fu.form_id AND fu.unit_id = ?1" +
            " left outer join unit u ON fu.unit_id = u.id" +
            " left outer join form_object fo ON f.form_id = fo.form_id" +
            " left outer join database_object o ON fo.object_id = o.id" +
            " where f.delete <> 1 and u.UNIT_NAME is not null group by f.form_name", nativeQuery = true)
    List<Object[]> getListByUnit(Integer unitId);

    @Query(value = "Select f from Form f where f.name like %?1%")
    Form findByName(String Name);
    @Query(value = "Select f from Form f where f.name like %?1% and  f.deleted <> 1 " )
    List<Form> findByForm_nameAndDelete(String name, Integer deleted);

    @Query(value = "Select f from Form f where f.deleted <> 1 " )
    List<Form> findByExport();

    @Query(value = "SELECT f FROM Form f WHERE f.formKey = ?1 AND f.deleted <> 1")
    Form findByFormKey(Integer formKey);

    @Query(value = "SELECT f FROM Form f WHERE f.status = 1 AND YEAR(f.createDate) = ?1")
    List<Form> listFormUploaded(Integer year);

    @Query(value = "SELECT f FROM Form f WHERE f.deleted <> 1 AND YEAR(f.createDate) = ?1")
    List<Form> totalForm(Integer year);

    @Query(value = "SELECT f FROM Form f JOIN FileDatabase fb ON f.formKey = fb.formKey WHERE fb.status = 0 AND YEAR(f.createDate) = ?1 group by fb.formKey")
    List<Form> listFormNotUploaded(Integer year);

    @Query("select max(f.formKey) from Form f")
    Integer getMaxFormKey();
}
