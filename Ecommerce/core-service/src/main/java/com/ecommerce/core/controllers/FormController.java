package com.ecommerce.core.controllers;

import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.entities.*;
import com.ecommerce.core.exceptions.DetectExcelException;
import com.ecommerce.core.repositories.FormRepository;
import com.ecommerce.core.service.*;
import com.ecommerce.core.util.FileDownloadUtil;
import com.ecommerce.core.util.FileUploadUtil;
import com.ecommerce.core.dto.FormCopyDTO;
import com.ecommerce.core.dto.FormDTO;
import com.ecommerce.core.dto.PagingResponse;
import com.ecommerce.core.dto.ResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/form")
public class FormController extends BaseController {
    private final String START_LOG = "form {}";
    private final String END_LOG = "form {} finished in: {}";

    private final String PATH_FILE = "D:\\HocVien\\hoc-vien-nong-nghiep\\DocumentManagement\\neo-frontend\\src\\assets\\form-file\\";

    @Autowired
    FormService formService;


    @Autowired
    ReportService reportService;

    @Autowired
    FormRepository formRepository;

    @Autowired
    UndoLogService undoLogService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    AssessmentService assessmentService;

    @Autowired
    DataSourceService dataSourceService;

    @Autowired
    FileDatabaseService fileDatabaseService;

    @Value("${form-path}")
    private String PATH_FILE_FORM;

    @Value("${csdl-path}")
    private String PATH_FILE_DB;

    @GetMapping("get-menu")
    public ResponseModel getMenus() {
        final String action = "doRetrieve";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(formService.listAll());
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping()
    public ResponseModel doSearch(@RequestParam(value = "name", defaultValue = "") String name,
                                  @RequestParam(value = "yearOfApplication", required = false) Integer yearOfApplication,
                                  @RequestParam(value = "uploadTime", required = false) String uploadTime,
                                  @RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int perPage,
                                  HttpServletRequest request) {
        final String action = "doSearch";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            Pageable paging = PageRequest.of(currentPage, perPage);
            Page<Form> pageResult = null;
            if (name.equals("")) {
                name = null;
            }
            System.out.println("UPLOAD TIME " + uploadTime);
            pageResult = formService.doSearch(name, yearOfApplication, uploadTime, paging);
            if ((pageResult == null || pageResult.isEmpty())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy Form nào.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }
            PagingResponse<Form> result = new PagingResponse<>();
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

    @GetMapping("database")
    public ResponseModel doSearchDataBase(@RequestParam(value = "name", defaultValue = "") String name,
                                  @RequestParam(value = "yearOfApplication", required = false) Integer yearOfApplication,
                                  @RequestParam(value = "uploadTime", required = false) String uploadTime,
                                  @RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int perPage,
                                  HttpServletRequest request) {
        final String action = "doSearch";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            Pageable paging = PageRequest.of(currentPage, perPage);
            Page<FormDTO> pageResult = null;
            if (name.equals("")) {
                name = null;
            }
            UserInfo userInfo = userInfoService.findByUsername(getUserFromToken(request));
            pageResult = formService.doSearchDataBase(name, yearOfApplication, uploadTime, userInfo.getUnit().getId(), paging);
            if ((pageResult == null || pageResult.isEmpty())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy biểu mẫu nào.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }
            PagingResponse<FormDTO> result = new PagingResponse<>();
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

    @GetMapping("{id}")
    public ResponseModel doRetrieve(@PathVariable Integer id) {
        final String action = "doRetrieve";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(formService.retrieve(id));
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("get-form/{id}")
    public ResponseModel getDetailForm(@PathVariable Integer id, HttpServletRequest request) {
        final String action = "doRetrieve";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            UserInfo userInfo = userInfoService.findByUsername(getUserFromToken(request));
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(formService.getDetailForm(id, userInfo.getUnit().getId()));
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("download/{id}")
    public ResponseEntity<?> downloadFileForm(@PathVariable("id") Integer id) {
        Form form = formService.findById(id);
        FileDownloadUtil downloadUtil = new FileDownloadUtil();
        Resource resource = null;
        try {
            String path = PATH_FILE_FORM + "/" + form.getFormKey() + "/" + form.getName();
            resource = downloadUtil.getFileAsResource(path, form.getFileName());
        } catch (IOException e) {
            return null;
        }
        if (resource == null) {
            return new ResponseEntity<>("File not found", org.springframework.http.HttpStatus.NOT_FOUND);
        }
        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }

    @GetMapping("downloadDB/{id}")
    public ResponseEntity<?> downloadFileDB(@PathVariable("id") Integer id, HttpServletRequest request) {
        Form form = formService.findById(id);
        UserInfo userInfo = userInfoService.findByUsername(getUserFromToken(request));
        FileDatabase fileDatabase = fileDatabaseService.findByFormKeyAndUnitIdAndFormId(form.getFormKey(), userInfo.getUnit().getId(), form.getId());
        if(fileDatabase != null){
            FileDownloadUtil downloadUtil = new FileDownloadUtil();
            Resource resource = null;
            try {
                String[] arr = fileDatabase.getPathFile().split("/");
                String path = PATH_FILE_DB + "/" + form.getFormKey() + "_" + userInfo.getUnit().getId() + "/" + form.getName();
                resource = downloadUtil.getFileAsResource(path, arr[arr.length - 1]);
            } catch (IOException e) {
                return null;
            }
            if (resource == null) {
                return new ResponseEntity<>("File not found", org.springframework.http.HttpStatus.NOT_FOUND);
            }
            String contentType = "application/octet-stream";
            String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                    .body(resource);
        }
        return null;
    }

    @PostMapping("add")
    public ResponseModel doCreate(@RequestBody Form entity, HttpServletRequest request) {
        final String action = "doCreate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            entity.setCreateBy(getUserFromToken(request));
            formService.create(entity);
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(entity);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PostMapping("copy")
    public ResponseModel doCopyForm(@RequestBody FormCopyDTO entity, HttpServletRequest request) {
        final String action = "doCreate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            Form form = formService.doCopyForm(entity, getUserFromToken(request), request);
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(form);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @DeleteMapping("{id}")
    public ResponseModel doDelete(@PathVariable Integer id, HttpServletRequest request) {
        final String action = "doDelete";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            String createdBy = getUserFromToken(request);
            Form entity = formService.deleteForm(id);
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

    @PutMapping("/edit")
    public ResponseModel doUpdate(@RequestBody Form dto, HttpServletRequest request) {
        final String action = "doUpdate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            dto.setUpdateBy(getUserFromToken(request));
            formService.doUpdate(dto,dto.getId(), getUserFromToken(request));
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

    @PutMapping("/edit-without-file")
    public ResponseModel doUpdateWithoutFile(@RequestBody Form dto, HttpServletRequest request) {
        final String action = "doUpdate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
//            dto.setUpdateBy(getUserFromToken(request));
            formService.updateWithoutFile(dto,dto.getId(),getUserFromToken(request));
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

    @DeleteMapping("delete-multi")
    public ResponseModel deleteMulti(@RequestBody List<Form> forms, HttpServletRequest request) {
        final String action = "deleteMulti";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            boolean canDelete = formService.deleteMulti(forms);
            if(canDelete){
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
                return responseModel;
            } else {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_CANNOT_DELETE);
                return responseModel;
            }
        } catch (Exception e) {
            throw handleException(e);
        } finally {
             log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @DeleteMapping("delete-multi-csdl")
    public ResponseModel deleteMultiCsdl(@RequestBody List<FormDTO> forms, HttpServletRequest request) {
        final String action = "deleteMultiCsdl";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            formService.deleteCSDL(forms);
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

    @PostMapping("copy-multi")
    public ResponseModel copyMultiForm(@RequestBody List<Form> forms, HttpServletRequest request) {
        final String action = "copyMultiForm";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            System.out.println("FORM " + forms);
            formService.copyMulti(forms, getUserFromToken(request), request);
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

    @GetMapping("wopi/files/{id}/contents")
    public ResponseEntity<?> getFile(@PathVariable("id") Integer id) {
        Form form = formService.retrieve(id);
        FileDownloadUtil downloadUtil = new FileDownloadUtil();
        Resource resource = null;
        String pathFile = form.getPathFile();
        String[] listStr = pathFile.split("/");
        String filename = listStr[listStr.length - 1];
        try {
            String path = pathFile.replace(filename, "");
            resource = downloadUtil.getFileAsResource(path, filename);
        } catch (IOException e) {
            return null;
        }

        if (resource == null) {
            return new ResponseEntity<>("File not found", org.springframework.http.HttpStatus.NOT_FOUND);
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }

    @GetMapping("wopi/files/{id}")
    public ResponseEntity<?> getDataFile(@PathVariable("id") Integer id, HttpServletRequest request) throws IOException {
        Form form = formService.retrieve(id);
        UserInfo userInfo = userInfoService.findByUsername(getUserFromToken(request));
        List<Unit> listUnit = form.getUnits();
        String pathFile = form.getPathFile();
        String[] listStr = pathFile.split("/");
        String filename = listStr[listStr.length - 1];

        FileInfo fileInfo = new FileInfo();
        fileInfo.setBaseFileName(filename);
        fileInfo.setUserId(userInfo.getId());
        fileInfo.setSize(FileUploadUtil.getCharCount(pathFile));
        fileInfo.setUserFriendlyName(userInfo.getEmail());
        if(listUnit.contains(userInfo.getUnit())){
            fileInfo.setUserCanWrite(true);
        } else {
            fileInfo.setUserCanWrite(false);
        }

        System.out.println("====================================" + fileInfo.isUserCanWrite() + "============================");
        String contentType = "application/json; charset=utf-8";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.ACCEPT_CHARSET)
                .body(fileInfo);
    }

    @PostMapping("wopi/files/{id}/contents")
    public ResponseEntity<?> saveFile(@PathVariable("id") Integer id, InputStream content, HttpServletRequest request) throws IOException, InvalidFormatException, DetectExcelException {
        Form form = formService.retrieve(id);
        String pathFile = form.getPathFile();
        String[] listStr = pathFile.split("/");
        String filename = listStr[listStr.length - 1];
        String path = pathFile.replace(filename, "");
        File file = new File(pathFile);
        FileUtils.copyInputStreamToFile(content, file);

        MultipartFile multipartFile = new MockMultipartFile(filename, Files.newInputStream(file.toPath()));
        dataSourceService.importDataSource(multipartFile, form.getCreateBy(), form.getNumTitle(), false, form.getName(), form.getYearOfApplication(), form.getFormKey(), form.getStartTitle(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping({"getTotalForm"})
    public ResponseModel getTotalForm(@RequestParam(value = "byYear") String year) {
        final String action = "getTotalForm";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(formService.totalForm(Integer.parseInt(year)).size());
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping({"getListFormNotUploaded"})
    public ResponseModel getListFormNotUploaded(@RequestParam(value = "byYear") String year) {
        final String action = "getListFormUploaded";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            System.out.println(" HH" + formService.listFormNotUploaded(Integer.parseInt(year)).size());
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(formService.listFormNotUploaded(Integer.parseInt(year)).size());
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping({"getTotalFormByYear"})
    public ResponseModel getTotalFormByYear(@RequestParam(value = "byYear") String year) {
        final String action = "getTotalFormByYear";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(formService.listFormNotUploaded(Integer.parseInt(year)).size() + formService.listFormUploaded(Integer.parseInt(year)).size());
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
