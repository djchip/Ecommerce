package com.ecommerce.core.service;

import com.ecommerce.core.entities.AppParam;
import com.ecommerce.core.entities.UndoLog;
import com.google.gson.Gson;
import com.ecommerce.core.dto.AppParamDTO;
import com.ecommerce.core.dto.AppParamExhDTO;
import com.ecommerce.core.dto.DocumentTypeAndFieldDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface AppParamService extends IRootService<AppParam>{
    List<DocumentTypeAndFieldDTO> getListDocumentType();

    List<AppParam> getListDateTimeFormat();

    List<DocumentTypeAndFieldDTO> getListField();
    List<AppParamDTO> getListExhCode();

    AppParam getAppParamById(Integer id);
    Page<AppParam> doSearchDocumentType(String keyword, Pageable paging);

    Page<AppParam> doSearchField(String keyword, Pageable paging);

    Page<AppParam> doSearchExhCode(String keyword, Pageable paging);

    Page<AppParam> doSearchDateFormat(String keyword, Pageable paging);

    AppParamDTO findByName(String name);

    AppParam checkExistedDocumentType(String name);

    AppParam checkExistedField(String name);

    AppParam create(AppParam entity);

    AppParam findById(int id);

    AppParam findAppParamByName(String name);

    AppParam findAppParamByOrganization(Integer organizationId);

    AppParam deleteAppParam (Integer id);


    List<AppParam> getListNotSelectedFormat(Integer id);

    AppParam getDateFormatSelected();

    List<AppParam> findByOrganizationIdOrderByCreatedBy(Integer organizationId);

    AppParam findByOrganizationId(Integer organizationId);

    AppParam getCodeByOrg(Integer orgId);

    AppParam deleteApp(Integer id) throws Exception;
    void undo(UndoLog undoLog) throws Exception;

    AppParamDTO formatObj(AppParam entity);

    AppParamExhDTO findByIdd(int id);

    List<AppParam> getSelectbox();
    Optional<AppParamDTO> finbyID(Integer id);

    boolean deleteAppp(Integer[] ids, Gson g, String createdBy, HttpServletRequest request) throws Exception;

}
