package com.ecommerce.core.controllers;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.entities.CriteriaUser;
import com.ecommerce.core.entities.Directory;
import com.ecommerce.core.entities.Programs;
import com.ecommerce.core.entities.UndoLog;
import com.ecommerce.core.exceptions.DetectExcelException;
import com.ecommerce.core.exceptions.ExistsDirectory;
import com.ecommerce.core.exceptions.NotSameCodeException;
import com.ecommerce.core.service.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ecommerce.core.dto.DetailStandardDTO;
import com.ecommerce.core.dto.DirectoryDTO;
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
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/directory")
public class DirectoryController extends BaseController {
    private final String START_LOG = "directory {}";
    private final String END_LOG = "directory {} finished in: {}";
    private final String TABLE_NAME = "directory";
    public static final Gson g = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    @Autowired
    DirectoryService directoryService;
    @Autowired
    StandardUserService standardUserService;
    @Autowired
    ProofService proofService;
    @Autowired
    CriteriaService criteriaService;
    @Autowired
    UndoLogService undoLogService;
    @Autowired
    UserInfoService userInfoService;

    @Autowired
    RolesService rolesService;

    @Autowired
    ProgramsService programsService;

    @Autowired
    CriteriaUserService criteriaUserService;

    @GetMapping("")
    public ResponseModel doSearch(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "lang", required = false) String lang,
                                  @RequestParam(value = "orgId", required = false) Integer orgId,
                                  @RequestParam(value = "programId", required = false) Integer programId,
                                  @RequestParam(value = "sort", required = false) boolean sortDesc,
                                  @RequestParam(defaultValue = "0") int currentPage,
                                  @RequestParam(defaultValue = "10") int perPage,
                                  @RequestParam(value = "isExcel", required = false) boolean isExcel,
                                  HttpServletRequest request) {
        final String action = "doSearch";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            Pageable paging;
            if (isExcel){
                paging = PageRequest.of(0, Integer.MAX_VALUE);
            } else {
                paging = PageRequest.of(currentPage, perPage);
            }
            Page<Directory> pageResult = null;
            if (keyword.trim().equals("")) {
                keyword = null;
            } else {
                keyword = keyword.trim();
            }
            if(orgId != null && programId != null){
                programId = null;
            }
            String username = getUserFromToken(request);
            List<String> listRoleCodeByUsername = rolesService.getListRolesCodeByUsername(username);
            if(listRoleCodeByUsername.contains("ADMIN") || listRoleCodeByUsername.contains("Super Admin")){
                pageResult = directoryService.doSearch(keyword, orgId, programId,null, paging);
            } else {
                pageResult = directoryService.doSearch(keyword, orgId, programId,username, paging);
            }
            if ((pageResult == null || pageResult.isEmpty())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy cây thư mục.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }
            PagingResponse<Directory> result = new PagingResponse<>();
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

    @GetMapping("search-by-list-id")
    public ResponseModel doSearchByListIds(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "lang", required = false) String lang,
                                  @RequestParam(value = "orgId", required = false) Integer orgId,
                                  @RequestParam(value = "programId", required = false) Integer programId,
                                  @RequestParam(value = "sort", required = false) boolean sortDesc,
                                  @RequestParam(defaultValue = "0") int currentPage,
                                  @RequestParam(defaultValue = "10") int perPage,
                                  @RequestParam(value = "isExcel", required = false) boolean isExcel,
                                  @RequestParam(value = "listId") String listId,
                                  HttpServletRequest request) {
        final String action = "doSearch";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            Pageable paging;
            if (isExcel){
                paging = PageRequest.of(0, Integer.MAX_VALUE);
            } else {
                paging = PageRequest.of(currentPage, perPage);
            }
            Page<DirectoryDTO> pageResult = null;
            if (keyword.trim().equals("")) {
                keyword = null;
            } else {
                keyword = keyword.trim();
            }
            if(orgId != null && programId != null){
                programId = null;
            }
            String username = getUserFromToken(request);
            List<String> listRoleCodeByUsername = rolesService.getListRolesCodeByUsername(username);
            if(listRoleCodeByUsername.contains("ADMIN") || listRoleCodeByUsername.contains("Super Admin")){
                pageResult = directoryService.doSearchExcel(keyword, orgId, programId,null, paging, listId);
            } else {
                pageResult = directoryService.doSearchExcel(keyword, orgId, programId,username, paging, listId);
            }
            if ((pageResult == null || pageResult.isEmpty())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy cây thư mục.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }
            PagingResponse<DirectoryDTO> result = new PagingResponse<>();
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

    @GetMapping("selectbox")
    public ResponseModel getSelectboxStandard() {
        final String action = "getSelectboxStandard";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(directoryService.getDirectory());
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("select-by-pro")
    public ResponseModel getListStandardByPro(@RequestParam(value = "programId", required = false) Integer proId) {
        final String action = "getSelectboxStandard";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(directoryService.getDirectoryByPro(proId));
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("select-by-programid")
    public ResponseModel getListStandardByProgramid(@RequestParam(value = "programId", required = false) Integer proId, HttpServletRequest request) {
        final String action = "getSelectboxStandard";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            List<String> listRoleCodeByUsername = rolesService.getListRolesCodeByUsername(getUserFromToken(request));
            List<Directory> directories = new ArrayList<>();
            if(listRoleCodeByUsername.contains("ADMIN") || listRoleCodeByUsername.contains("Super Admin")){
                directories=directoryService.getDirectoryByProgramId(proId, null);
            } else {
                directories= directoryService.getDirectoryByProgramId(proId, getUserFromToken(request));
            }

            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(directories);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("select-by-org")
    public ResponseModel getListStandardByOrg(@RequestParam(value = "orgId", required = false) Integer orgId, HttpServletRequest request) {
        final String action = "getSelectboxStandard";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            String username = getUserFromToken(request);
            List<String> listRoleCodeByUsername = rolesService.getListRolesCodeByUsername(username);
            ResponseModel responseModel = new ResponseModel();
            if(listRoleCodeByUsername.contains("ADMIN") || listRoleCodeByUsername.contains("Super Admin")){
                responseModel.setContent(directoryService.findByOrganizationIdAndUsername (orgId, null));
            } else {
                responseModel.setContent(directoryService.findByOrganizationIdAndUsername(orgId, username));
            }

            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("select-by-orgId")
    public ResponseModel getListStandardByOrganizationId(@RequestParam(value = "orgId", required = false) Integer orgId, @RequestParam(value = "categoryId", required = false) Integer categoryId, HttpServletRequest request) {
        final String action = "getSelectboxStandard";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            String username = getUserFromToken(request);
            List<String> listRoleCodeByUsername = rolesService.getListRolesCodeByUsername(username);
            if(listRoleCodeByUsername.contains("ADMIN") || listRoleCodeByUsername.contains("Super Admin")){
                responseModel.setContent(directoryService.findByOrgId(orgId,categoryId, null));
            } else {
                responseModel.setContent(directoryService.findByOrgId(orgId,categoryId, username));
            }

            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("getListStandardIdByUsername")
    public ResponseModel getListStandardIdByUsername(HttpServletRequest request) {
        final String action = "getListStandardIdByUsername";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            List<Integer> listStaId = directoryService.findAllStaIdByUsername(getUserFromToken(request));
            for(int i = 0; i < standardUserService.getListStandardIdByUsername(getUserFromToken(request)).size(); i++){
                if(!listStaId.contains(standardUserService.getListStandardIdByUsername(getUserFromToken(request)).get(i))){
                    listStaId.add(standardUserService.getListStandardIdByUsername(getUserFromToken(request)).get(i));
                }
            }
            responseModel.setContent(listStaId);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

//    @GetMapping("get-listSta-by-programId")
//    public ResponseModel getListStandardByOrgId(@RequestParam(value = "orgId", required = false) Integer orgId) {
//        final String action = "getSelectboxStandard";
//        StopWatch sw = new StopWatch();
//        log.info(START_LOG, action);
//        try {
//            ResponseModel responseModel = new ResponseModel();
//            responseModel.setContent(directoryService.findByOrgId(orgId));
//            responseModel.setStatusCode(HttpStatus.SC_OK);
//            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
//            return responseModel;
//        } catch (Exception e) {
//            throw handleException(e);
//        } finally {
//            log.info(END_LOG, action, sw.getTotalTimeSeconds());
//        }
//    }

    @GetMapping("get-listSta-by-programId")
    public ResponseModel getListStandardByOrgId(@RequestParam(value = "programId", required = false) Integer programId) {
        final String action = "getSelectboxStandard";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(directoryService.findByOrgId(programId));
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
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
            responseModel.setContent(directoryService.getDirectory());
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("find-all")
    public ResponseModel getDirectory() {
        final String action = "getDirectory";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(directoryService.findAll());
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("list-all-dto")
    public ResponseModel getAllDTO() {
        final String action = "doRetrieve";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(directoryService.findAllDTO());
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
    public ResponseModel findById(@PathVariable("id") int id) {
        final String action = "doFindById";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            DirectoryDTO result = null;
            result = directoryService.finbyID(id).get();
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
    public ResponseModel getUpdate(@PathVariable("id") int id) {
        System.out.println("Okeeeeeeeeeeeeeeeeeee");
        final String action = "getUpdate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            DetailStandardDTO result = new DetailStandardDTO();
            result.setEntity(directoryService.finbyID(id).get());
            result.setUserInfos(directoryService.retrieve(id).getUserInfos());
            if (result.getEntity().getUpdate_by() == null) {
                result.getEntity().setUpdatedDate(null);
            }
            System.out.println("h1");
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


    @PostMapping("add")
    public ResponseModel doCreate(@RequestBody Directory entity, HttpServletRequest request) {
        System.out.println("USER" + entity.getUserInfos());
        final String action = "doCreate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            if (directoryService.checkCodeExists(entity.getCode(), entity.getOrganizationId())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
                responseModel.setErrorMessages("Tên tiêu chuẩn đã tồn tại.");
                return responseModel;
            } else {
                java.util.Date date = new java.util.Date();
                entity.setCreatedDate(date);
                entity.setUpdatedDate(date);
                entity.setDelete(ConstantDefine.STATUS.NOT_DELETE);
                entity.setUndoStatus(ConstantDefine.STATUS.CAN_BE_UNDO);

                String createdBy = getUserFromToken(request);
                Integer order = directoryService.getMaxOrder() + 1;
                entity.setCreate_by(createdBy);
                entity.setOrderDir(order);
                ResponseModel responseModel = new ResponseModel();
                responseModel.setContent(entity);
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
                Directory directory = directoryService.save(entity);

                List<String> listRoleCodeByUsername = rolesService.getListRolesCodeByUsername(getUserFromToken(request));

                List<Programs> programs = programsService.findByOrgIdAndCategoryId(directory.getOrganizationId(), directory.getCategoryId());
                if(!listRoleCodeByUsername.contains("ADMIN") && !listRoleCodeByUsername.contains("Super Admin")) {
                    for(Programs program : programs){
                        CriteriaUser criteriaUser = new CriteriaUser();
                        criteriaUser.setUserId(userInfoService.findByUsername(createdBy).getId());
                        criteriaUser.setOrgId(directory.getOrganizationId());
                        criteriaUser.setStandardId(directory.getId());
                        criteriaUser.setCategoryId(directory.getCategoryId());
                        criteriaUser.setProgramId(program.getId());
                        criteriaUserService.save(criteriaUser);
                    }
                }

                UndoLog undoLog = UndoLog.undoLogBuilder()
                        .action(request.getMethod())
                        .requestObject(g.toJson(entity))
                        .status(ConstantDefine.STATUS.UNDO_NEW)
                        .url(request.getRequestURL().toString())
                        .description("Thêm mới tiêu chuẩn "+entity.getName()+" bởi " + createdBy)
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
//            List<Proof> lisProof = proofService.findByStandardId(id);
//            List<Criteria> criteriaList = criteriaService.findByStandardId(id, 1);
//            if (lisProof.size() > 0 || criteriaList.size() > 0){
//                check = false;
//            }
            Directory entity = directoryService.deleteDir(id);

            if (entity == null){
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_CANNOT_DELETE);
                return responseModel;
            } else {

                String createdBy = getUserFromToken(request);
                UndoLog undoLog = UndoLog.undoLogBuilder()
                        .action(request.getMethod())
                        .requestObject(g.toJson(entity, Directory.class))
                        .status(ConstantDefine.STATUS.UNDO_NEW)
                        .url(request.getRequestURL().toString())
                        .description("Xóa "+entity.getName()+" bởi " + createdBy)
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
            }
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PutMapping("/edit")
    public ResponseModel doUpdate(@RequestBody Directory dto, HttpServletRequest request) {
        final String action = "doUpdate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        Directory entity = directoryService.findById(dto.getId());
        try {
            String createdBy = getUserFromToken(request);
            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .revertObject(g.toJson(entity, Directory.class))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Cập nhập tiêu chuẩn "+entity.getName()+" bởi " + createdBy)
                    .updatedDate(LocalDateTime.now())
                    .createdBy(createdBy)
                    .tableName(TABLE_NAME)
                    .idRecord(entity.getId())
                    .build();
            if (directoryService.checkCodeExists(dto.getCode(), dto.getOrganizationId()) && !dto.getName().equalsIgnoreCase(entity.getName())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
                responseModel.setErrorMessages("tên chương trình đã tồn tại.");
                return responseModel;
            } else {
                entity.setName(dto.getName());
                entity.setNameEn(dto.getNameEn());
                entity.setDescription(dto.getDescription());
                entity.setDescriptionEn(dto.getDescriptionEn());
                entity.setOrganizationId(dto.getOrganizationId());
                entity.setCategoryId(dto.getCategoryId());
                entity.setUserInfos(dto.getUserInfos());
                entity.setUpdatedDate(new java.util.Date());
                entity.setCode(dto.getCode());
                entity.setPrograms(dto.getPrograms());
                entity.setUndoStatus(ConstantDefine.STATUS.NOT_Undo);
                entity.setUpdate_by(getUserFromToken(request));
            }
            directoryService.save(entity);
            undoLog.setRequestObject(g.toJson(entity));
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

    @GetMapping({"auto-code"})
    public ResponseModel getAutoCode() {
        final String action = "doAutoCode";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            Integer code = 0;
            if (directoryService.getMaxOrder() != null) {
                code = directoryService.getMaxOrder();
            }
            int result = code + 1;
            if (result < 10) {
                String setCode = "0" + result;
                responseModel.setContent(setCode);
            } else {
                responseModel.setContent(result);
            }
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;

        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PostMapping("import-excel")
    public ResponseModel importDirectory(
            @RequestParam("file") MultipartFile file,
            @RequestParam("organizationId") Integer organizationId,
            @RequestParam("categoryId") Integer categoryId,
            HttpServletRequest request) throws
            IOException, NotSameCodeException {
        StopWatch sw = new StopWatch();
        sw.start();
        log.info("importProof");
        ResponseModel responseModel = new ResponseModel();
        try {
            directoryService.importDirectory(file, getUserFromToken(request), organizationId,categoryId, request);
//            responseModel.setContent(content);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
        } catch (DetectExcelException excelException) {
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.EXCEL_WRONG_FORMAT);
        } catch (ExistsDirectory existsDirectory) {
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
        } catch (NotSameCodeException notSameCodeException){
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_PHONE_EXIST);
        }
        catch (Exception e) {
            throw e;
        } finally {
            sw.stop();
            log.info("importProof finish in: " + sw.getTotalTimeSeconds());
        }
        return responseModel;
    }

    @PostMapping("format-obj")
    public ResponseModel formatObj(@RequestBody Directory entity, HttpServletRequest request) {
        final String action = "formatObj";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            DirectoryDTO dto = directoryService.formatObj(entity);
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
    public ResponseModel deleteMulti(@RequestBody Integer[] standard, HttpServletRequest request) {
        final String action = "doDeleteMulti";
        ResponseModel responseModel = new ResponseModel();
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            int checkExist = directoryService.deleteDirectories(standard, g, getUserFromToken(request), request);
            System.out.println(checkExist + " Check ");
            if(checkExist == 1){
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
                return responseModel;
            }else if(checkExist == 0){
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.BEING_USED);
                return responseModel;
            } else {
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

    @GetMapping("selectboxSta")
    public ResponseModel getSelectboxProof(HttpServletRequest request) {
        final String action = "getSelectboxSta";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(directoryService.SelectboxSta());
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PostMapping("format-objs")
    public ResponseModel formatObjs(@RequestBody List<Directory> list, HttpServletRequest request) {
        final String action = "formatObj";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            List<DirectoryDTO> dtos = directoryService.formatObj(list);
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(dtos);
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

    @GetMapping("getId")
    public ResponseModel findbyId(@RequestParam("id") Integer id) {
        final String action = "getId";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            Directory directory=directoryService.findByIdAndAndDelete(id);
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(directory);
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
