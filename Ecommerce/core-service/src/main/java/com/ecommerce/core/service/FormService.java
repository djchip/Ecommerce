package com.ecommerce.core.service;


import com.ecommerce.core.entities.Form;
import com.ecommerce.core.exceptions.DetectExcelException;
import com.ecommerce.core.dto.DetailFormDTO;
import com.ecommerce.core.dto.FormCopyDTO;
import com.ecommerce.core.dto.FormDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Service
public interface FormService extends IRootService<Form> {
    List<Form> listAll();

    Page<Form> doSearch(String keyword, Integer yearOfApplication, String uploadTime, Pageable pageable) throws ParseException;

    Page<FormDTO> doSearchDataBase(String keyword, Integer yearOfApplication, String uploadTime, Integer unitId, Pageable pageable) throws ParseException;

    Page<Form> doSearchByUnit(Integer unitId, Pageable pageable);

    List<Form> save(List<Form> formList);

    void save(Form entity);

    Form findById(int id);

    Form deleteForm(Integer id) throws Exception;

    Boolean deleteMulti(List<Form> forms) throws IOException;

    void copyMulti(List<Form> forms, String user, HttpServletRequest request) throws IOException, DetectExcelException;

    void deleteCSDL(List<FormDTO> dtos) throws IOException;

    Form findByFormKey(Integer formKey);

    List<Form> listFormUploaded(Integer year);

    List<Form> totalForm(Integer year);

    List<Form> listFormNotUploaded(Integer year);

    Integer getMaxFormKey();

    void doUpdate(Form entity, Integer id,String updateBy);
    void updateWithoutFile(Form entity, Integer id,String updateBy);

    DetailFormDTO getDetailForm(Integer id, Integer unitId);

    Form doCopyForm(FormCopyDTO dto, String usernameFormRequest, HttpServletRequest request) throws IOException, DetectExcelException;
}
