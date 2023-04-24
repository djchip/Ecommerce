package com.ecommerce.core.service.impl;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.entities.*;
import com.ecommerce.core.exceptions.ExistsCriteria;
import com.ecommerce.core.repositories.CriteriaRepository;
import com.ecommerce.core.repositories.ProgramsRepository;
import com.ecommerce.core.repositories.ProofRepository;
import com.ecommerce.core.service.CategoriesService;
import com.ecommerce.core.service.OrganizationService;
import com.ecommerce.core.service.ProgramsService;
import com.ecommerce.core.service.UndoLogService;
import com.google.gson.Gson;
import com.ecommerce.core.dto.ProgramsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProgramsServiceImpl implements ProgramsService {
    private static final Integer DELETED =1 ;
    private static final Integer UNDO_DELETE = 0;
    private static final String TABLE_NAME = "programs";
    private static final int FIRST_INDEX = 0;
    private static final Integer UN_ACTIVE_UNDO_LOG = 1;

    @Autowired
    ProgramsRepository programsRepository;
    @Autowired
    UndoLogService undoLogService;
    @Autowired
    OrganizationService organizationService;
    @Autowired
    CategoriesService categoriesService;
    @Autowired
    ProofRepository proofRepository;

    @Autowired
    CriteriaRepository criteriaRepository;

    @Override
    public Page<ProgramsDTO> doSearch (String keyword, String username, Pageable pageable) {
        return programsRepository.doSearch(keyword, username, pageable);
    }

    @Override
    public Page<ProgramsDTO> doSearchEn(String keyword,String username, Pageable pageable) {
        return programsRepository.doSearchEn(keyword, username, pageable);
    }

    @Override
    public Programs findByProgramsName(String programsName) {
        return programsRepository.findByName(programsName);

    }

    @Override
    public Page<Programs> findByName(String name, Pageable pageable) {
        return null;
    }

//    @Override
//    public Page<Programs> findByName(String name, Pageable pageable) {
////        return programsRepository.findProgramsByName(name,pageable);
//
//    }

    @Override
    public Programs create(Programs entity) {
        return programsRepository.save(entity);
    }

    @Override
    public Programs retrieve(Integer id) {
        Optional<Programs> entity = programsRepository.findById(id);
        return entity.orElse(null);
    }

    @Override
    public void update(Programs entity, Integer id) {
       programsRepository.save(entity);
    }

    @Override
    public void delete(Integer id) throws Exception {
        Optional<Programs> optional = programsRepository.findById(id);
        if(optional.isPresent()){
            Programs userInfo = optional.get();
            userInfo.setDelete(DELETED);
            programsRepository.save(userInfo);
        }else {
            throw new Exception();
        }
//        programsRepository.deleteById(id);
    }

    @Override
    public Programs save(Programs programs) {
        return programsRepository.save(programs);
    }

    @Override
    public Page<Programs> findAll(Pageable pageable) {
        return programsRepository.findAll(pageable);
    }

    @Override
    public Programs findById(int id) {
        return programsRepository.findById(id).get();
    }

    @Override
    public ProgramsDTO findByIdd(int id) {
        return programsRepository.findByIdd(id);
    }

    @Override
    public Programs getByIdEn(Integer id) {
        return programsRepository.getByIdEn(id);
    }

    @Override
    public Programs getById(Integer id) {
        return programsRepository.getById(id);
    }

    @Transactional
    @Override
    public Programs deletePro(Integer id) throws Exception {
        Optional<Programs> optional = programsRepository.findById(id);
        if (optional.isPresent()) {
//            if(proofRepository.findByProgramIdAndDeletedNot(id, 1)){
//               throw new BeingUsedException("Has proof using the program");
//            }
//            List<Criteria> criteriaList = criteriaRepository.findByProgramIdAndDeleteNot(id, 1);
            List<Proof> byProId = proofRepository.findByProgramIdAndDeletedNot(id, 1);
            if(byProId.size() > 0){
                return null;
            }
            Programs programs = optional.get();
            programs.setDelete(DELETED);
            return programsRepository.save(programs);
        } else {
            throw new Exception();
        }
    }

    @Override
    public void undo(UndoLog undoLog) throws Exception {
        List<UndoLog> undoLogs = undoLogService.findByTableNameAndIdRecordAndStatusNotOrderByCreatedDateDesc(TABLE_NAME, undoLog.getIdRecord(), 1);
        for (UndoLog log: undoLogs) {
            if (log.getId().equals(undoLog.getId())){
                Optional<Programs> optional = programsRepository.findById(log.getIdRecord());
                Programs programs;
                if (optional.isPresent()) {
                    programs = optional.get();
                } else {
                    throw new Exception();
                }
                switch (log.getAction()) {
                    case "POST":

                        programsRepository.deleteById(programs.getId());
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                    case "DELETE":
                        if(!programsRepository.findByNameAndDelete(programs.getName(),0).isEmpty()){
                            throw new ExistsCriteria("Tên tổ chức đã tồn tại");
                        }else {
                        programs.setDelete(UNDO_DELETE);
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;}
                    case "PUT":
                        programs = undoPut(log);
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                }
                if(!undoLogService.existsByTableNameAndIdRecord(TABLE_NAME, programs.getId()) && !log.getAction().equals("POST")){
                    programs.setUndoStatus(ConstantDefine.STATUS.CAN_NOT_UNDO);
                }
                if(!log.getAction().equals("POST")){
                    programsRepository.save(programs);
                }
                break;
            }else {
                log.setStatus(UN_ACTIVE_UNDO_LOG);
                undoLogService.update(log, log.getId());
            }
        }
    }

    private Programs undoPut(UndoLog undoLog)  {
        Gson g = new Gson();
//        Directory userInfo = g.fromJson(undoLog.getRevertObject(), Directory.class);
//        userInfo.setUndoStatus(ConstantDefine.STATUS.CAN_NOT_UNDO);
//        directoryRepository.save(userInfo);

        return  g.fromJson(undoLog.getRevertObject(), Programs.class);

    }

    @Override
	public List<Programs> getSelectbox() {
		// TODO Auto-generated method stub
		return programsRepository.findByDeleteNot(1);
	}

    @Override
    public List<Programs> findProgramsByUsername(String username) {
        return programsRepository.findProgramsByUsername(username);
    }

    @Override
    public List<Programs> getAllProgramsByYear(int byYear) {
        return programsRepository.getAllProgramsByYear(byYear);
    }

    @Override
    public ProgramsDTO formatObj(Programs entity) {
        ProgramsDTO dto = new ProgramsDTO(entity.getCategoryId(), null,null,
                entity.getDescriptionEn(), entity.getNameEn(),null, entity.getId(),
                entity.getName(), entity.getDescription(), entity.getNote(), entity.getCreatedBy(),
                entity.getCreatedDate(), entity.getUpdatedDate(), null, entity.getOrganizationId(),
                entity.getUndoStatus(), entity.getDelete(), entity.getUpdatedBy()
        );
        Optional<Organization> organization = organizationService.finbyID(entity.getOrganizationId());
        if (organization.isPresent()) {
            dto.setOrganizationName(organization.get().getName());
            dto.setOrganizationNameEn(organization.get().getNameEn());
        }

        Optional<Categories> categories = categoriesService.finbyID(entity.getCategoryId());
        if (categories.isPresent()) {
            dto.setCategoryName(categories.get().getName());
            dto.setCategoryNameEn(categories.get().getNameEn());
        }
        System.out.println(categories.toString()+"oke");
        return dto;
    }

    @Override
    public Optional<ProgramsDTO> finbyID(Integer id) {
        return programsRepository.finbyID(id);
    }

    @Override
    public Programs findByDirectoryName(String name) {
        Optional<Programs> entity = programsRepository.findByNameAndDeleteNot(name, ConstantDefine.IS_DELETED.TRUE);
        return entity.orElse(null);
    }

    @Transactional
    @Override
    public boolean deletePrograms(Integer[] ids, Gson g, String createdBy, HttpServletRequest request) throws Exception {
        for(Integer id:ids){
            Programs entity = deletePro(id);
            if(entity == null){
                return false;
            }
            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .requestObject(g.toJson(entity, Programs.class))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Xóa chương trình "+entity.getName()+" bởi " + createdBy)
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
    public Programs findProgramByProofId(Integer id) {
        return programsRepository.findProgramByProofId(id);
    }

    @Override
    public List<Programs> findByOrgId(Integer orgId) {
        return programsRepository.findByOrganizationIdAndDeleteNot(orgId, 1);
    }

    @Override
    public boolean existsByOrganizationIdAndDelete(Integer orgId, Integer delete) {
        return programsRepository.existsByOrganizationIdAndDelete(orgId, delete);
    }

    @Override
    public List<Integer> getListYearInDataBase() {
        return programsRepository.getListYearInDataBase();
    }

    @Override
    public List<Integer> getProgramQuantityByYear() {
        List<Integer> listProgramQuantityByYear = new ArrayList<>();
        List<Integer> listYear = programsRepository.getListYearInDataBase();
        for (Integer year : listYear) {
            listProgramQuantityByYear.add(programsRepository.getAllProgramsByYear(year).size());
        }
        return listProgramQuantityByYear;
    }

    @Override
    public List<Programs> findByOrgIdAndCategoryId(Integer orgId, Integer categoryId) {
        return programsRepository.findByOrgIdAndCategoryId(orgId, categoryId);
    }

}
