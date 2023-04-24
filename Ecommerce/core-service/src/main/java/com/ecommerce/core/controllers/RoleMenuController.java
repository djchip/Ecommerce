package com.ecommerce.core.controllers;

import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.ResponseModel;
import com.ecommerce.core.dto.RoleMenuDTO;
import com.ecommerce.core.service.RoleMenuSerrvice;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("role-menu")
@Slf4j
public class RoleMenuController extends BaseController {
    private final String START_LOG = "ROLE_MENUS {}";
    private final String END_LOG = "ROLE_MENUS {} finished in: {}";

    @Autowired
    RoleMenuSerrvice service;

    @GetMapping("{roleId}")
    public ResponseModel getListPrivilegesByRoleId(@PathVariable Integer roleId) {
        final String action = "getListRoleMenu";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {

            List<Integer> listResult = service.getMenuByRoleId(roleId);
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


    @PostMapping()
    public ResponseModel updateRoleMenu(@RequestBody RoleMenuDTO dto, HttpServletRequest request) {
        final String action = "updatePrivileges";
        StopWatch sw = new StopWatch();
        ResponseModel responseModel = new ResponseModel();
        sw.start();
        log.info(START_LOG, action);
        try {
            service.updateRoleMenu(dto);
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
