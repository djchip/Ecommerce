package com.ecommerce.core.controllers;

import com.ecommerce.core.service.ReadFileService;
import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.ResponseModel;
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

@RestController
@RequestMapping("read-file")
@Slf4j
public class ReadFileController {

    @Autowired
    ReadFileService readFileService;

    @PostMapping("pdf")
    public ResponseModel readFile(
            @RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        StopWatch sw = new StopWatch();
        sw.start();
        log.info("readFile");
        ResponseModel responseModel = new ResponseModel();
        try {
            String content;
            String type = readFileService.detectDocTypeUsingDetector(file.getInputStream());
            if (type.equals("application/pdf")) {
                content = readFileService.readPDF(file.getInputStream());
            } else {
                content = readFileService.readFile(file.getInputStream());
            }
            responseModel.setContent(content);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
        } catch (Exception e) {
            throw e;
        } finally {
            sw.stop();
            log.info("readFile finish in: " + sw.getTotalTimeSeconds());
        }
        return responseModel;
    }

}

