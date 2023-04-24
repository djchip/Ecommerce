package com.ecommerce.core.controllers;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.entities.*;
import com.ecommerce.core.service.AppParamService;
import com.ecommerce.core.service.CriteriaService;
import com.ecommerce.core.service.DirectoryService;
import com.ecommerce.core.service.UndoLogService;
import com.google.gson.Gson;
import com.ecommerce.core.dto.AppParamDTO;
import com.ecommerce.core.dto.AppParamExhDTO;
import com.ecommerce.core.dto.PagingResponse;
import com.ecommerce.core.dto.ResponseModel;
import com.ecommerce.core.service.ProofService;
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

@RestController
@RequestMapping("document-field")
@Slf4j
public class AppParamController extends BaseController {
    private final String START_LOG = "DocumentField {}";
    private final String END_LOG = "DocumentField {} finished in: {}";
    private final String TABLE_NAME = "app_param";
    private final Gson g = new Gson();
    @Autowired
    AppParamService service;
    @Autowired
    UndoLogService undoLogService;
    @Autowired
    DirectoryService directoryService;
    @Autowired
    CriteriaService criteriaService;
    @Autowired
    ProofService proofService;

    @GetMapping("get-code-exh/{id}")
    public ResponseModel getCodeEXH(@PathVariable("id") Integer orgId) {
        final String action = "getCodeEXH";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            AppParam code = service.getCodeByOrg(orgId);
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(code);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("get-documentType")
    public ResponseModel doSearchDocumentType(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort", required = false) boolean sortDesc,
            @RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int perPage,
            HttpServletRequest request) {
        final String action = "doSearch";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            Pageable paging = PageRequest.of(currentPage, perPage);
            Page<AppParam> pageResult = null;
            if (keyword.equals("")) {
                keyword = null;
            } else {
                keyword = keyword.trim().toLowerCase();
            }
            pageResult = service.doSearchDocumentType(keyword, paging);
            if ((pageResult == null || pageResult.isEmpty())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy loại văn bản!!!");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }

            PagingResponse<AppParam> result = new PagingResponse<>();
            result.setTotal(pageResult.getTotalElements());
            result.setItems(pageResult.getContent());

            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(result);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("get-exh-code")
    public ResponseModel doSearchExhCode(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort", required = false) boolean sortDesc,
            @RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "5") int perPage,
            HttpServletRequest request) {
        final String action = "doSearch";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            Pageable paging = PageRequest.of(currentPage, perPage);
            Page<AppParam> pageResult = null;
            if (keyword.equals("")) {
                keyword = null;
            } else {
                keyword = keyword.trim().toLowerCase();
            }
            pageResult = service.doSearchExhCode(keyword, paging);
            if ((pageResult == null || pageResult.isEmpty())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy mã minh chứng !!!");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }

            PagingResponse<AppParam> result = new PagingResponse<>();
            result.setTotal(pageResult.getTotalElements());
            result.setItems(pageResult.getContent());

            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(result);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PostMapping("add-document-type")
    public ResponseModel doCreate(@RequestBody AppParamDTO dto, HttpServletRequest request) {
        final String action = "doCreate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        String documentTypeName = dto.getName();
        AppParam entity = new AppParam();
        try {
            AppParam checkExisted = service.checkExistedDocumentType(documentTypeName);
            if (checkExisted != null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.NAME_ALREADY_EXIST);
                responseModel.setErrorMessages("Loại văn bản đã tồn tại.");
                return responseModel;
            } else {
                String createdBy = getUserFromToken(request);

                entity.setName(dto.getName());
                entity.setNameEn(dto.getNameEn());
                entity.setCode("DOCUMENT");
                java.util.Date date = new java.util.Date();
                entity.setCreatedDate(date);
                entity.setUpdatedDate(date);
                entity.setCreatedBy(createdBy);
                entity.setDeleted(0);
                service.create(entity);

                UndoLog undoLog = UndoLog.undoLogBuilder()
                        .action(request.getMethod())
                        .requestObject(g.toJson(entity))
                        .status(ConstantDefine.STATUS.UNDO_NEW)
                        .url(request.getRequestURL().toString())
                        .description("Thêm cấu hình loại văn bản "+entity.getName()+" bởi " + createdBy)
                        .createdDate(LocalDateTime.now())
                        .createdBy(createdBy)
                        .tableName(TABLE_NAME)
                        .idRecord(entity.getId())
                        .build();
                undoLogService.create(undoLog);

                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setContent(entity);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
                return responseModel;
            }
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PostMapping("add-field")
    public ResponseModel doCreateField(@RequestBody AppParamDTO dto, HttpServletRequest request) {
        final String action = "doCreate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        String fieldName = dto.getName();
        AppParam entity = new AppParam();
        try {
            AppParam checkExisted = service.checkExistedField(fieldName);
            if (checkExisted != null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.NAME_ALREADY_EXIST);
                responseModel.setErrorMessages("Lĩnh vực đã tồn tại.");
                return responseModel;
            } else {
                String createdBy = getUserFromToken(request);

                entity.setName(dto.getName());
                entity.setNameEn(dto.getNameEn());
                entity.setCode("FIELD");
                java.util.Date date = new java.util.Date();
                entity.setCreatedDate(date);
                entity.setUpdatedDate(date);
                entity.setCreatedBy(createdBy);
                entity.setDeleted(0);
                service.create(entity);

                UndoLog undoLog = UndoLog.undoLogBuilder()
                        .action(request.getMethod())
                        .requestObject(g.toJson(entity))
                        .status(ConstantDefine.STATUS.UNDO_NEW)
                        .url(request.getRequestURL().toString())
                        .description("Thêm cấu hình lĩnh vực "+entity.getName()+" bởi " + createdBy)
                        .createdDate(LocalDateTime.now())
                        .createdBy(createdBy)
                        .tableName(TABLE_NAME)
                        .idRecord(entity.getId())
                        .build();
                undoLogService.create(undoLog);

                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setContent(entity);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
                return responseModel;
            }
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PostMapping("add-exh-code")
    public ResponseModel doCreateExhCode(@RequestBody AppParam entity, HttpServletRequest request) {
        final String action = "doCreate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        String exhCode = entity.getName();
        AppParam appParam = new AppParam();
        try {
            AppParam checkExisted = service.findAppParamByOrganization(entity.getOrganizationId());
            if (checkExisted != null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
                responseModel.setErrorMessages("Mã minh chứng đã tồn tại.");
                return responseModel;
            } else {
                String createdBy = getUserFromToken(request);

                appParam.setName(entity.getName());
                appParam.setNameEn(entity.getName());
                appParam.setCode("EXH_CODE");
                java.util.Date date = new java.util.Date();
                appParam.setCreatedDate(date);
                appParam.setUpdatedDate(date);
                appParam.setCreatedBy(createdBy);
                appParam.setDeleted(0);
                appParam.setEnCode(entity.isEnCode());
                appParam.setOrganizationId(entity.getOrganizationId());
                service.create(appParam);
                System.out.println("hhhhh = " +entity.toString());

                UndoLog undoLog = UndoLog.undoLogBuilder()
                        .action(request.getMethod())
                        .requestObject(g.toJson(appParam, AppParam.class))
                        .status(ConstantDefine.STATUS.UNDO_NEW)
                        .url(request.getRequestURL().toString())
                        .description("Thêm cấu hình mã mình chứng "+entity.getName()+" bởi " + createdBy)
                        .createdDate(LocalDateTime.now())
                        .createdBy(createdBy)
                        .tableName(TABLE_NAME)
                        .idRecord(appParam.getId())
                        .build();
                undoLogService.create(undoLog);

                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setContent(entity);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
                return responseModel;
            }
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PutMapping("edit-exh-code")
    @Transactional
    public ResponseModel doUpdateExhCode(@RequestBody AppParam entity, HttpServletRequest request) {
        final String action = "doCreate";
        StopWatch sw = new StopWatch();
        String exhCode = entity.getName();

        try {

            String createdBy = getUserFromToken(request);
            AppParam appParam = service.findById(entity.getId());
            Boolean oldEncode = appParam.isEnCode();
            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .revertObject(g.toJson(appParam, AppParam.class))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Cập nhập cấu hình mã minh chứng "+entity.getName()+" bởi " + createdBy)
                    .updatedDate(LocalDateTime.now())
                    .createdBy(createdBy)
                    .tableName(TABLE_NAME)
                    .idRecord(appParam.getId())
                    .build();


            String oldExhCode = appParam.getName();
            String newExhCode = entity.getName();
            appParam.setName(entity.getName());
            appParam.setNameEn(entity.getName());
            java.util.Date date = new java.util.Date();
            appParam.setUpdatedDate(date);
            appParam.setUpdatedBy(createdBy);
            appParam.setOrganizationId(entity.getOrganizationId());
            appParam.setEnCode(entity.isEnCode());
            service.update(appParam, appParam.getId());

            List<Directory> directories = directoryService.findByOrganizationId(appParam.getOrganizationId());
            List<Criteria> criteriaList = criteriaService.findByOrganizationId(appParam.getOrganizationId());
            List<Proof> proofs = proofService.findByOrganizationId(appParam.getOrganizationId());

//Update tu co 0 -> khong co 0
            if(oldEncode && !entity.isEnCode()) {
                //cap nhat ma tieu chuan
                for (Directory directory : directories) {
                    String preCode = directory.getCode().split("\\.")[0];
                    String tempStr = preCode.replace(oldExhCode, "");
                    String suffFix = directory.getCode().replace(preCode, "");
                    String newPreCode = preCode.replace("0", "");
                    if ("0".equals(tempStr.split("(?!^)")[0])) {
                        directory.setCode(newPreCode + suffFix);
                        directoryService.update(directory, directory.getId());
                    }
                }

                //cap nhat ma tieu chi
                for (Criteria criteria : criteriaList) {
                    String preCode = criteria.getCode().split("\\.")[0];
                    String tempStr = preCode.replace(oldExhCode, "");
                    String suffFix = criteria.getCode().replace(preCode, "");
                    String newPreCode = preCode.replace("0", "");
                    if ("0".equals(tempStr.split("(?!^)")[0])) {
                        criteria.setCode(newPreCode + suffFix);
                        criteriaService.update(criteria, criteria.getId());
                    }
                }

                //cap nhat ma minh chung
                for (Proof proof : proofs) {
                    String preCode = proof.getProofCode().split("\\.")[0];
                    String tempStr = preCode.replace(oldExhCode, "");
                    String suffFix = proof.getProofCode().replace(preCode, "");
                    String newPreCode = preCode.replace("0", "");
                    if ("0".equals(tempStr.split("(?!^)")[0])) {
                        proof.setProofCode(newPreCode + suffFix);
                        proofService.update(proof, proof.getId());
                    }
                }
            }

            //Update tu khong co 0 -> co 0
            if(!oldEncode && entity.isEnCode()){
                //cap nhat ma tieu chuan
                for (Directory directory : directories) {
                    String preCode = directory.getCode().split("\\.")[0];
                    String tempStr = preCode.replace(oldExhCode, "");
                    String suffFix = directory.getCode().replace(preCode, "");
                    String newPreCode = oldExhCode + "0" + tempStr;
                    if(tempStr.split("(?!^)").length < 2){
                        directory.setCode(newPreCode + suffFix);
                        directoryService.update(directory, directory.getId());
                    }
                }

                //cap nhat ma tieu chi
                for (Criteria criteria : criteriaList) {
                    String preCode = criteria.getCode().split("\\.")[0];
                    String tempStr = preCode.replace(oldExhCode, "");
                    String suffFix = criteria.getCode().replace(preCode, "");
                    String newPreCode = oldExhCode + "0" + tempStr;
                    if(tempStr.split("(?!^)").length < 2){
                        criteria.setCode(newPreCode + suffFix);
                        criteriaService.update(criteria, criteria.getId());
                    }
                }

                //cap nhat ma minh chung
                for (Proof proof : proofs) {
                    String preCode = proof.getProofCode().split("\\.")[0];
                    String tempStr = preCode.replace(oldExhCode, "");
                    String suffFix = proof.getProofCode().replace(preCode, "");
                    String newPreCode = oldExhCode + "0" + tempStr;
                    if(tempStr.split("(?!^)").length < 2){
                        proof.setProofCode(newPreCode + suffFix);
                        proofService.update(proof, proof.getId());
                    }
                }
            }



            //Update ma tieu chuan
            directoryService.updateExhCode(oldExhCode, newExhCode, appParam.getOrganizationId());
            //Update ma tieu chi
            criteriaService.updateExhCode(oldExhCode,newExhCode, appParam.getOrganizationId());
            //Update ma minh chung
            proofService.updateExhCode(oldExhCode, newExhCode, appParam.getOrganizationId());

            System.out.println("update = " +entity.toString());

            undoLog.setRequestObject(g.toJson(appParam));
            undoLogService.create(undoLog);

            ResponseModel responseModel = new ResponseModel();
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setContent(entity);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
//            }
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PostMapping("add-date-format")
    public ResponseModel doCreateDateFormat(@RequestBody AppParam entity, HttpServletRequest request) {
        final String action = "doCreate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        String dateFormat = entity.getName();
        AppParam appParam = new AppParam();
        try {
            AppParam checkExisted = service.findAppParamByName(dateFormat);
            if (checkExisted != null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.NAME_ALREADY_EXIST);
                responseModel.setErrorMessages("Định dạng đã tồn tại.");
                return responseModel;
            } else {
                appParam.setName(entity.getName());
                appParam.setNameEn(entity.getName());
                appParam.setCode("DATE_FORMAT");
                java.util.Date date = new java.util.Date();
                appParam.setCreatedDate(date);
                appParam.setUpdatedDate(date);
                appParam.setCreatedBy(getUserFromToken(request));
                appParam.setDeleted(0);
                service.create(appParam);

                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setContent(entity);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
                return responseModel;
            }
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("get-field")
    public ResponseModel doSearchField(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort", required = false) boolean sortDesc,
            @RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "5") int perPage,
            HttpServletRequest request) {
        final String action = "doSearch";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            Pageable paging = PageRequest.of(currentPage, perPage);
            Page<AppParam> pageResult = null;
            if (keyword.equals("")) {
                keyword = null;
            } else {
                keyword = keyword.trim().toLowerCase();
            }
            pageResult = service.doSearchField(keyword, paging);
            if ((pageResult == null || pageResult.isEmpty())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy lĩnh vực!!!");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }

            PagingResponse<AppParam> result = new PagingResponse<>();
            result.setTotal(pageResult.getTotalElements());
            result.setItems(pageResult.getContent());

            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(result);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("get-date-format")
    public ResponseModel doSearchDateFormat(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort", required = false) boolean sortDesc,
            @RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "5") int perPage,
            HttpServletRequest request) {
        final String action = "doSearch";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            Pageable paging = PageRequest.of(currentPage, perPage);
            Page<AppParam> pageResult = null;
            if (keyword.equals("")) {
                keyword = null;
            } else {
                keyword = keyword.trim().toLowerCase();
            }
            pageResult = service.doSearchDateFormat(keyword, paging);
            if ((pageResult == null || pageResult.isEmpty())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy lĩnh vực!!!");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }

            PagingResponse<AppParam> result = new PagingResponse<>();
            result.setTotal(pageResult.getTotalElements());
            result.setItems(pageResult.getContent());

            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(result);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("get-list-documentType")
    public ResponseModel getListDocumentType() {
        final String action = "getListDocumentType";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(service.getListDocumentType());
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("get-list-date-format")
    public ResponseModel getListDateTimeFormat() {
        final String action = "getListDateTimeFormat";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(service.getListDateTimeFormat());
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("get-list-field")
    public ResponseModel getListField() {
        final String action = "getListField";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(service.getListField());
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
    public ResponseModel getSelecboxOrganization(HttpServletRequest request) {
        final String action = "getSelecboxOrganization";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(service.getSelectbox());
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }


    @GetMapping("{id}")
    public ResponseModel doRetrieve(@PathVariable Integer id, HttpServletRequest request) {
        final String action = "doRetrieve";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            AppParam result = service.findById(id);
//            AppParamExhDTO result = service.findByIdd(id);

            if (result == null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }
            ResponseModel responseModel = new ResponseModel();
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

    @GetMapping("findby/{id}")
    public ResponseModel doRetrieve1(@PathVariable Integer id, HttpServletRequest request) {
        final String action = "doRetrieve";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            AppParamExhDTO result = service.findByIdd(id);
            if (result == null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }
            ResponseModel responseModel = new ResponseModel();
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

    @PutMapping("edit-document-type")
    public ResponseModel doUpdate(@RequestBody AppParam dto, HttpServletRequest request) {
        final String action = "doUpdate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            AppParam entity = service.findById(dto.getId());
            String createdBy = getUserFromToken(request);

            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .revertObject(g.toJson(entity))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Cập nhập cấu hình "+entity.getName()+" bởi " + createdBy)
                    .updatedDate(LocalDateTime.now())
                    .createdBy(createdBy)
                    .tableName(TABLE_NAME)
                    .idRecord(entity.getId())
                    .build();

            String updateBy = getUserFromToken(request);
            if (!dto.getName().equals("")) {
                String customName = dto.getName().trim();
                dto.setName(customName);
            }
            AppParam checkExistedName = service.findAppParamByName(dto.getName().trim());
            if (checkExistedName != null && !checkExistedName.getName().equalsIgnoreCase(entity.getName())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.NAME_ALREADY_EXIST);
                responseModel.setErrorMessages("Tên đơn vị đã tồn tại!!!");
                System.out.println(responseModel);
                return responseModel;
            } else {
                entity.setDeleted(ConstantDefine.STATUS.NOT_DELETE);
                entity.setUndoStatus(ConstantDefine.STATUS.CAN_BE_UNDO);
                entity.setName(dto.getName());
                entity.setNameEn(dto.getNameEn());
                entity.setUpdatedBy(updateBy);
                java.util.Date date = new java.util.Date();
                entity.setUpdatedDate(date);
                entity.setOrganizationId(dto.getOrganizationId());
                service.create(entity);


                undoLog.setRequestObject(g.toJson(entity));
                undoLogService.create(undoLog);
            }
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

    @GetMapping(value = "save-date-time-format/{id}")
    public ResponseModel saveDateTimeFormat(@PathVariable Integer id, HttpServletRequest request) {
        final String action = "doDelete";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            AppParam entity = service.findById(id);
            entity.setSelectedFormat(1);
            service.create(entity);
            List<AppParam> listNotSelectedFormat = service.getListNotSelectedFormat(id);
            for(AppParam a : listNotSelectedFormat){
                a.setSelectedFormat(0);
                service.create(a);
            }
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

    @GetMapping(value = "get-date-format-selected")
    public ResponseModel getDateFormatSelected() {
        final String action = "getDateFormatSelected";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            AppParam entity = service.getDateFormatSelected();
            ResponseModel responseModel = new ResponseModel();
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setContent(entity);
            System.out.println(entity);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
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

            AppParam entity = service.deleteAppParam(id);
            if(entity == null){
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_CANNOT_DELETE);
                return responseModel;
            }else{
                String createdBy = getUserFromToken(request);
                Gson g = new Gson();
                UndoLog undoLog = UndoLog.undoLogBuilder()
                        .action(request.getMethod())
                        .requestObject(g.toJson(entity))
                        .status(ConstantDefine.STATUS.UNDO_NEW)
                        .url(request.getRequestURL().toString())
                        .description("Xóa cấu hình "+entity.getName()+" bởi " + createdBy)
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

//            if (service.deleteAppParam(id) != null) {
//                ResponseModel responseModel = new ResponseModel();
//                responseModel.setStatusCode(HttpStatus.SC_OK);
//                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
//                return responseModel;
//            } else {
//                ResponseModel responseModel = new ResponseModel();
//                responseModel.setStatusCode(HttpStatus.SC_OK);
//                responseModel.setCode(ResponseFontendDefine.CODE_CANNOT_DELETE);
//                return responseModel;
//            }
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PostMapping("format-obj")
    public ResponseModel formatObj(@RequestBody AppParam entity, HttpServletRequest request) {
        System.out.println("đã chạy vào ====");
        final String action = "formatObj";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            AppParamDTO dto = service.formatObj(entity);
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
    public ResponseModel deleteMulti(@RequestBody Integer[] ids, HttpServletRequest request) {
        final String action = "doDeleteMulti";
        ResponseModel responseModel = new ResponseModel();
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {


            if(service.deleteAppp(ids, g, getUserFromToken(request), request)){
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
}
