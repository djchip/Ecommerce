package com.ecommerce.core.service;

import com.ecommerce.core.constants.TypeEnum;
import com.ecommerce.core.entities.DataSource;
import com.ecommerce.core.entities.Form;
import com.ecommerce.core.exceptions.DetectExcelException;
import com.ecommerce.core.dto.DataLabelDTO;
import com.ecommerce.core.dto.DataSourceDTO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public interface DataSourceService extends IRootService<DataSource> {
    DataSourceDTO importDataSource(MultipartFile file, String createdBy, Integer rowHeaderNumber,
                                   Boolean isForm, String formName, Integer year, Integer formKey, Integer startTitle, HttpServletRequest request) throws IOException, DetectExcelException;

    Form importReport(MultipartFile file, Integer rowStart, Integer rowHeader, String userName) throws IOException, DetectExcelException;
    DataSource getValueCol(TypeEnum typeEnum, Integer id, Integer unitId);

    String getMaxValue(Integer id);

    String getMinValue(Integer id);

    void deleteDatabase(Integer formKey);

    void deleteForm(Integer formKey);

    List<DataSource> getLabel(List<Integer> listId);

    List<DataSource> getList(Integer key, Integer unitId, Integer rowIdx);

    Integer getMinRow(List<Integer> key, Integer unitId);

    Integer getMaxRow(List<Integer> key, Integer unitId);

    List<DataLabelDTO> getLabelByKey(Integer key);

    Integer countRow(Integer key, Integer unitId);

    DataSource getCellValue(Integer key, Integer unitId, Integer colId, Integer rowId);

    List<Integer> getListRow(List<Integer> listKey, Integer unitId);

}
