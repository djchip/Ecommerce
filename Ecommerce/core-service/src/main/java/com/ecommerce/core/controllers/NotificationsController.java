package com.ecommerce.core.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.ResponseModel;
import com.ecommerce.core.entities.Notifications;
import com.ecommerce.core.service.NotificationsService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("notifications")
@Slf4j
public class NotificationsController extends BaseController {
	private final String START_LOG = "Notifications {}";
	private final String END_LOG = "Notifications {} finished in: {}";

	@Autowired
	NotificationsService service;

	@GetMapping()
	public ResponseModel doSearch(@RequestParam(value = "username", required = true) String username,
			HttpServletRequest request) {
		final String action = "doSearch";
		StopWatch sw = new StopWatch();
		sw.start();
        log.info(START_LOG, action);
		try {
//			List<Notifications> result = service.findNotifyByUsername(username);
			ResponseModel responseModel = new ResponseModel();
			responseModel.setContent(null);
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
	public ResponseModel doCreate(@RequestBody Notifications entity) {
		final String action = "doCreate";
		StopWatch sw = new StopWatch();
		log.info(START_LOG, action);

		try {
			
			entity.setStatus(ConstantDefine.STATUS.ACTIVE);

			service.create(entity);

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
	
	@PutMapping("{id}")
    public ResponseModel seenOne(@PathVariable Integer id, HttpServletRequest request) {
        final String action = "seenOne";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            Notifications entity = service.retrieve(id);
            entity.setStatus(ConstantDefine.NOTIFY_STATUS.SEEN);
            service.update(entity, id);
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
	
	@PutMapping("seenAll/{username}")
    public ResponseModel seenAll(@PathVariable String username,HttpServletRequest request) {
        final String action = "seenOne";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
        	if(username.equals(getUserFromToken(request))) {
        		service.seenAll(username);
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
}
