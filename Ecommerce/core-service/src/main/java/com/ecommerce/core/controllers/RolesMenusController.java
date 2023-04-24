package com.ecommerce.core.controllers;


import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.PagingResponse;
import com.ecommerce.core.dto.ResponseModel;
import com.ecommerce.core.entities.RoleMenus;
import com.ecommerce.core.service.RoleMenusService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("roles-menus")
@Slf4j
public class RolesMenusController extends BaseController{
    private final String START_LOG = "ROLE_MENUS {}";
    private final String END_LOG = "ROLE_MENUS {} finished in: {}";

    @Autowired
    RoleMenusService service;

    @GetMapping()
    public ResponseModel doSearch(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "sort", required = false) boolean sortDesc,
                                  @RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int perPage,
                                  HttpServletRequest request) {
        final String action = "doSearch";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            Pageable paging = PageRequest.of(currentPage, perPage);
            Page<RoleMenus> pageResult = null;
//            if (keyword.equals("")) {
//                keyword = null;
//            } else {
//                keyword = keyword.trim();
//            }
            pageResult = service.doSearch(paging);

            if ((pageResult == null || pageResult.isEmpty())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm menu hoặc nhóm quyền tương ứng.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }

            PagingResponse<RoleMenus> result = new PagingResponse<>();
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

    @PostMapping()
    public ResponseModel add(@RequestBody RoleMenus roleMenus, HttpServletRequest request) {
        final String action = "add";
        StopWatch sw = new StopWatch();
        ResponseModel responseModel = new ResponseModel();
        sw.start();
        log.info(START_LOG, action);
        try {

            RoleMenus roleMenus1 = service.searchRoleMenusWithId(roleMenus.getMenu().getId(),roleMenus.getRoles().getId());
            if(roleMenus1 != null){
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
                responseModel.setErrorMessages("Nhóm quyền và menu tương ứng đã tồn tại.");
                return responseModel;
            }
                service.create(roleMenus);
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

    @PutMapping()
    public ResponseModel update(@RequestBody RoleMenus roleMenus) {
        final String action = "update";
        StopWatch sw = new StopWatch();
        ResponseModel responseModel = new ResponseModel();
        sw.start();
        log.info(START_LOG, action);
        try {
            RoleMenus entity = service.retrieve(roleMenus.getRoleMenuId());
            entity.setRoles(roleMenus.getRoles());
            entity.setMenu(roleMenus.getMenu());
            service.update(entity,entity.getRoleMenuId());
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

    @GetMapping("get")
    public ResponseModel update(@RequestParam(value = "id") Integer id) {
        final String action = "update";
        StopWatch sw = new StopWatch();
        ResponseModel responseModel = new ResponseModel();
        sw.start();
        log.info(START_LOG, action);
        try {
            RoleMenus entity = service.retrieve(id);
            responseModel.setContent(entity);
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

    @DeleteMapping("delete/{id}")
    public ResponseModel delete(@PathVariable Integer id) {
        final String action = "delete";
        StopWatch sw = new StopWatch();
        ResponseModel responseModel = new ResponseModel();
        sw.start();
        log.info(START_LOG, action);
        try {
            service.delete(id);
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
}
