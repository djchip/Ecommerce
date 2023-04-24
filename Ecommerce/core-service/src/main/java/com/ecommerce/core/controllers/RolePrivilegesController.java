package com.ecommerce.core.controllers;

import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.ResponseModel;
import com.ecommerce.core.dto.RolePrivilegesDTO;
import com.ecommerce.core.dto.TreeNodeDTO;
import com.ecommerce.core.service.RolePrivilegesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("role-privileges")
@Slf4j
public class RolePrivilegesController extends BaseController{
    private final String START_LOG = "ROLE_PRIVILEGES {}";
    private final String END_LOG = "ROLE_PRIVILEGES {} finished in: {}";

    @Autowired
    RolePrivilegesService service;

    @GetMapping()
    public ResponseModel initTree(HttpServletRequest request) {
        final String action = "initTree";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            
            List<TreeNodeDTO> listResult = service.setupTreePrivileges();
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(listResult);
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
    
    @GetMapping("{roleId}")
    public ResponseModel getListPrivilegesByRoleId(@PathVariable Integer roleId) {
        final String action = "getListPrivilegesByRoleId";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            List<Integer> listResult = service.getPrivilegesByRoleId(roleId);
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(listResult);
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

//    @PreAuthorize("{@authorize.authorize(#request)}")
    @PostMapping()
    public ResponseModel updatePrivileges(@RequestBody RolePrivilegesDTO dto, HttpServletRequest request) {
        final String action = "updatePrivileges";
        StopWatch sw = new StopWatch();
        ResponseModel responseModel = new ResponseModel();
        sw.start();
        log.info(START_LOG, action);
        try {
            service.updatePrivileges(dto);
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
