package com.ecommerce.core.controllers;

import com.ecommerce.core.service.EmailConfigService;
import com.ecommerce.core.util.MailService;
import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.EmailConfigDTO;
import com.ecommerce.core.dto.PagingResponse;
import com.ecommerce.core.dto.ResponseModel;
import com.ecommerce.core.entities.EmailConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("email-config")
@Slf4j
public class EmailConfigController extends BaseController {
    private final String START_LOG = "EmailConfig {}";
    private final String END_LOG = "EmailConfig {} finished in: {}";

    @Autowired
    EmailConfigService service;

    @Autowired
    MailService mailServicel;

    @GetMapping()
    public ResponseModel doSearch(@RequestParam(defaultValue = "0") int currentPage,
                                  @RequestParam(defaultValue = "10") int perPage) {
        final String action = "doSearch";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            Pageable paging = PageRequest.of(currentPage, perPage);
            Page<EmailConfig> pageResult = null;
            pageResult = service.doSearch(paging);

            if ((pageResult == null || pageResult.isEmpty())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy văn bản!!!");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }

            PagingResponse<EmailConfig> result = new PagingResponse<>();
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

    @PutMapping()
    public ResponseModel doUpdate(@RequestBody EmailConfigDTO dto) {
        final String action = "Update";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            EmailConfig entity = service.retrieve(dto.getId());
            entity.setEmail(dto.getEmail());
            entity.setHost(dto.getHost());
            entity.setPort(dto.getPort());
            entity.setUsername(dto.getUsername());
            entity.setPassword(dto.getPassword());
            entity.setUpdatedDate(LocalDateTime.now());
            service.update(entity, entity.getId());

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

    @PostMapping("/send")
    public ResponseModel doSend(@RequestParam(defaultValue = "",required = false) String[] cc,
                                @RequestParam String subject,
                                @RequestParam String textContent,
                                @RequestParam(required = false) List<MultipartFile> atttachment,
                                @RequestParam String[] to) {
        final String action = "Send Mail";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            mailServicel.sendHtmlMail1("",to,cc,subject,textContent, atttachment);

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

    @GetMapping("/get-mail-to")
    public ResponseModel getListMailTo() {
        final String action = "getListMailTo";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        ResponseModel responseModel = new ResponseModel();
        try {
            responseModel.setContent(service.doSearchMailTo());
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
        } catch (Exception e) {
            throw e;
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
        return responseModel;
    }

}
