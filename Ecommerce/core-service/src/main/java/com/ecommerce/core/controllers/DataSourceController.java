package com.ecommerce.core.controllers;

import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.DataLabelDTO;
import com.ecommerce.core.dto.ResponseModel;
import com.ecommerce.core.entities.Form;
import com.ecommerce.core.exceptions.DetectExcelException;
import com.ecommerce.core.service.DataSourceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("data-source")
@Slf4j
public class DataSourceController extends BaseController {

    @Autowired
    DataSourceService dataSourceService;

    @PostMapping("import-excel")
    public ResponseModel importProof(@RequestParam("formName") String formName, @RequestParam("year") Integer year,
                                     @RequestParam("numTitle") Integer numTitle, @RequestParam("isForm") Boolean isForm,
                                     @RequestParam("formKey") Integer formKey, @RequestParam("startTitle") Integer startTitle,
            @RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        StopWatch sw = new StopWatch();
        sw.start();
        log.info("importProof");
        ResponseModel responseModel = new ResponseModel();
        try {
            responseModel.setContent(dataSourceService.importDataSource(file, getUserFromToken(request), numTitle, isForm, formName, year, formKey, startTitle, request));
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
        } catch (DetectExcelException excelException) {
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.EXCEL_WRONG_FORMAT);
        } catch (Exception e) {
            throw e;
        } finally {
            sw.stop();
            log.info("importProof finish in: " + sw.getTotalTimeSeconds());
        }
        return responseModel;
    }

    @PostMapping("import-report")
    public ResponseModel importReport(@RequestParam("rowStart") Integer rowStart, @RequestParam("rowHeader") Integer rowHeader,
                                     @RequestParam("fileUpload") MultipartFile fileUpload, HttpServletRequest request) throws IOException {
        StopWatch sw = new StopWatch();
        sw.start();
        log.info("ImportProof");
        ResponseModel responseModel = new ResponseModel();
        try {
            Form form = dataSourceService.importReport(fileUpload, rowStart, rowHeader, getUserFromToken(request));
            List<DataLabelDTO> labelByKey = dataSourceService.getLabelByKey(form.getFormKey());
            if (labelByKey.isEmpty()){
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }
            responseModel.setContent(labelByKey);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
        } catch (DetectExcelException excelException) {
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.EXCEL_WRONG_FORMAT);
        } catch (Exception e) {
            throw e;
        } finally {
            sw.stop();
            log.info("Import Report finish in: " + sw.getTotalTimeSeconds());
        }
        return responseModel;
    }
}
