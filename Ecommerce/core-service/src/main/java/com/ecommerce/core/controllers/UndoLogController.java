package com.ecommerce.core.controllers;

import com.ecommerce.core.exceptions.*;
import com.ecommerce.core.service.UndoLogService;
import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.PagingResponse;
import com.ecommerce.core.dto.ResponseModel;
import com.ecommerce.core.entities.UndoLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("undo-log")
@Slf4j
public class UndoLogController extends BaseController {
    private final String START_LOG = "UndoLog {}";
    private final String END_LOG = "UndoLog {} finished in: {}";
    private final String TABLE_NAME = "UndoLog";

    @Autowired
    private UndoLogService service;

    @GetMapping()
    public ResponseModel doSearch(@RequestParam(value = "tableName", required = false, defaultValue = "") String tableName,
                                  @RequestParam(value = "action", required = false, defaultValue = "") String action,
                                  @RequestParam(value = "startDate", required = false, defaultValue = "") String startDate,
                                  @RequestParam(value = "endDate", required = false, defaultValue = "") String endDate,
                                  @RequestParam(value = "createdBy", required = false, defaultValue = "") String createdBy,
                                  @RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int perPage,
                                  HttpServletRequest request) {
        final String action_func = "doSearch";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action_func);
        try {
            Pageable paging = PageRequest.of(currentPage, perPage);
            createdBy=createdBy.toLowerCase(Locale.ROOT);
            tableName=tableName.toLowerCase(Locale.ROOT);
            Page<UndoLog> pageResult = null;
            LocalDateTime start = null;
            LocalDateTime end = null;
            if (tableName.isEmpty()) {
                tableName = null;
            }
            if (action.isEmpty()) {
                action = null;
            }
            if(createdBy.isEmpty()){
                createdBy = null;
            }
            if (!startDate.isEmpty()) {
                List<String> myList = new ArrayList<String>(Arrays.asList(startDate.split("-")));
                start = LocalDateTime.of(Integer.parseInt(myList.get(0)), Integer.parseInt(myList.get(1)), Integer.parseInt(myList.get(2)), 0, 0);
            }
            if (!endDate.isEmpty()) {
                List<String> myList = new ArrayList<String>(Arrays.asList(endDate.split("-")));
                end = LocalDateTime.of(Integer.parseInt(myList.get(0)), Integer.parseInt(myList.get(1)), Integer.parseInt(myList.get(2)), 0, 0);
                end = end.plusDays(1);
            }
            pageResult = service.doSearch(tableName, action, start, end, createdBy, paging);
            PagingResponse<UndoLog> result = new PagingResponse<>();
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
            log.info(END_LOG, action_func, sw.getTotalTimeSeconds());
        }
    }

    @PostMapping("undo")
    @Transactional
    public ResponseModel undo(@RequestBody UndoLog undoLog, HttpServletRequest request) {
        final String action = "undo";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        ResponseModel responseModel = new ResponseModel();
        try {
            service.filterAndRedirect(undoLog);
            setStatusAndCode(responseModel, ResponseFontendDefine.CODE_SUCCESS);
        } catch (ExistsCriteria existsCriteria){
            setStatusAndCode(responseModel, ResponseFontendDefine.ALREADY_EXIST_CRITERIA);
        } catch (CategoryBeingUseException categoryBeingUseException){
            setStatusAndCode(responseModel, ResponseFontendDefine.CATEGORY_BEING_USED);
        } catch (ExistsCategoryException existsCategoryException){
            setStatusAndCode(responseModel, ResponseFontendDefine.EXIST_CATEGORY);
        } catch (OrganizationBeingUseException organizationBeingUseException){
            setStatusAndCode(responseModel, ResponseFontendDefine.ORGANIZATION_BEING_USED);
        } catch (ExitsOrganizationException exitsOrganizationException){
            setStatusAndCode(responseModel, ResponseFontendDefine.EXIST_ORGANIZATION);
        } catch (StandardBeingUseException standardBeingUseException){
            setStatusAndCode(responseModel, ResponseFontendDefine.STANDARD_BEING_USED);
        } catch (ExitsStandardException exitsStandardException){
            setStatusAndCode(responseModel, ResponseFontendDefine.EXIST_STANDARD);
        } catch (CriterionBeingUsedException criterionBeingUsedException){
            setStatusAndCode(responseModel, ResponseFontendDefine.CRITERIA_BEING_USED);
        } catch (ExistsCriteriaException existsCriteriaException){
            setStatusAndCode(responseModel, ResponseFontendDefine.EXIST_CRITERIA);
        } catch (AppParamBeingUsedException appParamBeingUsedException){
            setStatusAndCode(responseModel, ResponseFontendDefine.APP_PARAM_BEING_USED);
        } catch (ExistsAppParamException existsAppParamException){
            setStatusAndCode(responseModel, ResponseFontendDefine.EXIST_APP_PARAM);
        }  catch (ExistsUnitException existsUnitException){
            setStatusAndCode(responseModel, ResponseFontendDefine.UNIT);
        } catch (UnitException unitException){
            setStatusAndCode(responseModel, ResponseFontendDefine.EXIT_UNIT);
        } catch (ExitsCheckEmail checkEmail){
            setStatusAndCode(responseModel, ResponseFontendDefine.CHECK_EMAIL);

        }
        catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
        return responseModel;
    }

    private void setStatusAndCode(ResponseModel responseModel, int code){
        responseModel.setStatusCode(HttpStatus.SC_OK);
        responseModel.setCode(code);
    }
}
