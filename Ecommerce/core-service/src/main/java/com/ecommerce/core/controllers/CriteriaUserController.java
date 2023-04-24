package com.ecommerce.core.controllers;

import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.CriDTO;
import com.ecommerce.core.dto.CriteriaUserDTO;
import com.ecommerce.core.dto.ResponseModel;
import com.ecommerce.core.service.CriteriaUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("criteria-user")
@Slf4j
public class CriteriaUserController extends BaseController{
    private  final String START_LOG="CRITERIA_PROOF {}";
    private  final String END_LOG="CRITERIA_PROOF {} finished in: {}";

    @Autowired
    private CriteriaUserService service;

    @GetMapping("{userId}")
    public ResponseModel getListProStaCriDTOByUserId(@PathVariable String userId) {
        final String action = "getListPrivilegesByRoleId";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            List<CriDTO> listIdPro = null;
            List<CriDTO> listIdProSta = null;
            List<CriDTO> listIdProStaCri = null;
            String[] ids = userId.split(",");
            System.out.println(Arrays.toString(ids));
            List<CriteriaUserDTO> criteriaUsers = service.getListCriteriaUserByUserId(Integer.valueOf(ids[0]), Integer.valueOf(ids[1]), Integer.valueOf(ids[2]));
            System.out.println(criteriaUsers + "  vv");
            for(CriteriaUserDTO criteriaUser : criteriaUsers) {
                if(criteriaUser.getStandardId() == null && criteriaUser.getCriteriaId() == null){
                    listIdPro = service.getListProDTOByUserId(Integer.valueOf(ids[0]), Integer.valueOf(ids[1]), Integer.valueOf(ids[2]));
                } else if(criteriaUser.getCriteriaId() == null) {
                    listIdProSta = service.getListProStaDTOByUserId(Integer.valueOf(ids[0]), Integer.valueOf(ids[1]), Integer.valueOf(ids[2]));
                } else {
                    listIdProStaCri = service.getListProStaCriDTOByUserId(Integer.valueOf(ids[0]), Integer.valueOf(ids[1]), Integer.valueOf(ids[2]));
                }
            }
            List<CriDTO> listResult = new ArrayList<>();
            if (listIdPro != null) {
                for(CriDTO criDTO : listIdPro){
                    if(criDTO.getId() != null){
                        listResult.add(criDTO);
                    }
                }
            }
            if(listIdProSta != null) {
                for(CriDTO criDTO : listIdProSta){
                    if(criDTO.getId() != null){
                        listResult.add(criDTO);
                    }
                }
            }
            if(listIdProStaCri != null) {
                for(CriDTO criDTO : listIdProStaCri){
                    if(criDTO.getId() != null){
                        listResult.add(criDTO);
                    }
                }
            }
            System.out.println(listResult + "DAY");
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
}
