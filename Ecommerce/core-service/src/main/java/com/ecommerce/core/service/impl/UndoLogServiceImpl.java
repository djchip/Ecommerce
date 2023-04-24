package com.ecommerce.core.service.impl;

import com.ecommerce.core.constants.TableNameDefine;
import com.ecommerce.core.entities.UndoLog;
import com.ecommerce.core.entities.Unit;
import com.ecommerce.core.repositories.UndoLogRepository;
import com.ecommerce.core.service.*;
import com.ecommerce.core.dto.UnitDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UndoLogServiceImpl implements UndoLogService {

    @Autowired
    UndoLogRepository repository;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    DirectoryService directoryService;
    @Autowired
    CriteriaService criteriaService;
    @Autowired
    ProgramsService programsService;
    @Autowired
    CategoriesService categoriesService;
    @Autowired
    SoftwareLogService service;
    @Autowired
    DocumentService documentService;
    @Autowired
    FormService formService;
    @Autowired
    RolesService rolesService;
    @Autowired
    OrganizationService organizationService;
    @Autowired
    AssessmentService assessmentService;
    @Autowired
    ProofService proofService;
    @Autowired
    UnitService unitService;

    @Autowired
    AppParamService appParamService;

    @Override
    public UndoLog create(UndoLog entity) {
        return repository.save(entity);
    }

    @Override
    public UndoLog retrieve(Integer id) {
        return null;
    }

    @Override
    public void update(UndoLog entity, Integer id) {
        repository.save(entity);
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public List<UndoLog> findByRequestObjectStartsWithOrderByCreatedDateDesc(String requestObject) {
        return repository.findByRequestObjectStartsWithOrderByCreatedDateDesc(requestObject);
    }

    @Override
    public List<UndoLog> findByTableNameAndIdRecordAndStatusNotOrderByCreatedDateDesc(String tableName, Integer idRecord, Integer status) {
        return repository.findByTableNameAndIdRecordAndStatusNotOrderByCreatedDateDesc(tableName, idRecord, status);
    }

    @Override
    public Boolean existsByTableNameAndIdRecord(String tableName, Integer idRecord) {
        return repository.existsByTableNameAndIdRecord(tableName,idRecord);
    }

    @Override
    public Page<UndoLog> doSearch(String tableName, String action, LocalDateTime startDate, LocalDateTime endDate, String createdBy, Pageable paging) {
        return repository.doSearch(tableName, action, startDate, endDate, createdBy, paging);
    }

    @Override
    public void filterAndRedirect(UndoLog undoLog) throws Exception {
        if(undoLog == null || undoLog.getTableName() == null) {
            throw new Exception();
        }
        switch (undoLog.getTableName()) {
            case TableNameDefine.USER_INFO:
                userInfoService.undo(undoLog);
                break;
            case TableNameDefine.DIRECTORY:
                directoryService.undo(undoLog);
                System.out.println("Redirect to directory service");
                break;
            case TableNameDefine.CRITERIA:
                criteriaService.undo(undoLog);
                System.out.println("Redirect to criteria service");
                break;
            case TableNameDefine.PROGRAM:
                programsService.undo(undoLog);
                System.out.println("Redirect to program service");
                break;
            case TableNameDefine.CATEGORIES:
                categoriesService.undo(undoLog);
                System.out.println("Redirect to program service");
                break;
            case TableNameDefine.SOFTWARE_LOG:
                service.undo(undoLog);
                System.out.println("Redirect to software log service");
                break;
            case TableNameDefine.DOCUMENT:
                documentService.undo(undoLog);
                break;
            case TableNameDefine.ROLE:
                rolesService.undo(undoLog);
                break;
            case TableNameDefine.UNIT:
                unitService.undo(undoLog);
                break;
            case TableNameDefine.PROOF:
                proofService.undo(undoLog);
                break;
            case TableNameDefine.ORGANIZATION:
                organizationService.undo(undoLog);
                break;
            case TableNameDefine.ASSESSMENT:
                assessmentService.undo(undoLog);
                break;
            case TableNameDefine.APP_PARM:
                appParamService.undo(undoLog);
                break;
            default:
                throw new Exception();
        }
    }

    @Override
    public UndoLog findById(Integer id) {
        Optional<UndoLog> undoLog = repository.findById(id);
        return undoLog.orElse(null);
    }

    @Override
    public List<UnitDTO> formatObj(List<Unit> entity) {
        List<UnitDTO> dtos = new ArrayList<>();
        for (Unit criteria : entity) {
            UnitDTO dto = new UnitDTO();
            dto.setUnitName(criteria.getUnitName());
            dto.setUnitNameEn(criteria.getUnitNameEn());
            dto.setUnitCode(criteria.getUnitCode());
            dto.setDescription(criteria.getDescription());
            dto.setDescriptionEn(criteria.getDescriptionEn());
            dto.setEmail(criteria.getEmail());
            dtos.add(dto);
        }
        return dtos;    }
}
