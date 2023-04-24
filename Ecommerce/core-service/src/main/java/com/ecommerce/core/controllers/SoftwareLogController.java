package com.ecommerce.core.controllers;

import com.ecommerce.core.config.JwtConfig;
import com.ecommerce.core.repositories.SoftwareVersionRepository;
import com.ecommerce.core.service.SoftwareLogService;
import com.ecommerce.core.service.UndoLogService;
import com.google.gson.Gson;
import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.PagingResponse;
import com.ecommerce.core.dto.ResponseModel;
import com.ecommerce.core.entities.SoftwareLog;
import com.ecommerce.core.entities.SoftwareVersion;
import com.ecommerce.core.entities.UndoLog;
import com.ecommerce.core.exceptions.DetectExcelException;
import com.ecommerce.core.exceptions.ExistsSoftwareLog;
import com.ecommerce.core.security.SecurityCredentialsConfig;
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
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("software-log")


public class SoftwareLogController extends BaseController {
    private final String START_LOG = "Software Log {}";
    private final String END_LOG = "Software Log {} finished in {}";

    private final String TABLE_NAME = "software_log";
    private final Gson g = new Gson();
    @Autowired
    SoftwareLogService service;
    @Autowired
    SecurityCredentialsConfig config;
    @Autowired
    private JwtConfig jwtConfig;
    @Autowired
    SoftwareVersionRepository repo;
    @Autowired
    UndoLogService undoLogService;

    //    @GetMapping()
//    public ResponseModel doSearch(@RequestParam(value = "keyword", required = false) String keyword,
//                                  @RequestParam(value = "sort", required = false) boolean sortDesc,
//                                  @RequestParam(value = "startDate", required = false, defaultValue = "") String startDate,
//                                  @RequestParam(value = "endDate", required = false, defaultValue = "") String endDate,
//                                  @RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int perPage,
//                                  HttpServletRequest request) {
//        final String action = "doSearch";
//        StopWatch sw = new StopWatch();
//        sw.start();
//        log.info(START_LOG, action);
//        try {
//            Pageable paging = PageRequest.of(currentPage, perPage);
//            Page<SoftwareLog> pageResult = null;
//            LocalDateTime start = null;
//            LocalDateTime end = null;
//            if (keyword.trim().equals("")) {
//                keyword = null;
//            }
//            if (!startDate.isEmpty()) {
//                List<String> myList = new ArrayList<String>(Arrays.asList(startDate.split("-")));
//                start = LocalDateTime.of(Integer.parseInt(myList.get(0)), Integer.parseInt(myList.get(1)), Integer.parseInt(myList.get(2)), 0, 0);
//            }
//            if (!endDate.isEmpty()) {
//                List<String> myList = new ArrayList<String>(Arrays.asList(endDate.split("-")));
//                end = LocalDateTime.of(Integer.parseInt(myList.get(0)), Integer.parseInt(myList.get(1)), Integer.parseInt(myList.get(2)), 0, 0);
//                end = end.plusDays(1);
//            } else {
//                keyword = keyword.trim();
//            }
//            pageResult = service.doSearch(keyword, start, end, paging);
//
//            if ((pageResult == null || pageResult.isEmpty())) {
//                ResponseModel responseModel = new ResponseModel();
//                responseModel.setErrorMessages("Không tìm thấy log phần mềm nào.");
//                responseModel.setStatusCode(HttpStatus.SC_OK);
//                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
//                return responseModel;
//            }
//
//            PagingResponse<SoftwareLog> result = new PagingResponse<>();
//            result.setTotal(pageResult.getTotalElements());
//            result.setItems(pageResult.getContent());
//
//            ResponseModel responseModel = new ResponseModel();
//            responseModel.setContent(result);
//            responseModel.setStatusCode(HttpStatus.SC_OK);
//            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
//            return responseModel;
//        } catch (Exception e) {
//            throw handleException(e);
//        } finally {
//            sw.stop();
//            log.info(END_LOG, action, sw.getTotalTimeSeconds());
//        }
//    }
    @GetMapping()
    public ResponseModel doSearch(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "status", required = false) Integer status,
                                  @RequestParam(value = "startDate", required = false, defaultValue = "") String startDate,
                                  @RequestParam(value = "endDate", required = false, defaultValue = "") String endDate,
                                  @RequestParam(value = "sort", required = false) boolean sortDesc,
                                  @RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int perPage,
                                  HttpServletRequest request) {
        final String action_func = "doSearch";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action_func);
        try {
            Pageable paging = PageRequest.of(currentPage, perPage);
            Page<SoftwareLog> pageResult = null;
            LocalDateTime start = null;
            LocalDateTime end = null;
            if (keyword.equals("")) { keyword = null; } else { keyword = keyword.trim(); }
            if (!startDate.isEmpty()) {
                List<String> myList = new ArrayList<String>(Arrays.asList(startDate.split("-")));
                start = LocalDateTime.of(Integer.parseInt(myList.get(0)), Integer.parseInt(myList.get(1)), Integer.parseInt(myList.get(2)), 0, 0);
            }
            if (!endDate.isEmpty()) {
                List<String> myList = new ArrayList<String>(Arrays.asList(endDate.split("-")));
                end = LocalDateTime.of(Integer.parseInt(myList.get(0)), Integer.parseInt(myList.get(1)), Integer.parseInt(myList.get(2)), 0, 0);
                end = end.plusDays(1);
            }
            pageResult = service.doSearch(keyword, status, start, end, paging);
            PagingResponse<SoftwareLog> result = new PagingResponse<>();
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
            log.info(END_LOG, action_func, sw.getTotalTimeSeconds());
        }
    }

//    @GetMapping("find-by-id/{id}")
//    public ResponseModel findById(@PathVariable("id") int id) {
//        final String action = "doFindById";
//        StopWatch sw = new StopWatch();
//        log.info(START_LOG, action);
//        try {
//            ResponseModel responseModel = new ResponseModel();
//            SoftwareLog result = service.findById(id);
//            if (result.getUpdatedBy() == null) {
//                result.setSuccessfulrevisiontime(null);
//            }
//            responseModel.setContent(result);
//            responseModel.setStatusCode(HttpStatus.SC_OK);
//            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
//            return responseModel;
//
//        } catch (Exception e) {
//            throw handleException(e);
//        } finally {
//            log.info(END_LOG, action, sw.getTotalTimeSeconds());
//        }
//    }

    @PostMapping("add")
    public ResponseModel doCreate(@RequestBody SoftwareLog entity, HttpServletRequest request) {
        final String action = "doCreate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        String createdBy = getUserFromToken(request);

        try {
            String error = entity.getError().trim();
            String version = entity.getVersion().trim();
            List<SoftwareLog> checkExisted = service.findByVersion(version);
            SoftwareLog checkExistName = service.findByError(error);

            if (checkExisted.size() > 0) {
                if (checkExisted.size() > 0) {
                    ResponseModel responseModel = new ResponseModel();
                    responseModel.setStatusCode(HttpStatus.SC_OK);
                    responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
                    responseModel.setErrorMessages("Phiên bản đã tồn tại!!!");
                    return responseModel;
                }
            } else if (checkExistName != null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.NAME_ALREADY_EXIST);
                responseModel.setErrorMessages("Nhật ký lỗi đã tồn tại!!!");
                System.out.println(responseModel);
                return responseModel;
            } else {
                java.util.Date date = new java.util.Date();
                entity.setErrorlogtime(date);
                entity.setSuccessfulrevisiontime(date);
                entity.setUndoStatus(ConstantDefine.STATUS.CAN_BE_UNDO);
                entity.setDeleted(ConstantDefine.STATUS.DELETED);
//                String name = "admin";
                entity.setCreatedBy(createdBy);
                service.save(entity);
            }
            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .requestObject(g.toJson(entity))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Thêm mới nhật ký phiên bản "+entity.getError()+" bởi " + createdBy)
                    .createdDate(LocalDateTime.now())
                    .tableName(TABLE_NAME)
                    .createdBy(createdBy)
                    .idRecord(entity.getId())
                    .build();
            undoLogService.create(undoLog);
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(entity);
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

    @DeleteMapping("{id}")
    public ResponseModel doDelete(@PathVariable Integer id, HttpServletRequest request) {
        final String action = "doDelete";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            String createdBy = getUserFromToken(request);

//            service.delete(id);
            SoftwareLog entity = service.deleteError(id);
            Gson g = new Gson();
            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .requestObject(g.toJson(entity))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Xóa nhập nhật "+entity.getError()+ " bởi " + entity.getCreatedBy())
                    .createdDate(LocalDateTime.now())
                    .tableName(TABLE_NAME)
                    .idRecord(entity.getId())
                    .createdBy(createdBy)
                    .build();
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

    @GetMapping("{id}")
    public ResponseModel doRetrieve(@PathVariable Integer id) {
        final String action = "doRetrieve";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);

        try {
            SoftwareLog entity = service.retrieve(id);
            if (entity == null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy nhật ký lỗi!!!");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }

            ResponseModel responseModel = new ResponseModel();
            SoftwareLog result = service.findById(id);
            if (result.getUpdatedBy() == null) {
                result.setSuccessfulrevisiontime(null);
            }
            responseModel.setContent(entity);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PutMapping("edit")
    public ResponseModel doUpdate(@RequestBody SoftwareLog dto, HttpServletRequest request) {
        final String action = "doUpdate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        SoftwareLog entity = service.findById(dto.getId());
        String createdBy = getUserFromToken(request);

        try {
            SoftwareLog checkExisted = service.findByError(dto.getError());
            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .revertObject(g.toJson(entity))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Cập nhập nhật ký "+entity.getVersion()+" bởi " + createdBy)
                    .updatedDate(LocalDateTime.now())
                    .createdBy(createdBy)
                    .tableName(TABLE_NAME)
                    .idRecord(entity.getId())
                    .build();
            if (checkExisted != null && !checkExisted.getError().equals(entity.getError())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
                responseModel.setErrorMessages("Nhật ký lỗi đã tồn tại!!!");
                return responseModel;
            } else {
                entity.setError(dto.getError());
                entity.setVersion(dto.getVersion());
                entity.setAmendingcontent(dto.getAmendingcontent());
                entity.setNote(dto.getNote());
                entity.setStatus(dto.getStatus());

                java.util.Date date = new java.util.Date();
                entity.setSuccessfulrevisiontime(date);

                entity.setUpdatedBy(createdBy);


                service.save(entity);
            }
//            SoftwareLog entity = service.findByError(dto.getError().trim());

//            entity.setError(dto.getError().trim());
//            entity.setAmendingcontent(dto.getAmendingcontent().trim());
//            entity.setVersion(dto.getVersion().trim());
//            service.update(entity, entity.getId());
            // save undo

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

    @PostMapping("import-excel")
    public ResponseModel importLog(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException, ExistsSoftwareLog {
        StopWatch sw = new StopWatch();
        sw.start();
        log.info("importLog");
        ResponseModel responseModel = new ResponseModel();
        try {
            service.importLog(file, getUserFromToken(request));
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
        } catch (DetectExcelException excelException) {
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.EXCEL_WRONG_FORMAT);
        } catch (ExistsSoftwareLog existsSoftwareLog) {
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
        } catch (Exception e) {
            throw e;
        } finally {
            sw.stop();
            log.info("importLog finish in: " + sw.getTotalTimeSeconds());
        }
        return responseModel;
    }

    @PostMapping("format-obj")
    public ResponseModel formatObj(@RequestBody SoftwareLog entity, HttpServletRequest request) {
        final String action = "formatObj";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            SoftwareLog softwareLog = service.formatObj(entity);
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(entity);
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

    @PostMapping("add-version")
    public ResponseModel doCreateVersion(@RequestBody SoftwareVersion entity, HttpServletRequest request) {
        final String action = "doCreateVersion";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            String version = entity.getVersion().trim();
            String changeLogs = entity.getChangeLogs().trim();
//            SoftwareVersion checkExisted = service.findBySVersion(version);
//
//                if (checkExisted.getVersion() != null) {
//                    ResponseModel responseModel = new ResponseModel();
//                    responseModel.setStatusCode(HttpStatus.SC_OK);
//                    responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
//                    responseModel.setErrorMessages("Phiên bản đã tồn tại!!!");
//                    return responseModel;
//                }
//            else {
//                entity.setChangeLogs(changeLogs);
//                entity.setVersion(version);
////
//            }
            entity.setLastestVersion(1);
            repo.save(entity);
            repo.updateLastestVersion(entity.getId());
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(entity);

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
            if(service.deleteUser(ids, g, getUserFromToken(request), request)){
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