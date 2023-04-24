package com.ecommerce.core.controllers;

import java.text.Normalizer;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.ProductCategoryDTO;
import com.ecommerce.core.entities.*;
import com.ecommerce.core.dto.PagingResponse;
import com.ecommerce.core.dto.ResponseModel;
import com.ecommerce.core.service.ProductCategoryService;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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
import java.util.List;

@RestController
@RequestMapping("product-category")
@Slf4j
public class ProductCategoryController extends BaseController {

    private final String START_LOG = "Product category {}";
    private final String END_LOG = "Product category {} finished in: {}";

    @Autowired
    private ProductCategoryService service;

    @GetMapping("get-all")
    public ResponseModel doSearch(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(defaultValue = "0") int currentPage,
                                  @RequestParam(defaultValue = "10") int perPage,
                                  HttpServletRequest request) {
        final String action = "doSearch";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            Pageable paging = PageRequest.of(currentPage, perPage);
            Page<ProductCategoryDTO> pageResult = null;
            if (keyword.equals("")) {
                keyword = null;
            } else {
                keyword = keyword.trim();
            }
            pageResult = service.doSearch(keyword, paging);

            if ((pageResult == null || pageResult.isEmpty())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy danh mục sản phẩm.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }

            PagingResponse<ProductCategoryDTO> result = new PagingResponse<>();
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

    @PostMapping("add-category")
    public ResponseModel doCreate(@RequestBody ProductCategory entity, HttpServletRequest request) {
        final String action = "doCreate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);

        try {
            String inputCategoryName = entity.getCategoryName().trim();
            ProductCategory checkExistedByCategoryName = service.findByCategoryName(inputCategoryName);
            if (checkExistedByCategoryName != null) {
                if (checkExistedByCategoryName.getCategoryName() != null) {
                    ResponseModel responseModel = new ResponseModel();
                    responseModel.setStatusCode(HttpStatus.SC_OK);
                    responseModel.setCode(ResponseFontendDefine.NAME_ALREADY_EXIST);
                    responseModel.setErrorMessages("Tên danh mục đã tồn tại.");
                    return responseModel;
                }
            } else {
                String createdBy = getUserFromToken(request);
                String normalized = Normalizer.normalize(inputCategoryName, Normalizer.Form.NFD);

                // Loại bỏ các ký tự không phải ASCII và chuyển thành chữ thường
                String slug = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                        .replaceAll("[^\\p{ASCII}]", "").toLowerCase();
                entity.setCreatedBy(createdBy);
                entity.setSlug(slug.replaceAll(" ", "-"));
                entity.setUpdatedDate(LocalDateTime.now());
                entity.setActive(true);
                service.create(entity);
            }

            ResponseModel responseModel = new ResponseModel();
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PutMapping("update")
    public ResponseModel doUpdate(@RequestBody ProductCategory entity, HttpServletRequest request) {
        final String action = "doDelete";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ProductCategory productCategory = service.findByIdAndActiveTrue(entity.getId());

            String inputName = entity.getCategoryName();

            ProductCategory checkExistedCategoryName = null;
            if (!inputName.equalsIgnoreCase(productCategory.getCategoryName())) {
                checkExistedCategoryName = service.findByCategoryName(inputName);
            }

            if (checkExistedCategoryName != null && !checkExistedCategoryName.getCategoryName().equalsIgnoreCase(productCategory.getCategoryName())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.NAME_ALREADY_EXIST);
                responseModel.setErrorMessages("Tên danh mục đã tồn tại!!!");
                return responseModel;
            } else {
                productCategory.setCategoryName(entity.getCategoryName());
                productCategory.setParentId(entity.getParentId());
                productCategory.setDescription(entity.getDescription());
                productCategory.setUpdatedBy(getUserFromToken(request));
                productCategory.setUpdatedDate(LocalDateTime.now());
                service.update(productCategory, productCategory.getId());
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

    @DeleteMapping("delete/{id}")
    public ResponseModel doDelete(@PathVariable Integer id) {
        final String action = "doDelete";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            if(service.deleteCategory(id)){
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
                return responseModel;
            } else {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.BEING_USED);
                return responseModel;
            }

        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @DeleteMapping("delete-multi")
    public ResponseModel deleteMultiple(@RequestBody Integer[] ids) {
        final String action = "doDeleteMulti";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            if(service.deleteMultiple(ids)){
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
                return responseModel;
            } else {
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

    @GetMapping("find-all")
    public ResponseModel findAllByActiveIsTrue() {
        final String action = "findAllByActiveIsTrue";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(service.findAllByActiveIsTrue());
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("detail-category/{id}")
    public ResponseModel doRetrieve(@PathVariable Integer id) {
        final String action = "doRetrieve";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ProductCategoryDTO dto = service.findProductCategoryDTOById(id);
            if (dto == null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy danh mục sản phẩm.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }

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
}
