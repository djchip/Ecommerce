package com.ecommerce.core.controllers;

import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import com.ecommerce.core.util.FileUploadUtil;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.ResponseModel;
import com.ecommerce.core.entities.ExhibitionCollection;
import com.ecommerce.core.service.ExhibitionCollectionService;
import com.ecommerce.core.service.FilesStorageService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("file")
@Slf4j
public class FilesController extends BaseController {
	private final String START_LOG = "FilesController {}";
	private final String END_LOG = "FilesController {} finished in: {}";
	
	@Autowired
	FilesStorageService storageService;
	
	@Autowired
	ExhibitionCollectionService service;
	
	@GetMapping()
	public ResponseModel collectedFiles(@RequestParam(value = "programId", required = true) Integer programId,
			HttpServletRequest request) {
		final String action = "collectedFiles";
		StopWatch sw = new StopWatch();
		sw.start();
		log.info(START_LOG, action);
		try {
			ResponseModel responseModel = new ResponseModel();
			responseModel.setContent(service.getListTreeNode(programId));
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

	@Transactional
	@PostMapping("/upload")
	public ResponseModel uploadFile(@RequestParam("programId") Integer programId, @RequestParam("file") MultipartFile file) {
		try {
			boolean isFolder = false;
			String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
			String uploadDir = ConstantDefine.FILE_PATH.COLLECTION + "/" + programId;
			if(fileName.contains("/")) {
				String[] split = fileName.split("/");
				uploadDir += "/" + split[0];
				fileName = split[1];
				isFolder = true;
			}
			//Check existed
			if(service.checkExistedFile(programId, fileName)) {
				ResponseModel responseModel = new ResponseModel();
				responseModel.setStatusCode(HttpStatus.SC_OK);
				responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
				return responseModel;
			}
			FileUploadUtil.saveFiles(uploadDir, fileName, file);
			ExhibitionCollection collection = new ExhibitionCollection();
			if(isFolder) {
				collection.setIsDirectory(ConstantDefine.IS_DIRECTORY.TRUE);
			} else {
				collection.setIsDirectory(ConstantDefine.IS_DIRECTORY.FALSE);
			}
			collection.setName(fileName);
			collection.setPath(uploadDir);
			collection.setProgramId(programId);
			service.create(collection);

			ResponseModel responseModel = new ResponseModel();
			responseModel.setStatusCode(HttpStatus.SC_OK);
			responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
			return responseModel;
		} catch (Exception e) {
			throw handleException(e);
		}
	}
	
	@DeleteMapping()
	public ResponseModel deleteCollection(@RequestBody List<Integer> collections) {
		try {
			for (Integer integer : collections) {
				ExhibitionCollection collection = service.retrieve(integer);
				if (collection != null) {
					FileUploadUtil.deleteFile(collection.getPath(), collection.getName());
					service.delete(collection.getId());
				}
			}
			ResponseModel responseModel = new ResponseModel();
			responseModel.setStatusCode(HttpStatus.SC_OK);
			responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
			return responseModel;
		} catch (Exception e) {
			throw handleException(e);
		}
	}

	@Transactional
	@GetMapping("/get-link")
	public ResponseModel getLink(@RequestParam("link") String link,
								 @RequestParam(value = "isLink") Boolean isLink, HttpServletRequest request) {
		try {
			ResponseModel responseModel = new ResponseModel();
			responseModel.setStatusCode(HttpStatus.SC_OK);
			responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
			responseModel.setContent(storageService.setLinkOrFile(link, isLink));
			return responseModel;
		} catch (Exception e) {
			throw handleException(e);
		}
	}
}
