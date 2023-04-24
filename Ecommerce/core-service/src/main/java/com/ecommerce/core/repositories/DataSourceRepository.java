package com.ecommerce.core.repositories;

import com.ecommerce.core.constants.TypeEnum;
import com.ecommerce.core.entities.DataSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataSourceRepository extends JpaRepository<DataSource, Integer> {
    @Query("select max(ds.formKey) from DataSource ds")
    Integer getMaxKey();

    @Query(value = "SELECT d FROM DataSource d WHERE d.type = ?1 AND d.colIdx = (SELECT ds.colIdx FROM DataSource ds WHERE ds.id = ?2) AND d.createdUnit = ?3 AND d.formKey = (SELECT dk.formKey FROM DataSource dk WHERE dk.id = ?2)")
    DataSource getValueCol(TypeEnum typeEnum, Integer id, Integer unitId);

    @Query(value = "SELECT ifnull(MAX(VALUE), 0) FROM data_source d WHERE d.COL_IDX = (SELECT ds.COL_IDX FROM data_source ds WHERE ds.ID = ?1)", nativeQuery = true)
    String getMaxValue(Integer id);

    @Query(value = "SELECT ifnull(MIN(VALUE), 0) FROM data_source d WHERE d.COL_IDX = (SELECT ds.COL_IDX FROM data_source ds WHERE ds.ID = ?1)", nativeQuery = true)
    String getMinValue(Integer id);

    void deleteByFormKeyAndType(Integer formKey, TypeEnum type);

    void deleteByFormKey(Integer formKey);

    void deleteByFormKeyAndCreatedUnitAndTypeIsNot(Integer formKey, Integer unitId, TypeEnum type);

    @Query(value = "SELECT d FROM DataSource d WHERE d.type = ?1 AND d.formKey in (SELECT ds.formKey FROM Form ds WHERE ds.id in ?2)")
    List<DataSource> findByTypeAndFormKeyIn(TypeEnum typeEnum, List<Integer> listId);

    @Query(value = "SELECT d FROM DataSource d WHERE d.formKey = ?1 AND d.createdUnit = ?2 AND d.type = 1 AND d.rowIdx = ?3 ORDER BY d.colIdx asc")
    List<DataSource> getList(Integer key, Integer unitId, Integer rowIdx);

    @Query(value = "SELECT min(d.rowIdx) FROM DataSource d WHERE d.formKey in ?1 AND d.createdUnit = ?2 AND d.type = 1")
    Integer getMinRow(List<Integer> key, Integer unitId);

    @Query(value = "SELECT max(d.rowIdx) FROM DataSource d WHERE d.formKey in ?1 AND d.createdUnit = ?2 AND d.type = 1")
    Integer getMaxRow(List<Integer> key, Integer unitId);

    @Query(value = "SELECT d.id, group_concat(d.VALUE), d.COL_IDX FROM data_source d where FORM_KEY = ?1 AND TYPE =0 group by COL_IDX", nativeQuery = true)
    List<Object[]> getLabelByKey(Integer key);

    @Query(value = "SELECT count(distinct d.rowIdx) FROM DataSource d WHERE d.formKey = ?1 AND d.createdUnit = ?2 AND d.type = 1")
    Integer countRow(Integer key, Integer unitId);

    @Query(value = "SELECT d FROM DataSource d WHERE d.type = 1 AND d.formKey = ?1 AND d.createdUnit = ?2 AND d.colIdx = ?3 AND d.rowIdx = ?4")
    DataSource getValueCell(Integer key, Integer unitId, Integer colId, Integer rowId);

    @Query(value = "SELECT distinct d.rowIdx FROM DataSource d WHERE d.formKey in ?1 AND d.createdUnit = ?2 AND d.type = 1")
    List<Integer> getListRow(List<Integer> listKey, Integer unitId);
}
