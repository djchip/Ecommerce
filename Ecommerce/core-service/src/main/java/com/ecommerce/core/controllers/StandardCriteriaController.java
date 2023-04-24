package com.ecommerce.core.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.ecommerce.core.service.StandardCriteriaService;
import com.ecommerce.core.dto.TreeNodeProgramDTO;
import com.ecommerce.core.service.RolesService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.ExhCodeAndIdDTO;
import com.ecommerce.core.dto.ResponseModel;
import com.ecommerce.core.dto.TreeNodeDTO;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("standard-criteria")
@Slf4j
public class StandardCriteriaController extends BaseController{
    private final String START_LOG = "STANDARD_CRITERIA {}";
    private final String END_LOG = "STANDARD_CRITERIA {} finished in: {}";

    @Autowired
    StandardCriteriaService service;

    @Autowired
    RolesService rolesService;

    @GetMapping({"setupTreeStandardCriteriaByProgramId/{id}"})
    public ResponseModel initTree(@PathVariable Integer id, HttpServletRequest request) {
        final String action = "initTree";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            String username = getUserFromToken(request);
            List<TreeNodeDTO> listResult = null;
            List<String> listRoleCodeByUsername = rolesService.getListRolesCodeByUsername(username);
            if(listRoleCodeByUsername.contains("ADMIN") || listRoleCodeByUsername.contains("Super Admin")){
                listResult = service.setupTreeStandardCriteria(id, null);
            } else {
                listResult = service.setupTreeStandardCriteria(id, username);
            }
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(listResult);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping({"setupTreeStandardCriteriaByProgramIdEn/{id}"})
    public ResponseModel initTreeEn(@PathVariable Integer id, HttpServletRequest request) {
        final String action = "initTree";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            String username = getUserFromToken(request);
            List<TreeNodeDTO> listResult = null;
            List<String> listRoleCodeByUsername = rolesService.getListRolesCodeByUsername(username);
            if(listRoleCodeByUsername.contains("ADMIN") || listRoleCodeByUsername.contains("Super Admin")){
                listResult = service.setupTreeStandardCriteriaEn(id, null);
            } else {
                listResult = service.setupTreeStandardCriteriaEn(id, username);
            }
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(listResult);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping({"setupTreeProgramStandardCriteriaByOrgId/{id}"})
    public ResponseModel initTreeProgramStandardCriteria(@PathVariable Integer id) {
        final String action = "initTree";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
//            String username = isAdminFromToken(request) ? null : getUserFromToken(request);
            List<TreeNodeProgramDTO> listResult = service.setupTreeProgramStandardCriteria(id, "username");

            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(listResult);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping({"setupTreeProgramStandardCriteriaByOrgIdEn/{id}"})
    public ResponseModel initTreeProgramStandardCriteriaEn(@PathVariable Integer id) {
        final String action = "initTree";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
//            String username = isAdminFromToken(request) ? null : getUserFromToken(request);
            List<TreeNodeProgramDTO> listResult = service.setupTreeProgramStandardCriteriaEn(id, "username");

            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(listResult);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }
    
    @GetMapping("getTreeStandard/{id}")
    public ResponseModel initTreeStandard(@PathVariable Integer id, HttpServletRequest request) {
        final String action = "initTreeStandard";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            String username = getUserFromToken(request);
            List<TreeNodeDTO> listResult = null;
            List<String> listRoleCodeByUsername = rolesService.getListRolesCodeByUsername(username);
            if(listRoleCodeByUsername.contains("ADMIN") || listRoleCodeByUsername.contains("Super Admin")){
                listResult = service.setupTreeSta(id, null);
            } else {
                listResult = service.setupTreeSta(id, username);
            }
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

    @GetMapping("getTreeStandardEn/{id}")
    public ResponseModel initTreeStandardEn(@PathVariable Integer id, HttpServletRequest request) {
        final String action = "initTreeStandard";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            String username = getUserFromToken(request);
            List<TreeNodeDTO> listResult = null;
            List<String> listRoleCodeByUsername = rolesService.getListRolesCodeByUsername(username);
            if(listRoleCodeByUsername.contains("ADMIN") || listRoleCodeByUsername.contains("Super Admin")){
                listResult = service.setupTreeStaEn(id, null);
            } else {
                listResult = service.setupTreeStaEn(id, username);
            }
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
    
    @GetMapping("getExhibitionCode")
	public ResponseModel getExhibitionCode(@RequestParam(value = "standard") String standard,
			@RequestParam(value = "criteria") String criteria, HttpServletRequest request) {
		final String action = "doCreate";
		StopWatch sw = new StopWatch();
		log.info(START_LOG, action);

		try {
			
			List<ExhCodeAndIdDTO> listCode = new ArrayList<ExhCodeAndIdDTO>();
			if (standard != null && !"".equals(standard)) {
				String[] listStandard = standard.split(",");
				for (int i = 0; i < listStandard.length; i++) {
					listCode.add(service.generateExhibitionCodeForStandardIdV2(Integer.parseInt(listStandard[i])));
				}
			}
			if (criteria != null && !"".equals(criteria)) {
				String[] listCriterias = criteria.split(",");
				for (int i = 0; i < listCriterias.length; i++) {
					listCode.add(service.generateExhibitionCodeForCriteriaIdWithStandardId(Integer.parseInt(listCriterias[i])));
				}
			}
            System.out.println("day la list" + listCode);

			ResponseModel responseModel = new ResponseModel();
			responseModel.setStatusCode(HttpStatus.SC_OK);
			responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
			responseModel.setContent(listCode);
			return responseModel;
		} catch (Exception e) {
			throw handleException(e);
		} finally {
			log.info(END_LOG, action, sw.getTotalTimeSeconds());
		}
	}
}
