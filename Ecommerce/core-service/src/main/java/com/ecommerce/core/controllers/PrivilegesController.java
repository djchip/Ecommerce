package com.ecommerce.core.controllers;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.PagingResponse;
import com.ecommerce.core.dto.ResponseModel;
import com.ecommerce.core.entities.Privileges;
import com.ecommerce.core.entities.Roles;
import com.ecommerce.core.entities.UserInfo;
import com.ecommerce.core.service.MenusService;
import com.ecommerce.core.service.PrivilegesSevice;
import com.ecommerce.core.service.RolesService;
import com.ecommerce.core.service.UserInfoService;
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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("privileges")
@Slf4j
public class PrivilegesController extends BaseController{
    private final String START_LOG = "privileges {}";
    private final String END_LOG = "privileges {} finished in: {}";

    @Autowired
    PrivilegesSevice privilegesSevice;

    @Autowired
    MenusService menusService;

    @Autowired
    RolesService rolesService;

    @Autowired
    UserInfoService userInfoService;

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
            Page<Privileges> pageResult = null;
            if (keyword.equals("")) {
                keyword = null;
            } else {
                keyword = keyword.trim();
            }
            pageResult = privilegesSevice.doSearch(keyword, paging);

            if ((pageResult == null || pageResult.isEmpty())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy quyền.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }

            PagingResponse<Privileges> result = new PagingResponse<>();
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

    @PostMapping("/add")
    public ResponseModel doCreate(@RequestBody Privileges entity, HttpServletRequest request) {
        final String action = "doCreate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);

        try {
            String roleCode = entity.getCode().trim();
            String roleName = entity.getName().trim();
            Privileges checkExisted = privilegesSevice.findByCode(roleCode);
            Privileges checkExistName = privilegesSevice.findByName(roleName);

            if (checkExisted != null) {
                if (checkExisted.getCode() != null) {
                    ResponseModel responseModel = new ResponseModel();
                    responseModel.setStatusCode(HttpStatus.SC_OK);
                    responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
                    responseModel.setErrorMessages("Mã quyền đã tồn tại.");
                    return responseModel;
                }
            }else if (checkExistName != null){
                if (checkExistName.getName() != null) {
                    ResponseModel responseModel = new ResponseModel();
                    responseModel.setStatusCode(HttpStatus.SC_OK);
                    responseModel.setCode(ResponseFontendDefine.NAME_ALREADY_EXIST);
                    responseModel.setErrorMessages("Tên quyền đã tồn tại.");
                    return responseModel;
                }
            }else {
                entity.setStatus(ConstantDefine.STATUS.ACTIVE);
                entity.setCreatedBy(getUserFromToken(request));
                entity.setCreatedDate(LocalDateTime.now());
                entity.setUpdatedDate(LocalDateTime.now());
                privilegesSevice.create(entity);
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

    @GetMapping("{id}")
    public ResponseModel doRetrieve(@PathVariable Integer id) {
        final String action = "doRetrieve";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            Privileges entity = privilegesSevice.retrieve(id);
            if (entity == null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy quyền.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }
            
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

    @PutMapping("/edit")
    public ResponseModel doUpdate(@RequestBody Privileges dto, HttpServletRequest request) {
        final String action = "doUpdate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            String roleName = dto.getName().trim();
            System.out.println("dto: "+dto);
            Privileges checkExistName = privilegesSevice.findByNameAndId(dto.getId(), roleName);
            if (checkExistName != null){
                if (checkExistName.getName() != null) {
                    ResponseModel responseModel = new ResponseModel();
                    responseModel.setStatusCode(HttpStatus.SC_OK);
                    responseModel.setCode(ResponseFontendDefine.NAME_ALREADY_EXIST);
                    responseModel.setErrorMessages("Tên quyền đã tồn tại.");
                    return responseModel;
                }
            }else {
                Privileges entity = privilegesSevice.retrieve(dto.getId());
                entity.setName(dto.getName());
                entity.setMenuID(dto.getMenuID());
                entity.setMethod(dto.getMethod());
                entity.setUrl(dto.getUrl());
                entity.setUpdatedBy(getUserFromToken(request));
                entity.setUpdatedDate(LocalDateTime.now());
                privilegesSevice.update(entity, entity.getId());
            }
            ResponseModel responseModel = new ResponseModel();
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (
                Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }

    }

    @PutMapping("/delete/{id}")
    public ResponseModel doDelete(@PathVariable Integer id, HttpServletRequest request) {
        final String action = "doDelete";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            Privileges entity = privilegesSevice.retrieve(id);
            entity.setStatus(ConstantDefine.STATUS.DELETED);
            privilegesSevice.update(entity, id);
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

    @GetMapping("get-menus")
    public ResponseModel getAllChildMenu() {
        final String action = "getListMenus";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(menusService.findAllChilds());
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("action")
    public ResponseModel getAction(HttpServletRequest request){
        final String action = "getAction";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            UserInfo username = userInfoService.findByUsername(getUserFromToken(request));
            List<Integer> roleIds = new ArrayList<>();
            for (Roles role : username.getRole()){
                roleIds.add(role.getId());
            }
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(privilegesSevice.getListPrivilegesAction(roleIds));
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
