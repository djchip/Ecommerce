package com.ecommerce.core.repositories;

import com.ecommerce.core.entities.FileDatabase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FileDatabaseRepository extends JpaRepository<FileDatabase, Integer> {
    FileDatabase findByFormKeyAndUnitId(Integer formKey, Integer unitId);
    void deleteByFormKey(Integer formKey);
    List<FileDatabase> findByFormKey(Integer formKey);

    @Query(value = "SELECT f FROM FileDatabase f JOIN Form fo ON f.formKey = fo.formKey WHERE f.status = 1 AND f.formKey = ?1 AND f.unitId = ?2 AND fo.id = ?3 ")
    FileDatabase findByFormKeyAndUnitIdAndFormId(Integer formKey, Integer unitId, Integer formId);


    @Transactional
    @Modifying
    @Query(value = "DELETE FROM FileDatabase f WHERE f.formKey = ?1 AND f.unitId = ?2 ")
    void deleteByIdAndFormKey(Integer formKey, Integer unitId);

}
