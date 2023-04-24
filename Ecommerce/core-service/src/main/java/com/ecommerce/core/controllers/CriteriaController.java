package com.ecommerce.core.controllers;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.entities.Criteria;
import com.ecommerce.core.entities.CriteriaUser;
import com.ecommerce.core.entities.StandardUser;
import com.ecommerce.core.entities.UndoLog;
import com.ecommerce.core.exceptions.CheckDecentralizeException;
import com.ecommerce.core.exceptions.DetectExcelException;
import com.ecommerce.core.exceptions.ExistsCriteria;
import com.ecommerce.core.service.*;
import com.ecommerce.core.service.impl.Export_Import.CriteriaExcelExport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ecommerce.core.dto.CriteriaDTO;
import com.ecommerce.core.dto.PagingResponse;
import com.ecommerce.core.dto.ResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j

@RestController
@RequestMapping("/criteria")
public class CriteriaController extends BaseController {
    private final String START_LOG = "criteria {}";
    private final String END_LOG = "criteria {} finished in: {}";
    private final String TABLE_NAME = "criteria";
    public static final Gson g = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    @Autowired
    CriteriaService criteriaService;
    @Autowired
    DirectoryService directoryService;
    @Autowired
    ProofService proofService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    UndoLogService undoLogService;
    @Autowired
    CriteriaUserService criteriaUserService;
    @Autowired
    UserInfoService userInfoService;

    @Autowired
    StandardUserService standardUserService;

    @Autowired
    RolesService rolesService;

    @GetMapping("")
    public ResponseModel doSearch(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "lang", required = false) String lang,
                                  @RequestParam(value = "orgId", required = false) Integer orgId,
                                  @RequestParam(value = "programId", required = false) Integer programId,
                                  @RequestParam(value = "standardId", required = false) Integer standardId,
                                  @RequestParam(value = "sort", required = false) boolean sortDesc,
                                  @RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int perPage,
                                  @RequestParam(value = "isExcel", required = false) boolean isExcel,
                                  HttpServletRequest request) {
        final String action = "doSearch";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            Pageable paging;
            if (isExcel) {
                paging = PageRequest.of(0, Integer.MAX_VALUE);
            } else {
                paging = PageRequest.of(currentPage, perPage);
            }
            Page<CriteriaDTO> pageResult = null;
            if (keyword.equals("")) {
                keyword = null;
            } else {
                keyword = keyword.trim();
            }
            if (orgId != null && programId != null) {
                programId = null;
            }
            String username = getUserFromToken(request);
            List<String> listRoleCodeByUsername = rolesService.getListRolesCodeByUsername(username);
            if (lang.trim().equalsIgnoreCase("vn")) {
                if (listRoleCodeByUsername.contains("ADMIN") || listRoleCodeByUsername.contains("Super Admin")) {
                    pageResult = criteriaService.doSearch(keyword, programId, standardId, orgId, null, paging);
                } else {
                    pageResult = criteriaService.doSearch(keyword, programId, standardId, orgId, username, paging);
                }
            } else {
                if (listRoleCodeByUsername.contains("ADMIN") || listRoleCodeByUsername.contains("Super Admin")) {
                    pageResult = criteriaService.doSearchEn(keyword, programId, standardId, orgId, null, paging);
                } else {
                    pageResult = criteriaService.doSearchEn(keyword, programId, standardId, orgId, username, paging);
                }
            }
            if ((pageResult == null || pageResult.isEmpty())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy cây thư mục.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }

            PagingResponse<CriteriaDTO> result = new PagingResponse<>();
            result.setTotal(pageResult.getTotalElements());
            result.setItems(pageResult.getContent());

            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(result);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("get-list-by-id")
    public ResponseModel doSearchExcel(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "lang", required = false) String lang,
                                  @RequestParam(value = "orgId", required = false) Integer orgId,
                                  @RequestParam(value = "programId", required = false) Integer programId,
                                  @RequestParam(value = "standardId", required = false) Integer standardId,
                                  @RequestParam(value = "sort", required = false) boolean sortDesc,
                                  @RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int perPage,
                                  @RequestParam(value = "isExcel", required = false) boolean isExcel,
                                  @RequestParam(value = "listId") String listId,
                                  HttpServletRequest request) {
        final String action = "doSearch";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            Pageable paging;
            if (isExcel) {
                paging = PageRequest.of(0, Integer.MAX_VALUE);
            } else {
                paging = PageRequest.of(currentPage, perPage);
            }
            Page<CriteriaDTO> pageResult = null;
            if (keyword.equals("")) {
                keyword = null;
            } else {
                keyword = keyword.trim();
            }
            if (orgId != null && programId != null) {
                programId = null;
            }
            String username = getUserFromToken(request);
            List<String> listRoleCodeByUsername = rolesService.getListRolesCodeByUsername(username);
            if (lang.trim().equalsIgnoreCase("vn")) {
                if (listRoleCodeByUsername.contains("ADMIN") || listRoleCodeByUsername.contains("Super Admin")) {
                    pageResult = criteriaService.doSearchExcel(keyword, programId, standardId, orgId, null, paging, listId);
                } else {
                    pageResult = criteriaService.doSearchExcel(keyword, programId, standardId, orgId, username, paging, listId);
                }
            } else {
                if (listRoleCodeByUsername.contains("ADMIN") || listRoleCodeByUsername.contains("Super Admin")) {
                    pageResult = criteriaService.doSearchExcelEn(keyword, programId, standardId, orgId, null, paging, listId);
                } else {
                    pageResult = criteriaService.doSearchExcelEn(keyword, programId, standardId, orgId, username, paging, listId);
                }
            }
            if ((pageResult == null || pageResult.isEmpty())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy cây thư mục.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }

            PagingResponse<CriteriaDTO> result = new PagingResponse<>();
            result.setTotal(pageResult.getTotalElements());
            result.setItems(pageResult.getContent());

            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(result);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("list-all")
    public ResponseModel getDocument() {
        final String action = "doRetrieve";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(criteriaService.getCriteria());
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("{id}")
    public ResponseModel doRetrieve(@RequestParam(value = "lang", required = false) String lang, @PathVariable Integer id) {
        final String action = "doRetrieve";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            CriteriaDTO result = null;
            if (lang.trim().equalsIgnoreCase("vn")) {
                result = criteriaService.getById(id);
            } else {
                result = criteriaService.getByIdEn(id);
            }
            if (result.getUpdate_by() == null) {
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

    @GetMapping("get/{id}")
    public ResponseModel doDetail(@PathVariable Integer id) {
        final String action = "getDetail";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            Criteria result = criteriaService.findById(id);
            if (result.getUpdate_by() == null) {
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

    @GetMapping("getListCriteriaIdByUsername")
    public ResponseModel getListCriteriaIdByUsername(HttpServletRequest request) {
        final String action = "getListCriteriaIdByUsername";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            List<Integer> listCriId = criteriaService.findAllCriIdByUsername(getUserFromToken(request));
            List<Integer> listCriteriaFromCriteriaUser = criteriaUserService.getListCriteriaIdByUsername(getUserFromToken(request));
            for (int i = 0; i < listCriteriaFromCriteriaUser.size(); i++) {
                if (!listCriId.contains(listCriteriaFromCriteriaUser.get(i))) {
                    listCriId.add(listCriteriaFromCriteriaUser.get(i));
                }
            }
            responseModel.setContent(listCriId);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PostMapping("add")
    public ResponseModel doCreate(@RequestBody Criteria entity, HttpServletRequest request) {
        final String action = "doCreate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
//        String directoryName = entity.getName().trim();
        try {
//            Criteria checkExisted = criteriaService.findByDirectoryName(directoryName);
//            checkExisted != null && checkExisted.getDelete() != ConstantDefine.IS_DELETED.TRUE
            if (criteriaService.checkCodeExists(entity.getCode(), entity.getOrganizationId(), entity.getStandardId())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
                responseModel.setErrorMessages("tên cây thư mục đã tồn tại.");
                return responseModel;
            } else {
                java.util.Date date = new java.util.Date();
                entity.setCreatedDate(date);
                entity.setUpdatedDate(date);
                String createdBy = getUserFromToken(request);
                entity.setCreate_by(createdBy);
                entity.setDelete(ConstantDefine.STATUS.NOT_DELETE);
                entity.setUndoStatus(ConstantDefine.STATUS.UNDO_CREATE);
                Integer code = 0;
                if (criteriaService.getMaxOrder(entity.getStandardId()) != null) {
                    code = criteriaService.getMaxOrder(entity.getStandardId());
                }
                entity.setOrderCri(code + 1);
                ResponseModel responseModel = new ResponseModel();
                responseModel.setContent(entity);
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
                Criteria criteria = criteriaService.save(entity);

                CriteriaUser criteriaUser = new CriteriaUser();
                int criId = criteria.getId();
//                if (criteriaUserService.checkExisted(criId, userInfoService.findByUsername(createdBy).getId())) {
//                    criteriaUser.setCriteriaId(criId);
//                    criteriaUser.setUserId(userInfoService.findByUsername(createdBy).getId());
//                    criteriaUserService.save(criteriaUser);
//                }

                List<String> listRoleCodeByUsername = rolesService.getListRolesCodeByUsername(getUserFromToken(request));
                if (!listRoleCodeByUsername.contains("ADMIN") && !listRoleCodeByUsername.contains("Super Admin")) {
                    List<CriteriaUser> listUserIdPrivilegesByStandardId = criteriaUserService.getListUserIdPrivileges(criteria.getOrganizationId(), criteria.getCategoryId(), criteria.getStandardId());
                    for (CriteriaUser i : listUserIdPrivilegesByStandardId) {
                        CriteriaUser criteriaUser1 = new CriteriaUser();
                        criteriaUser1.setCriteriaId(criteria.getId());
                        criteriaUser1.setStandardId(criteria.getStandardId());
                        criteriaUser1.setOrgId(criteria.getOrganizationId());
                        criteriaUser1.setCategoryId(criteria.getCategoryId());
                        criteriaUser1.setUserId(i.getUserId());
                        criteriaUser1.setProgramId(i.getProgramId());
                        criteriaUserService.save(criteriaUser1);
                    }
                }


//                List<Integer> listUserId = criteriaUserService.getListUserIdByCriteriaId(criteria.getId());
//                for(Integer userId : listUserId){
//                    if(standardUserService.checkExisted(criteria.getStandardId(), userId)){
//                        StandardUser standardUser = new StandardUser();
//                        standardUser.setStandardId(criteria.getStandardId());
//                        standardUser.setUserId(userId);
//                        standardUserService.save(standardUser);
//                    }
//                }
                UndoLog undoLog = UndoLog.undoLogBuilder()
                        .action(request.getMethod())
                        .requestObject(g.toJson(entity))
                        .status(ConstantDefine.STATUS.UNDO_NEW)
                        .url(request.getRequestURL().toString())
                        .description("Thêm mới tiêu chí " + entity.getName() + " bởi " + createdBy)
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

    @DeleteMapping("{id}")
    public ResponseModel doDelete(@PathVariable Integer id, HttpServletRequest request) {
        final String action = "doDelete";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
//            boolean check = true;
//            List<Proof> proofList = proofService.findByCriteriaId(id);
//            if (proofList.size() > 0) {
//                check = false;
//            }
//            if (!check) {
//                ResponseModel responseModel = new ResponseModel();
//                responseModel.setStatusCode(HttpStatus.SC_OK);
//                responseModel.setCode(ResponseFontendDefine.CODE_CANNOT_DELETE);
//                return responseModel;
//            } else {
////                criteriaService.delete(id);
            String createdBy = getUserFromToken(request);
            Criteria entity = criteriaService.deleteDir(id);
            if (entity == null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.BEING_USED);
                return responseModel;
            }
            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .requestObject(g.toJson(entity))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Xóa tiêu chí " + entity.getName() + " bởi " + createdBy)
                    .createdDate(LocalDateTime.now())
                    .createdBy(createdBy)
                    .tableName(TABLE_NAME)
                    .idRecord(entity.getId())
                    .build();
            undoLogService.create(undoLog);

            ResponseModel responseModel = new ResponseModel();
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
//            }
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("selectbox")
    public ResponseModel getSelectboxStandard() {
        final String action = "getSelectboxStandard";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(criteriaService.getCriteria());
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }


    @PutMapping("/edit")
    public ResponseModel doUpdate(@RequestBody Criteria dto, HttpServletRequest request) {
        final String action = "doUpdate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        Criteria entity = criteriaService.findById(dto.getId());
        try {
            String createdBy = getUserFromToken(request);
            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .revertObject(g.toJson(entity, Criteria.class))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Cập nhập tiêu chí " + entity.getName() + " bởi " + createdBy)
                    .updatedDate(LocalDateTime.now())
                    .createdBy(createdBy)
                    .tableName(TABLE_NAME)
                    .idRecord(entity.getId())
                    .build();
//            String criteriaName = dto.getName().trim();
//            Criteria checkExisted = criteriaService.findByDirectoryName(criteriaName);
            if (criteriaService.checkCodeExists(dto.getCode(), dto.getOrganizationId(), dto.getStandardId()) && !dto.getName().equals(entity.getName())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
                responseModel.setErrorMessages("tên chương trình đã tồn tại.");
                return responseModel;
            } else {
                entity.setName(dto.getName());
                entity.setNameEn(dto.getNameEn());
                entity.setCode(dto.getCode());
                entity.setStandardId(dto.getStandardId());
                entity.setProgramId(dto.getProgramId());
                entity.setOrganizationId(dto.getOrganizationId());
                entity.setCategoryId(dto.getCategoryId());
                entity.setDescription(dto.getDescription());
                entity.setDescriptionEn(dto.getDescriptionEn());
                entity.setUserInfos(dto.getUserInfos());
                java.util.Date date = new java.util.Date();
                entity.setUpdatedDate(date);
                entity.setUndoStatus(ConstantDefine.STATUS.NOT_Undo);
                entity.setUpdate_by(createdBy);
            }
            Criteria criteria = criteriaService.save(entity);
            List<Integer> listUserId = criteriaUserService.getListUserIdByCriteriaId(criteria.getId());
            for (Integer userId : listUserId) {
                if (standardUserService.checkExisted(criteria.getStandardId(), userId)) {
                    StandardUser standardUser = new StandardUser();
                    standardUser.setStandardId(criteria.getStandardId());
                    standardUser.setUserId(userId);
                    standardUserService.save(standardUser);
                }
            }
            undoLog.setRequestObject(g.toJson(entity, Criteria.class));
            undoLogService.create(undoLog);

            ResponseModel responseModel = new ResponseModel();
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }

    }

    @GetMapping({"auto-code/{id}"})
    public ResponseModel getAutoCode(@PathVariable Integer id) {
        final String action = "doAutoCode";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            Integer code = criteriaService.getMaxOrder(id);
            if (code == null) {
                code = 0;
            }
            int autoCode = code + 1;
            String setCode = "0";
            if (autoCode < 10) {
                setCode += autoCode;
                responseModel.setContent(setCode);
            } else {
                setCode = String.valueOf(autoCode);
            }
            String setCodeDir = "0";
            String codeDir = directoryService.findById(id).getCode();
            String[] codeDirArr = codeDir.split("\\.");
            setCodeDir = String.valueOf(codeDirArr[codeDirArr.length - 1]);
            String result = setCodeDir + "." + setCode;
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

    @GetMapping("select-by-sta")
    public ResponseModel getListStandardByProgramid(@RequestParam(value = "standardId", required = false) Integer standardId) {
        final String action = "getSelectboxcrite";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            List<String> listRoleCodeByUsername = rolesService.getListRolesCodeByUsername(getUserFromToken(request));
            List<Criteria> criteria = new ArrayList<>();
            if(listRoleCodeByUsername.contains("ADMIN") || listRoleCodeByUsername.contains("Super Admin")){
                criteria=criteriaService.getCriteriaByDirectoryId(standardId, null);
            } else {
                criteria= criteriaService.getCriteriaByDirectoryId(standardId, getUserFromToken(request));
            }


            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(criteria);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }


    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/exportExcel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<Criteria> criteriaList = criteriaService.getCriteria();
        CriteriaExcelExport excelExporter = new CriteriaExcelExport();
        excelExporter.export(criteriaList, response);
    }

    @PostMapping("import-excel")
    public ResponseModel importProof(
            @RequestParam("file") MultipartFile file,
            @RequestParam("organizationId") Integer organizationId,
            @RequestParam("categoryId") Integer categoryId,
            HttpServletRequest request) throws
            Exception {
        StopWatch sw = new StopWatch();
        sw.start();
        log.info("importProof");
        ResponseModel responseModel = new ResponseModel();
        try {
            criteriaService.importCriteria(file, getUserFromToken(request), organizationId, categoryId, request);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
        } catch (ExistsCriteria existsCriteria) {
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
        } catch (CheckDecentralizeException checkDecentralizeException) {
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_Decentralize);
        } catch (DetectExcelException excelException) {
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.EXCEL_WRONG_FORMAT);
        } catch (IllegalStateException illegalStateException) {
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.NOT_FOUND_ORG);
        } catch (Exception e) {
            throw e;
        } finally {
            sw.stop();
            log.info("importProof finish in: " + sw.getTotalTimeSeconds());
        }
        return responseModel;
    }

    @PostMapping("format-obj")
    public ResponseModel formatObj(@RequestBody Criteria entity, HttpServletRequest request) {
        final String action = "formatObj";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            CriteriaDTO dto = criteriaService.formatObj(entity);
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(dto);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            System.out.println(e);
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @DeleteMapping("delete-multi")
    public ResponseModel deleteMulti(@RequestBody Integer[] criteria) {
        final String action = "doDeleteMulti";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            // resultDelete = 1 -> success, resultDelete = 0 -> BEING_USED , BEING_USED = 2 -> No records have been deleted
            int resultDelete = criteriaService.deleteCriteria(criteria, g, getUserFromToken(request), request);
            if (resultDelete == 1) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
                return responseModel;
            } else if (resultDelete == 0) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.BEING_USED);
                return responseModel;
            } else {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.NO_RECORD_DELETED);
                return responseModel;
            }
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("find-all")
    public ResponseModel findAll() {
        final String action = "findAll";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(criteriaService.findAllByDelete());
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PostMapping("format-objs")
    public ResponseModel formatObjs(@RequestBody List<Criteria> entity, HttpServletRequest request) {
        final String action = "formatObjs";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            List<CriteriaDTO> dto = criteriaService.formatObj(entity);
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(dto);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            System.out.println(e);
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }
}
