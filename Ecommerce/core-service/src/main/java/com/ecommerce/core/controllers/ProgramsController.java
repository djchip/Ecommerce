package com.ecommerce.core.controllers;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.entities.*;
import com.ecommerce.core.repositories.DirectoryRepository;
import com.ecommerce.core.service.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ecommerce.core.dto.PagingResponse;
import com.ecommerce.core.dto.ProgramsDTO;
import com.ecommerce.core.dto.ResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("programs")
@Slf4j
public class ProgramsController extends BaseController {
    private final String START_LOG = "programs {}";
    private final String END_LOG = "programs {} finished in: {}";
    private final String TABLE_NAME = "programs";
    public static final Gson g = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    @Autowired
    private ProgramsService programsService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private DirectoryRepository directoryRepository;
    @Autowired
    private ProofService proofService;

    @Autowired
    private CriteriaService criteriaService;
    @Autowired
    UndoLogService undoLogService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    ProgramUserSevice programUserSevice;

    @Autowired
    RolesService rolesService;

    @Autowired
    CriteriaUserService criteriaUserService;

    @Autowired
    DirectoryService directoryService;

    @GetMapping()
    public ResponseModel doSearch(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "lang", required = false) String lang,
                                  @RequestParam(value = "sort", required = false) boolean sortDesc,
                                  @RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int perPage,
                                  HttpServletRequest request) {
        final String action = "doSearch";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            Pageable paging = PageRequest.of(currentPage, perPage);
            Page<ProgramsDTO> pageResult = null;
            String username = getUserFromToken(request);
            List<String> listRoleCodeByUsername = rolesService.getListRolesCodeByUsername(username);
            if (keyword.equals("")) {
                keyword = null;
            } else {
                keyword = keyword.trim();
            }
            if (keyword != null) {
                keyword = keyword.toLowerCase();
            }


            if (lang.trim().equalsIgnoreCase("vn")) {
                if(listRoleCodeByUsername.contains("ADMIN") || listRoleCodeByUsername.contains("Super Admin")){
                    pageResult = programsService.doSearch(keyword,null, paging);
                } else {
                    pageResult = programsService.doSearch(keyword,username, paging);
                }
            } else {
                if(listRoleCodeByUsername.contains("ADMIN") || listRoleCodeByUsername.contains("Super Admin")){
                    pageResult = programsService.doSearchEn(keyword,null, paging);
                } else {
                    pageResult = programsService.doSearchEn(keyword,username, paging);
                }
            }
            if ((pageResult == null || pageResult.isEmpty())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy chương trình.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }
            PagingResponse<ProgramsDTO> result = new PagingResponse<>();
            result.setTotal(pageResult.getTotalElements());
            result.setItems(pageResult.getContent());
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(result);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("find-by-id/{id}")
    public ResponseModel findById(@PathVariable("id") int id) {
        final String action = "doFindById";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            ProgramsDTO result = programsService.findByIdd(id);
            if (result.getUpdatedBy() == null) {
                result.setUpdatedDate(null);
            }
            responseModel.setContent(result);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;

        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("selectbox")
    public ResponseModel getSelectboxProgram(HttpServletRequest request) {
        final String action = "getSelectboxProgram";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            String username = getUserFromToken(request);
            List<String> listRoleCodeByUsername = rolesService.getListRolesCodeByUsername(username);
            if(listRoleCodeByUsername.contains("ADMIN") || listRoleCodeByUsername.contains("Super Admin")){
                responseModel.setContent(programsService.findProgramsByUsername(null));
            } else {
                responseModel.setContent(programsService.findProgramsByUsername(username));
            }

            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("getListYearInDataBase")
    public ResponseModel getListYearInDataBase(HttpServletRequest request) {
        final String action = "getListYearInDataBase";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(programsService.getListYearInDataBase());
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("getProgramQuantityByYear")
    public ResponseModel getProgramQuantityByYear(HttpServletRequest request) {
        final String action = "getProgramQuantityByYear";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(programsService.getProgramQuantityByYear());
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("getYearNow")
    public ResponseModel getYearNow(HttpServletRequest request) {
        final String action = "getYearNow";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(LocalDateTime.now().getYear());
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("get-all-programs")
    public ResponseModel getAllPrograms(@RequestParam(value = "byYear") String byYear, HttpServletRequest request) {
        final String action = "getSelectboxProgram";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(programsService.getAllProgramsByYear(Integer.parseInt(byYear)));
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("get-programs")
    public ResponseModel getPrograms(@RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int perPage) {
        final String action = "doGet";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            Pageable paging = PageRequest.of(currentPage, perPage);
            Page<Programs> pageResult = null;
            pageResult = programsService.findAll(paging);
            ResponseModel responseModel = new ResponseModel();
            if (pageResult == null || pageResult.isEmpty()) {
                responseModel.setErrorMessages("Không có chương trình nào.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            } else {
                responseModel.setContent(pageResult);
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
                return responseModel;
            }
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @Transactional
    @PostMapping()
    public ResponseModel doCreate(@RequestBody Programs entity, HttpServletRequest request) {
        final String action = "doCreate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        String ProgramsName = entity.getName();
        try {
            Programs checkExisted = programsService.findByDirectoryName(ProgramsName);
            if (checkExisted != null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
                responseModel.setErrorMessages("chương trình đã tồn tại.");
                return responseModel;
            } else {
                List<String> listRoleCodeByUsername = rolesService.getListRolesCodeByUsername(getUserFromToken(request));
                java.util.Date date = new java.util.Date();
                entity.setCreatedDate(date);
                entity.setUpdatedDate(date);
                entity.setDelete(ConstantDefine.STATUS.NOT_DELETE);

                entity.setUndoStatus(ConstantDefine.STATUS.CAN_BE_UNDO);
                String createdBy = getUserFromToken(request);
                entity.setCreatedBy(createdBy);
//                entity.setUpdatedBy(name);

                ResponseModel responseModel = new ResponseModel();
                responseModel.setContent(entity);
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
                entity = programsService.save(entity);

                if(!listRoleCodeByUsername.contains("ADMIN") && !listRoleCodeByUsername.contains("Super Admin")){
                    CriteriaUser criteriaUser = new CriteriaUser();
                    criteriaUser.setProgramId(entity.getId());
                    criteriaUser.setCategoryId(entity.getCategoryId());
                    criteriaUser.setOrgId(entity.getOrganizationId());
                    criteriaUser.setUserId(userInfoService.findByUsername(getUserFromToken(request)).getId());
                    criteriaUserService.save(criteriaUser);

                    List<Directory> directories = directoryService.getListStandardByOrgIdAndCategoryId(entity.getOrganizationId(), entity.getCategoryId());
                    List<Criteria> criterias = criteriaService.getListCriteriaByOrgIdAndCategoryId(entity.getOrganizationId(), entity.getCategoryId());
                    for(Directory directory : directories){
                        CriteriaUser criteriaUser1 = new CriteriaUser();
                        criteriaUser1.setProgramId(entity.getId());
                        criteriaUser1.setCategoryId(entity.getCategoryId());
                        criteriaUser1.setOrgId(entity.getOrganizationId());
                        criteriaUser1.setUserId(userInfoService.findByUsername(getUserFromToken(request)).getId());
                        criteriaUser1.setStandardId(directory.getId());
                        criteriaUserService.save(criteriaUser1);

                        for(Criteria criteria : criterias) {
                            if(Objects.equals(criteria.getStandardId(), directory.getId())){
                                CriteriaUser criteriaUser2 = new CriteriaUser();
                                criteriaUser2.setProgramId(entity.getId());
                                criteriaUser2.setCategoryId(entity.getCategoryId());
                                criteriaUser2.setOrgId(entity.getOrganizationId());
                                criteriaUser2.setUserId(userInfoService.findByUsername(getUserFromToken(request)).getId());
                                criteriaUser2.setStandardId(directory.getId());
                                criteriaUser2.setCriteriaId(criteria.getId());
                                criteriaUserService.save(criteriaUser2);
                            }
                        }
                    }
                }

                UndoLog undoLog = UndoLog.undoLogBuilder()
                        .action(request.getMethod())
                        .requestObject(g.toJson(entity, Programs.class))
                        .status(ConstantDefine.STATUS.UNDO_NEW)
                        .url(request.getRequestURL().toString())
                        .description("Thêm mới chương trình "+entity.getName()+" bởi " + createdBy)
                        .createdDate(LocalDateTime.now())
                        .createdBy(createdBy)
                        .tableName(TABLE_NAME)
                        .idRecord(entity.getId())
                        .build();
                undoLogService.create(undoLog);
                return responseModel;
            }
        } catch (Exception e) {
            System.out.println(e);
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @Transactional
    @DeleteMapping("delete/{id}")
    public ResponseModel delete(@PathVariable("id") Integer id) {
        final String action = "doDelete";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            Programs entity = programsService.deletePro(id);
//            List<Criteria> criteriaList = criteriaService.findByProgramIdAndDeleteNot(id, 1);
//            List<Proof> byProId = proofService.findByProId(programsService.findById(id).getId());

            if (entity == null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_CANNOT_DELETE);
                responseModel.setErrorMessages("Không thể xóa chương trình chứa tiêu chuẩn.");
                return responseModel;
            } else {

                String createdBy = getUserFromToken(request);
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
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
                return responseModel;
            }
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @DeleteMapping("delete-multi")
    public ResponseModel deleteMulti(@RequestBody Integer[] ids) {
        final String action = "doDeleteMulti";
        ResponseModel responseModel = new ResponseModel();
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            if(programsService.deletePrograms(ids, g, getUserFromToken(request), request)){
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
                return responseModel;
            }else{
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.BEING_USED);
                return responseModel;
            }

        } catch (Exception e) {
            throw handleException(e);
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @Transactional
    @PutMapping()
    public ResponseModel doUpdate(@RequestBody Programs dto, HttpServletRequest request) {
        final String action = "doUpdate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        Programs entity = programsService.findById(dto.getId());

        try {
            String createdBy = getUserFromToken(request);
            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .revertObject(g.toJson(entity, Programs.class))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Cập nhập chương trình "+entity.getName()+" bởi " + createdBy)
                    .updatedDate(LocalDateTime.now())
                    .createdBy(createdBy)
                    .tableName(TABLE_NAME)
                    .idRecord(entity.getId())
                    .build();

            Programs checkExisted = programsService.findByDirectoryName(dto.getName());
            if (checkExisted != null && !checkExisted.getName().equals(entity.getName())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
                responseModel.setErrorMessages("tên chương trình đã tồn tại.");
                return responseModel;
            } else {
                entity.setNameEn(dto.getNameEn());
                entity.setDescriptionEn(dto.getDescriptionEn());
                entity.setName(dto.getName());
                entity.setUndoStatus(ConstantDefine.STATUS.CAN_BE_UNDO);
                entity.setDescription(dto.getDescription());
                entity.setNote(dto.getNote());
                entity.setOrganizationId(dto.getOrganizationId());
                entity.setCategoryId(dto.getCategoryId());
                System.out.println(dto.getCategoryId()+"+oke");
                java.util.Date date = new java.util.Date();
                entity.setUpdatedDate(date);
                entity.setUpdatedBy(createdBy);
                programsService.save(entity);

                undoLog.setRequestObject(g.toJson(entity));
                undoLogService.create(undoLog);
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
                return responseModel;

            }
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }

    }

    @PostMapping("format-obj")
    public ResponseModel formatObj(@RequestBody Programs entity, HttpServletRequest request) {
        final String action = "formatObj";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ProgramsDTO dto = programsService.formatObj(entity);
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(dto);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("find-by-orgId")
    public ResponseModel findByOrgId(@RequestParam Integer orgId, HttpServletRequest request) {
        final String action = "findByOrgId";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(programsService.findByOrgId(orgId));
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }
}
