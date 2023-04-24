package com.ecommerce.core.controllers;

import com.ecommerce.core.repositories.CategoriesRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.CategoriesDTO;
import com.ecommerce.core.dto.PagingResponse;
import com.ecommerce.core.dto.ResponseModel;
import com.ecommerce.core.entities.Categories;
import com.ecommerce.core.entities.UndoLog;
import com.ecommerce.core.service.CategoriesService;
import com.ecommerce.core.service.UndoLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@RequestMapping("categories")
@Slf4j
public class CategoriesController extends BaseController {
    private final String START_LOG = "categories {}";
    private final String END_LOG = "categories {} finished in: {}";
    private final String TABLE_NAME = "categories";
    public static final Gson g = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    @Autowired
    private CategoriesRepository repo;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    UndoLogService undoLogService;
    @Autowired
    CategoriesService categoriesService;

    @GetMapping()
    public ResponseModel doSearch(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "lang", required = false) String lang,
                                  @RequestParam(defaultValue = "0") int currentPage,
                                  @RequestParam(defaultValue = "10") int perPage,
                                  HttpServletRequest request) {
        final String action = "doSearch";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            Pageable paging = PageRequest.of(currentPage, perPage);
            Page<CategoriesDTO> pageResult = null;
            if (keyword.equals("")) {
                keyword = null;
            } else {
                keyword = keyword.trim();
            }
            if (keyword != null) {
                keyword = keyword.toLowerCase();
            }
            pageResult = repo.doSearch(keyword, paging);
            if (lang.trim().equalsIgnoreCase("vn")) {
                pageResult = repo.doSearch(keyword, paging);
            } else {
                pageResult = repo.doSearchEn(keyword, paging);
            }
            if ((pageResult == null || pageResult.isEmpty())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy chương trình.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }
            PagingResponse<CategoriesDTO> result = new PagingResponse<>();
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
            Categories result = repo.findById(id).get();
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

    @GetMapping("get-categories")
    public ResponseModel getPrograms(@RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int perPage) {
        final String action = "doGet";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);

        try {
            Pageable paging = PageRequest.of(currentPage, perPage);
            Page<Categories> pageResult = null;
            pageResult = repo.findAll(paging);
            ResponseModel responseModel = new ResponseModel();
            if (pageResult == null || pageResult.isEmpty()) {
                responseModel.setErrorMessages("Không có chuyên mục nào.");
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

    @GetMapping("selectbox")
    public ResponseModel getSelectBoxCategory() {
        final String action = "doGet";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);

        try {
            ResponseModel responseModel = new ResponseModel();
//                responseModel.setContent(repo.findAll());
            responseModel.setContent(repo.listSelectbox());
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
    public ResponseModel doCreate(@RequestBody Categories entity, HttpServletRequest request) {
        final String action = "doCreate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        String ProgramsName = entity.getName();
        try {
//            Categories checkExisted = repo.findByName(ProgramsName);
            Categories checkExisted = categoriesService.findByDirectoryName(ProgramsName);

            if (checkExisted != null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
                responseModel.setErrorMessages("chuyên mục đã tồn tại.");
                return responseModel;
            } else {
                java.util.Date date = new java.util.Date();
                entity.setCreatedDate(date);
                entity.setUpdatedDate(date);

                String createdBy = getUserFromToken(request);
                entity.setCreatedBy(createdBy);

                entity.setDelete(ConstantDefine.STATUS.NOT_DELETE);
                entity.setUndoStatus(ConstantDefine.STATUS.CAN_BE_UNDO);
//                entity.setUpdatedBy(name);
                entity = repo.save(entity);

                UndoLog undoLog = UndoLog.undoLogBuilder()
                        .action(request.getMethod())
                        .requestObject(g.toJson(entity))
                        .status(ConstantDefine.STATUS.UNDO_NEW)
                        .url(request.getRequestURL().toString())
                        .description("Thêm chuyên mục "+entity.getName()+" bởi " + createdBy)
                        .createdDate(LocalDateTime.now())
                        .createdBy(createdBy)
                        .tableName(TABLE_NAME)
                        .idRecord(entity.getId())
                        .build();
                undoLogService.create(undoLog);

                ResponseModel responseModel = new ResponseModel();
                responseModel.setContent(entity);
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
                return responseModel;
            }
        } catch (Exception e) {
            System.out.println(e);
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseModel delete(@PathVariable("id") int id) {
        final String action = "doDelete";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
//            List<Organization> organizationList=organizationRepository.findAll();
//            int check=0;
//            for (int i = 0; i < organizationList.size(); i++) {
//              if(organizationList.get(i).getCategoryId()==id){
//                    check=1;
//                    break;
//              }
//            }
//            if(check==1){
//
//            }
//            else {
            Categories entity = categoriesService.deleteCar(id);
            if (entity == null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_CANNOT_DELETE);
                responseModel.setErrorMessages("Không thể xóa chuyên mục chứa tổ chức.");
                return responseModel;
            } else {
                String createdBy = getUserFromToken(request);
                Gson g = new Gson();
                UndoLog undoLog = UndoLog.undoLogBuilder()
                        .action(request.getMethod())
                        .requestObject(g.toJson(entity))
                        .status(ConstantDefine.STATUS.UNDO_NEW)
                        .url(request.getRequestURL().toString())
                        .description("Xóa chuyên mục "+entity.getName()+" bởi " + createdBy)
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

    @PutMapping()
    public ResponseModel doUpdate(@RequestBody Categories dto, HttpServletRequest request) {
        final String action = "doUpdate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        Categories entity = repo.findById(dto.getId()).get();
        String createdBy = getUserFromToken(request);
        try {
            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .revertObject(g.toJson(entity, Categories.class))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Cập nhập chuyên mục "+entity.getName()+" bởi " + createdBy)
                    .updatedDate(LocalDateTime.now())
                    .createdBy(createdBy)
                    .tableName(TABLE_NAME)
                    .idRecord(entity.getId())
                    .build();

            Categories checkExisted = repo.findByName(dto.getName());
            if (checkExisted != null && !checkExisted.getName().equals(entity.getName())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
                responseModel.setErrorMessages("tên chuyên mục đã tồn tại.");
                return responseModel;
            } else {
                entity.setName(dto.getName());
                entity.setNameEn(dto.getNameEn());
                entity.setDescriptionEn(dto.getDescriptionEn());
                entity.setDescription(dto.getDescription());
                entity.setNote(dto.getNote());
                java.util.Date date = new java.util.Date();
                entity.setUpdatedDate(date);
                entity.setUpdatedBy(createdBy);
                entity.setDelete(ConstantDefine.STATUS.NOT_DELETE);
                entity.setUndoStatus(ConstantDefine.STATUS.CAN_BE_UNDO);
                repo.save(entity);

                undoLog.setRequestObject(g.toJson(entity, Categories.class));
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
    public ResponseModel deleteMulti(@RequestBody Integer[] ids, HttpServletRequest request) {
        final String action = "doDeleteMulti";
        ResponseModel responseModel = new ResponseModel();
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            if(categoriesService.deleteCate(ids, g, getUserFromToken(request), request)){
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

    @GetMapping("getListCategoryByOrgId/{orgId}")
    public ResponseModel getListCategoryByOrgId(@PathVariable Integer orgId) {
        final String action = "getListCategoryByOrgId";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(repo.getListCategoryByOrgId(orgId));
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }


    @GetMapping("selectCategory-by-Org")
    public ResponseModel getListStandardByOrganizationId(@RequestParam(value = "OrgId", required = false) Integer OrgId) {
        final String action = "selectCategory-by-Org";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(categoriesService.getCategoriesByOrganizationId(OrgId));
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }
}
