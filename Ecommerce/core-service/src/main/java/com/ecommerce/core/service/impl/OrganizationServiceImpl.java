package com.ecommerce.core.service.impl;


import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.entities.*;
import com.ecommerce.core.exceptions.ExitsOrganizationException;
import com.ecommerce.core.exceptions.OrganizationBeingUseException;
import com.ecommerce.core.repositories.*;
import com.ecommerce.core.service.ProgramsService;
import com.ecommerce.core.service.UndoLogService;
import com.google.gson.Gson;
import com.ecommerce.core.dto.CategoriesDTO;
import com.ecommerce.core.dto.OrganizationDTO;
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
public class OrganizationServiceImpl implements OrganizationService {
    private static final Integer DELETED = 1;
    private static final Integer UNDO_DELETE = 0;
    private static final Integer STATUS_DELETE = 0;
    private static final String TABLE_NAME = "organization";
    private static final int FIRST_INDEX = 0;

    private static final Integer UN_ACTIVE_UNDO_LOG = 1;
    @Autowired
    OrganizationRepository repo;
    @Autowired
    UndoLogService undoLogService;
    @Autowired
    CategoriesRepository categoriesService;
    @Autowired
    ProofRepository proofRepository;
    @Autowired
    ProgramsRepository programsRepository;
    @Autowired
    DirectoryRepository directoryRepository;
    @Autowired
    CriteriaRepository criteriaRepository;
    @Autowired
    ProgramsService programsService;

    @Override
    public Organization create(Organization entity) {
        // TODO Auto-generated method stub
        return repo.save(entity);
    }

    @Override
    public Organization retrieve(Integer id) {
        // TODO Auto-generated method stub
        Optional<Organization> entity = repo.findById(id);
        return entity.orElse(null);
    }

    @Override
    public void update(Organization entity, Integer id) {
        // TODO Auto-generated method stub
        repo.save(entity);
    }

    @Override
    public void delete(Integer id) {
        // TODO Auto-generated method stub
        repo.deleteById(id);
    }

    @Override
    public Page<Organization> doSearch(String keyword, Pageable pageable) {
        // TODO Auto-generated method stub
        return repo.doSearch(keyword, pageable);
    }

    @Override
    public List<Organization> getSelectbox() {
        // TODO Auto-generated method stub
        return repo.findByDeletedNot(1);
    }

    @Override
    public List<Organization> findOrgForCriteria() {
        return repo.findOrgForCriteria();
    }

    @Override
    public List<Organization> getListOrg() {
        return repo.getListOrg();
    }

    @Override
    public Optional<Organization> finbyID(Integer id) {
        return repo.finbyID(id);
    }

    @Override
    public List<Organization> findByName(String name) {
        return repo.findByNameExisted(name);
    }

    @Override
    public Organization findOrgByName(String name) {
        return repo.findOrgByName(name);
    }

    @Override
    public Organization deleteOr(Integer id) throws Exception {
        Optional<Organization> optional = repo.findById(id);
        if (optional.isPresent()) {
            List<Programs> programs = programsRepository.findByOrganizationIdAndDeleteNot(id, 1);
            List<Directory> directories = directoryRepository.findByOrganizationIdAndDeleteNot(id, 1);
            List<Criteria> criteria = criteriaRepository.findByOrganizationIdAndDeleteNot(id, 1);
            if (programs.size() > 0 || directories.size() > 0 || criteria.size() > 0) {
                return null;
            }
            Organization userInfo = optional.get();
            userInfo.setDeleted(DELETED);
            userInfo.setUndoStatus(ConstantDefine.STATUS.CAN_BE_UNDO);
            return repo.save(userInfo);
        } else {
            throw new Exception();
        }
    }

    private Organization undoPut(UndoLog undoLog) {
        Gson g = new Gson();
        return g.fromJson(undoLog.getRevertObject(), Organization.class);
    }

    @Override
    public void undo(UndoLog undoLog) throws Exception {
        List<UndoLog> undoLogs = undoLogService.findByTableNameAndIdRecordAndStatusNotOrderByCreatedDateDesc(TABLE_NAME, undoLog.getIdRecord(), 1);
        for (UndoLog log : undoLogs) {
            if (log.getId().equals(undoLog.getId())) {
                Optional<Organization> optional = repo.findById(log.getIdRecord());
                Organization organization;
                if (optional.isPresent()) {
                    organization = optional.get();
                } else {
                    throw new Exception();
                }
                switch (log.getAction()) {
                    case "POST":
                        if(checkOrgBeingUse(organization.getId())){
                            throw new OrganizationBeingUseException("Organization being use");
                        }
                        repo.deleteById(organization.getId());
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                    case "DELETE":
                        if (!repo.findByNameAndDeleted(organization.getName(), 0).isEmpty()) {
                            throw new ExitsOrganizationException("Tên tổ chức đã tồn tại");
                        } else {
                            organization.setDeleted(UNDO_DELETE);
                            log.setStatus(UN_ACTIVE_UNDO_LOG);
                            undoLogService.update(log, log.getId());
                            break;
                        }
                    case "PUT":
                        organization = undoPut(log);
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                }
                if (!undoLogService.existsByTableNameAndIdRecord(TABLE_NAME, organization.getId()) && !log.getAction().equals("POST")) {
                    organization.setUndoStatus(ConstantDefine.STATUS.CAN_NOT_UNDO);
                }
                if (!log.getAction().equals("POST")) {
                    repo.save(organization);
                }
                break;
            } else {
                log.setStatus(UN_ACTIVE_UNDO_LOG);
                undoLogService.update(log, log.getId());
            }
        }
    }

    @Override
    public OrganizationDTO formatObj(Organization entity) {
        OrganizationDTO dto = new OrganizationDTO(
                entity.getId(), entity.getName(), entity.getNameEn(), entity.getDescription(), entity.getDescriptionEn(),
                entity.isEncode(), entity.getCategoryId(), null, null, entity.getCreatedBy(),
                entity.getCreatedDate(), entity.getUpdatedBy(), entity.getUpdatedDate()

        );
        Optional<CategoriesDTO> categoriesDTO = categoriesService.finbyID(entity.getCategoryId());
        if (categoriesDTO.isPresent()) {
            dto.setCategoryName(categoriesDTO.get().getName());
            dto.setCategoryNameEn(categoriesDTO.get().getNameEn());
        }
        return dto;
    }

    @Override
    public Organization findByProgramId(Integer id) {
        return repo.findByProgramId(id);
    }

    @Override
    public Organization findByIdAndDeletedNot(Integer id, Integer deleted) {
        return repo.findByIdAndDeletedNot(id, 0);
    }

    private boolean checkOrgBeingUse(Integer orgId){
        return directoryRepository.existsByOrganizationIdAndDelete(orgId, 0) ||
                programsService.existsByOrganizationIdAndDelete(orgId, 0) ||
                criteriaRepository.existsByOrganizationIdAndDelete(orgId, 0);
    }
    @Override
    public boolean deleteOr(Integer[] ids, Gson g, String deleteBy, HttpServletRequest request) throws Exception {
        for (Integer id : ids) {
            Organization organization = deleteOr(id);
            if (organization == null) {
                return false;
            }
            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .requestObject(g.toJson(organization, Organization.class))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Xóa tổ chức "+organization.getName()+" bởi " + deleteBy)
                    .createdDate(LocalDateTime.now())
                    .createdBy(deleteBy)
                    .tableName(TABLE_NAME)
                    .idRecord(organization.getId())
                    .build();
            undoLogService.create(undoLog);
        }
        return true;
    }

    @Override
    public boolean findOrgbyEncode() {
        return repo.findOrgbyEncode();
    }
}
