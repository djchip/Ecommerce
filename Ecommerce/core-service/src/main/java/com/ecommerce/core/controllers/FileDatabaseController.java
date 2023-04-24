package com.ecommerce.core.controllers;

import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.ResponseModel;
import com.ecommerce.core.entities.FileDatabase;
import com.ecommerce.core.service.FileDatabaseService;
import com.ecommerce.core.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("file-database")
@Slf4j
public class FileDatabaseController extends BaseController {
    private final String START_LOG = "FileDatabaseController {}";
    private final String END_LOG = "FileDatabaseController {} finished in: {}";

    @Autowired
    private FileDatabaseService service;

    @Autowired
    private UserInfoService userInfoService;

    @PutMapping("/edit")
    public ResponseModel doUpdate(@RequestBody FileDatabase database, HttpServletRequest request) {
        final String action = "doUpdate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            database.setUnitId(userInfoService.findByUsername(getUserFromToken(request)).getUnit().getId());
            service.update(database, database.getId());
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


}
