package com.ecommerce.core.controllers;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.PagingResponse;
import com.ecommerce.core.dto.ProductCategoryDTO;
import com.ecommerce.core.dto.ProductDTO;
import com.ecommerce.core.dto.ResponseModel;
import com.ecommerce.core.entities.*;
import com.ecommerce.core.service.ProductCategoryService;
import com.ecommerce.core.service.ProductImagesService;
import com.ecommerce.core.service.ProductService;
import com.ecommerce.core.util.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("product")
@Slf4j
public class ProductController extends BaseController {
    private final String START_LOG = "Product {}";
    private final String END_LOG = "Product {} finished in: {}";

    @Autowired
    private ProductService service;

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private ProductImagesService productImagesService;

    private final String PATH_FILE = "D:\\DOAN\\Ecommerce\\frontend\\src\\assets\\images\\chien";

    @GetMapping("get-all")
    public ResponseModel doSearch(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                  @RequestParam(defaultValue = "0") int currentPage,
                                  @RequestParam(defaultValue = "10") int perPage) {
        final String action = "doSearch";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            System.out.println("CATE ID " + categoryId);
            Pageable paging = PageRequest.of(currentPage, perPage);
            Page<ProductDTO> pageResult = null;
            if (keyword.equals("")) {
                keyword = null;
            } else {
                keyword = keyword.trim();
            }
            pageResult = service.doSearch(keyword, categoryId, paging);

            if ((pageResult == null || pageResult.isEmpty())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy danh mục sản phẩm.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }

            PagingResponse<ProductDTO> result = new PagingResponse<>();
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

    @GetMapping("get-list-category")
    public ResponseModel getListCategory() {
        final String action = "getListCategory";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(productCategoryService.findAllByActiveIsTrue());
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PostMapping("add-product")
    public ResponseModel doCreate(@RequestBody Product product, HttpServletRequest request) {
        final String action = "doCreate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            product.setCreatedBy(getUserFromToken(request));
            ResponseModel responseModel = new ResponseModel();
            responseModel.setStatusCode(HttpStatus.SC_OK);
            Product saveProduct = service.create(product);
            if (saveProduct == null) {
                responseModel.setCode(ResponseFontendDefine.NAME_ALREADY_EXIST);
            } else {
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
                responseModel.setContent(saveProduct);
            }
            return responseModel;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PostMapping("add-product-image")
    public ResponseModel doCreateProductImg(@RequestParam("imgProduct") List<MultipartFile> multipartFiles,
                                            @RequestParam("id") Integer productId) {
        final String action = "doCreateProductImg";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setStatusCode(HttpStatus.SC_OK);
            if(multipartFiles != null && !multipartFiles.isEmpty()){
                String pathFile = PATH_FILE + "/" + productId;
                for(MultipartFile file : multipartFiles){
                    String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
                    FileUploadUtil.saveFiles(pathFile , fileName, file);
                    ProductImages productImages = new ProductImages();
                    productImages.setPathImage(pathFile + "/" + fileName);
                    productImages.setProductId(productId);
                    productImagesService.create(productImages);
                }
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            } else {
                responseModel.setCode(ResponseFontendDefine.ERROR);
            }
            return responseModel;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("detail-product/{id}")
    public ResponseModel doRetrieve(@PathVariable Integer id) {
        final String action = "doRetrieve";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            Product product = service.retrieve(id);
            if (product == null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy danh mục sản phẩm.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }

            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(product);
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
            Integer deleteOrUnlock = service.deleteProduct(id);
            ResponseModel responseModel = new ResponseModel();
            responseModel.setStatusCode(HttpStatus.SC_OK);
            if(deleteOrUnlock == 1){
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            } else if (deleteOrUnlock == 2){
                responseModel.setCode(ResponseFontendDefine.UNLOCK_SUCCESS);
            } else {
                responseModel.setErrorMessages("Đã có lỗi xảy ra");
                responseModel.setCode(ResponseFontendDefine.ERROR);
            }
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PutMapping("edit-product")
    public ResponseModel doUpdate(@RequestBody Product entity, HttpServletRequest request) {
        final String action = "doDelete";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setStatusCode(HttpStatus.SC_OK);

            Product product = service.doUpdate(entity, entity.getId(), getUserFromToken(request));
            if(product != null){
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            } else {
                responseModel.setCode(ResponseFontendDefine.NAME_ALREADY_EXIST);
            }
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("get-list-product")
    public ResponseModel getListProduct() {
        final String action = "getListProduct";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(service.getListProduct());
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }
}
