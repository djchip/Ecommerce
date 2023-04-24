package com.ecommerce.core.service.impl;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.entities.*;
import com.ecommerce.core.exceptions.AppParamBeingUsedException;
import com.ecommerce.core.exceptions.ExistsAppParamException;
import com.ecommerce.core.repositories.AppParamRepository;
import com.ecommerce.core.repositories.DocumentRepository;
import com.ecommerce.core.service.UndoLogService;
import com.google.gson.Gson;
import com.ecommerce.core.dto.AppParamDTO;
import com.ecommerce.core.dto.AppParamExhDTO;
import com.ecommerce.core.dto.DocumentTypeAndFieldDTO;
import com.ecommerce.core.repositories.OrganizationRepository;
import com.ecommerce.core.repositories.ProofRepository;
import com.ecommerce.core.service.AppParamService;
import com.ecommerce.core.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppParamServiceImpl implements AppParamService {
    private static final Integer DELETED = 1;
    private static final Integer UNDO_DELETE = 0;
    private static final String TABLE_NAME = "app_param";
    private static final int FIRST_INDEX = 0;
    private static final Integer UN_ACTIVE_UNDO_LOG = 1;
    @Autowired
    AppParamRepository repository;
    @Autowired
    UndoLogService undoLogService;
    @Autowired
    OrganizationService organizationService;
    @Autowired
    ProofRepository proofRepository;
    @Autowired
    DocumentRepository documentRepository;
    @Autowired
    OrganizationRepository organizationRepository;

    @Override
    public AppParam create(AppParam entity) {
        return repository.save(entity);
    }

    @Override
    public AppParam findById(int id) {
        return repository.findById(id).get();
    }

    @Override
    public AppParam findAppParamByName(String name) {
        Optional<AppParam> entity = repository.findAppParamByName(name);
        return entity.orElse(null);
    }

    @Override
    public AppParam findAppParamByOrganization(Integer organizationId) {
        Optional<AppParam> entity = repository.findAppParamByOrganization(organizationId);
        return entity.orElse(null);
    }

    @Override
    public AppParam deleteAppParam(Integer id) {
        Optional<AppParam> optional = repository.findById(id);

        if (optional.isPresent()) {
            AppParam appParam = optional.get();
            if ("DOCUMENT".equals(appParam.getCode())) {
                List<Proof> listProofUsingDoccimentType = proofRepository.findProofUsingDocumentType(id);
                List<Document> listDocumentUsingDocumentType = documentRepository.findDocumentUsingDocumentType(id);
                if (listDocumentUsingDocumentType.size() > 0 || listProofUsingDoccimentType.size() > 0) {
                    return null;
                } else {
                    appParam.setDeleted(1);
                    return repository.save(appParam);
                }
            }
            if ("FIELD".equals(appParam.getCode())) {
                List<Proof> listProofUsingField = proofRepository.findProofUsingField(id);
                List<Document> listDocumentUsingField = documentRepository.findDocumentUsingField(id);
                if (listProofUsingField.size() > 0 || listDocumentUsingField.size() > 0) {
                    return null;
                } else {
                    appParam.setDeleted(1);
                    return repository.save(appParam);
                }
            }
            if ("EXH_CODE".equals(appParam.getCode())) {
                if (appParam.getOrganizationId() != null) {
                    if (organizationRepository.findByIdAndDeletedNot(appParam.getOrganizationId(), 1) != null) {
                        return null;
                    }
                }
                appParam.setDeleted(1);
                return repository.save(appParam);
            }

        }
        return null;
    }

    @Override
    public List<AppParam> getListNotSelectedFormat(Integer id) {
        return repository.getListNotSelectedFormat(id);
    }

    @Override
    public AppParam getDateFormatSelected() {
        return repository.getDateFormatSelected();
    }

    @Override
    public List<AppParam> findByOrganizationIdOrderByCreatedBy(Integer organizationId) {
        return repository.findByOrganizationIdAndDeletedIsNotOrderByCreatedBy(organizationId, 1);
    }

    @Override
    public  AppParam findByOrganizationId(Integer organizationId) {
        return repository.findByOrganizationId(organizationId);
    }

    @Override
    public AppParam getCodeByOrg(Integer orgId) {
        return repository.getCodeByOrg(orgId);
    }

    @Override
    public AppParam deleteApp(Integer id) throws Exception {
        Optional<AppParam> optional = repository.findById(id);
        if (optional.isPresent()) {
            AppParam userInfo = optional.get();
            userInfo.setDeleted(DELETED);
            userInfo.setUndoStatus(ConstantDefine.STATUS.CAN_BE_UNDO);
            return repository.save(userInfo);
        } else {
            throw new Exception();
        }
    }

    private AppParam undoPut(UndoLog undoLog) {
        Gson g = new Gson();
        return g.fromJson(undoLog.getRevertObject(), AppParam.class);
    }

    @Override
    public void undo(UndoLog undoLog) throws Exception {
        List<UndoLog> undoLogs = undoLogService.findByTableNameAndIdRecordAndStatusNotOrderByCreatedDateDesc(TABLE_NAME, undoLog.getIdRecord(), 1);
        for (UndoLog log : undoLogs) {
            if (log.getId().equals(undoLog.getId())) {
                Optional<AppParam> optional = repository.findById(log.getIdRecord());
                AppParam appParam;
                if (optional.isPresent()) {
                    appParam = optional.get();
                } else {
                    throw new Exception();
                }
                switch (log.getAction()) {
                    case "POST":
                        if(checkBeingUsed(appParam)){
                            throw new AppParamBeingUsedException("App param being used");
                        }
                        repository.deleteById(appParam.getId());
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                    case "DELETE":
                        if (!repository.findByNameAndDeleted(appParam.getName(), 0).isEmpty()) {
                            throw new ExistsAppParamException("Cấu hình còn tồn tại: ");
                        } else {
                            appParam.setDeleted(UNDO_DELETE);
                            log.setStatus(UN_ACTIVE_UNDO_LOG);
                            undoLogService.update(log, log.getId());
                            break;
                        }
                    case "PUT":
                        appParam = undoPut(log);
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                }
                if (!undoLogService.existsByTableNameAndIdRecord(TABLE_NAME, appParam.getId()) && !log.getAction().equals("POST")) {
                    appParam.setUndoStatus(ConstantDefine.STATUS.CAN_NOT_UNDO);
                }
                if (!log.getAction().equals("POST")) {
                    repository.save(appParam);
                }
                break;
            } else {
                log.setStatus(UN_ACTIVE_UNDO_LOG);
                undoLogService.update(log, log.getId());
            }
        }
    }

    private boolean checkBeingUsed(AppParam appParam) {
        if("DOCUMENT".equals(appParam.getCode())) {
            return documentRepository.existsByDocumentTypeAndDelete(appParam.getId(), 0) ||
                    proofRepository.existsByDocumentTypeAndDeleted(appParam.getId(), 0);
        } else if("FIELD".equals(appParam.getCode())) {
            return documentRepository.existsByFieldAndDelete(appParam.getId(), 0) ||
                    proofRepository.existsByFieldAndDeleted(appParam.getId(), 0);
        } else if(appParam.getCode().equals("EXH_CODE")) {
            return organizationRepository.existsByIdAndDeleted(appParam.getOrganizationId(), 0);
        } else {
            return false;
        }
    }

    @Override
    public AppParamDTO formatObj(AppParam entity) {
        AppParamDTO dto = new AppParamDTO(
                entity.getId(), entity.getCode(), entity.getName(), entity.getNameEn(),
                entity.getCreatedBy(), entity.getUpdatedBy(), entity.getCreatedDate(), entity.getUpdatedDate(), entity.getUndoStatus(), entity.getDeleted(), entity.getOrganizationId(),
                null, null
        );
        Optional<Organization> organizationDTO = organizationService.finbyID(entity.getOrganizationId());
        if (organizationDTO.isPresent()) {
            dto.setOrganizaNam(organizationDTO.get().getName());
            dto.setOrganizaNamEn(organizationDTO.get().getNameEn());
        }
        return dto;
    }

    @Override
    public AppParamExhDTO findByIdd(int id) {
        return repository.findByIdd(id);
    }

    @Override
    public List<AppParam> getSelectbox() {
        return repository.findByDeletedNot(1);
    }

    @Override
    public Optional<AppParamDTO> finbyID(Integer id) {
        return repository.finbyID(id);

    }

    @Override
    public boolean deleteAppp(Integer[] ids, Gson g, String createdBy, HttpServletRequest request) throws Exception {
        for (Integer id:ids){
            AppParam entity=deleteAppParam(id);
            if(entity == null){
                return  false;
            }
            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .requestObject(g.toJson(entity, AppParam.class))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Xóa cấu hình bởi " + createdBy)
                    .createdBy(createdBy)
                    .createdDate(LocalDateTime.now())
                    .tableName(TABLE_NAME)
                    .idRecord(entity.getId())
                    .build();
            undoLogService.create(undoLog);
        }
        return true;
    }

    @Override
    public AppParam retrieve(Integer id) {
        return null;
    }

    @Override
    public void update(AppParam entity, Integer id) {

    }

    @Override
    public void delete(Integer id) throws Exception {

    }

    @Override
    public List<DocumentTypeAndFieldDTO> getListDocumentType() {
        return repository.getListDocumentType();
    }

    @Override
    public List<AppParam> getListDateTimeFormat() {
        return repository.getListDateTimeFormat();
    }

    @Override
    public List<DocumentTypeAndFieldDTO> getListField() {
        return repository.getListField();
    }

    @Override
    public List<AppParamDTO> getListExhCode() {
        return repository.getListExhCode();
    }

    @Override
    public AppParam getAppParamById(Integer id) {
        return repository.getAppParamById(id);
    }

    @Override
    public Page<AppParam> doSearchDocumentType(String keyword, Pageable paging) {
        return repository.doSearchDocumentType(keyword, paging);
    }

    @Override
    public Page<AppParam> doSearchField(String keyword, Pageable paging) {
        return repository.doSearchField(keyword, paging);
    }

    @Override
    public Page<AppParam> doSearchExhCode(String keyword, Pageable paging) {
        return repository.doSearchExhCode(keyword, paging);
    }

    @Override
    public Page<AppParam> doSearchDateFormat(String keyword, Pageable paging) {
        return repository.doSearchDateFormat(keyword, paging);
    }

    @Override
    public AppParamDTO findByName(String name) {
        Optional<AppParamDTO> entity = repository.findDocumentTypeByName(name);
        return entity.orElse(null);
    }

    @Override
    public AppParam checkExistedDocumentType(String name) {
        Optional<AppParam> entity = repository.checkExistedDocumentType(name);
        return entity.orElse(null);
    }

    @Override
    public AppParam checkExistedField(String name) {
        Optional<AppParam> entity = repository.checkExistedField(name);
        return entity.orElse(null);
    }

}
