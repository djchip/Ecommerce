package com.ecommerce.core.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.OrganizationDTO;
import com.ecommerce.core.dto.PagingResponse;
import com.ecommerce.core.dto.ResponseModel;
import com.ecommerce.core.entities.Organization;
import com.ecommerce.core.entities.UndoLog;
import com.ecommerce.core.service.OrganizationService;
import com.ecommerce.core.service.ProgramsService;
import com.ecommerce.core.service.UndoLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("organization")
@Slf4j
public class OrganizationController extends BaseController {
    private final String START_LOG = "OrganizationController {}";
    private final String END_LOG = "OrganizationController {} finished in: {}";
    private final String TABLE_NAME = "organization";
    private final Gson g =  new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    @Autowired
    OrganizationService service;
    @Autowired
    ProgramsService programsService;
    @Autowired
    UndoLogService undoLogService;

    @Autowired
    HttpServletRequest request;
    @GetMapping()
    public ResponseModel doSearch(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "lang", required = false) String lang,
                                  @RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int perPage,
                                  HttpServletRequest request) {
        final String action = "doSearch";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            Pageable paging = PageRequest.of(currentPage, perPage);
            Page<Organization> pageResult = null;
            if (keyword.equals("")) {
                keyword = null;
            } else {
                keyword = keyword.trim();
            }
            pageResult = service.doSearch(keyword, paging);
            if ((pageResult == null || pageResult.isEmpty())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm tổ chức.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }
            PagingResponse<Organization> result = new PagingResponse<>();
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

    @GetMapping("getOrgForCriteria")
    public ResponseModel getOrganizationForCriteria() {
        final String action = "getSelecboxOrganization";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(service.findOrgForCriteria());
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

    @GetMapping("findOrgByProgramId/{id}")
    public ResponseModel findOrgByProgramId(@PathVariable Integer id) {
        final String action = "findOrgByProgramId";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(service.findByProgramId(id));
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

    @GetMapping("findOrgId/{id}")
    public ResponseModel findOrgId(@PathVariable Integer id) {
        final String action = "findOrgId";
        StopWatch sw = new StopWatch();
        sw.start();
        System.out.println("id =" + id);
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(service.findByIdAndDeletedNot(id, 0));
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            System.out.println("okeee"+service.findByIdAndDeletedNot(id, 0));
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("get-list-org")
    public ResponseModel getListOrganization(HttpServletRequest request) {
        final String action = "getSelecboxOrganization";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(service.getListOrg());
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
    public ResponseModel doRetrieve(@PathVariable Integer id) {
        final String action = "doRetrieve";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
//            Organization entity = service.retrieve(id);
            Organization entity = service.finbyID(id).get();
            if (entity.getUpdatedBy() == null) {
                entity.setUpdatedDate(null);
            }
            if (entity == null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy tổ chức.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }
            //Todo hardcode
            ResponseModel responseModel = new ResponseModel();
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

    @PostMapping()
    public ResponseModel doCreate(@RequestBody Organization entity, HttpServletRequest request) {
        final String action = "doCreate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        String ProgramsName = entity.getName();
        try {
            List<Organization> checkExisted = null;
            checkExisted = service.findByName(ProgramsName);
            if (checkExisted.size() > 0) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
                responseModel.setErrorMessages("tổ chức đã tồn tại.");
                return responseModel;
            } else {
                java.util.Date date = new java.util.Date();
                entity.setCreatedDate(date);
                entity.setUpdatedDate(date);
                String createdBy = getUserFromToken(request);
                entity.setDeleted(ConstantDefine.STATUS.NOT_DELETE);
                entity.setUndoStatus(ConstantDefine.STATUS.UNDO_CREATE);
                entity.setCreatedBy(getUserFromToken(request));
                service.create(entity);
                UndoLog undoLog = UndoLog.undoLogBuilder()
                        .action(request.getMethod())
                        .requestObject(g.toJson(entity))
                        .status(ConstantDefine.STATUS.UNDO_NEW)
                        .url(request.getRequestURL().toString())
                        .description("Thêm mới tổ chức "+entity.getName()+" bởi " + createdBy)
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

    @PutMapping()
    @Transactional
    public ResponseModel doUpdate(@RequestBody Organization dto, HttpServletRequest request) {
        final String action = "doUpdate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            Organization entity = service.retrieve(dto.getId());
            Organization checkExisted = null;
            checkExisted = service.findOrgByName(dto.getName());

            String createdBy = getUserFromToken(request);

            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .revertObject(g.toJson(entity, Organization.class))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Cập nhập tổ chức "+entity.getName()+" bởi " + createdBy)
                    .updatedDate(LocalDateTime.now())
                    .createdBy(createdBy)
                    .tableName(TABLE_NAME)
                    .idRecord(entity.getId())
                    .build();

            if (checkExisted != null && !checkExisted.getName().equals(entity.getName())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
                responseModel.setErrorMessages("Tổ chức đã tồn tại.");
                return responseModel;
            } else {
                java.util.Date date = new java.util.Date();
                entity.setUpdatedDate(date);
                entity.setUndoStatus(ConstantDefine.STATUS.NOT_Undo);
                entity.setNameEn(dto.getNameEn());
                entity.setCategories(dto.getCategories());
                entity.setEncode(dto.isEncode());
                entity.setDescriptionEn(dto.getDescriptionEn());
                entity.setName(dto.getName());
                entity.setUpdatedBy(getUserFromToken(request));
                entity.setDescription(dto.getDescription());
                entity.setCategoryId(dto.getCategoryId());

                undoLog.setRequestObject(g.toJson(entity, Organization.class));
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

    @DeleteMapping("{id}")
    public ResponseModel doDelete(@PathVariable Integer id, HttpServletRequest request) {
        final String action = "doDelete";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            Organization entity = service.deleteOr(id);
//            int checkDirectory=0;
//            for (int i = 0; i < directoryList.size(); i++) {
//                if(directoryList.get(i).getOrganizationId()==id){
//                    checkDirectory=1;
//                    break;
//                }
//            }
            if (entity == null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_CANNOT_DELETE);
                responseModel.setErrorMessages("Không thể xóa tổ chức chứa chương trình đánh giá.");
                return responseModel;
            } else {
                String createdBy = getUserFromToken(request);

                UndoLog undoLog = UndoLog.undoLogBuilder()
                        .action(request.getMethod())
                        .requestObject(g.toJson(entity))
                        .status(ConstantDefine.STATUS.UNDO_NEW)
                        .url(request.getRequestURL().toString())
                        .description("Xóa tổ chức "+entity.getName()+" bởi " + createdBy)
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


    @PostMapping("format-obj")
    public ResponseModel formatObj(@RequestBody Organization entity, HttpServletRequest request) {
        System.out.println("ddax chay vao = " );
        final String action = "formatObj";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            OrganizationDTO dto = service.formatObj(entity);
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
    public ResponseModel deleteMulti(@RequestBody Integer[] org) {
        final String action = "doDeleteMulti";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            if(service.deleteOr(org, g, getUserFromToken(request), request)){
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
                return responseModel;
            }else{
                ResponseModel responseModel = new ResponseModel();
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
