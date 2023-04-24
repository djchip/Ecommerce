package com.ecommerce.core.controllers;

import com.ecommerce.core.dto.*;
import com.ecommerce.core.entities.*;
import com.ecommerce.core.repositories.AssessmentRepository;
import com.ecommerce.core.service.*;
import com.ecommerce.core.util.FileDownloadUtil;
import com.ecommerce.core.util.FileUploadUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.constants.ResponseFontendDefine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("assessment")
@Slf4j
public class AssessmentController extends BaseController {

    private final String START_LOG = "Assessment {}";

    private final String END_LOG = "Assessment {} finished in: {}";
    private final String TABLE_NAME = "assessment";
    private final Gson g = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    @Autowired
    AssessmentService service;

    @Autowired
    ExhibitionFileService exhibitionFileService;

    @Autowired
    AssessmentRepository repository;

    @Autowired
    ProofService proofService;

    @Autowired
    ReadFileService readFileService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UndoLogService undoLogService;

    @Autowired
    HttpServletRequest request;

    @GetMapping("new")
    public ResponseModel newFileTemp(HttpServletRequest request) throws IOException {
        Integer order = service.getMaxOrder() + 1;
        System.out.println("Order: "+order);
        LocalDateTime today = LocalDateTime.now();
        String fileName = "HVN_" + order + "_" + String.valueOf(today.getDayOfMonth())
                + "_" + String.valueOf(today.getMonthValue()) + "_" + String.valueOf(today.getYear()) + ".docx";
        System.out.println("Name File: "+fileName);

        Path template = Paths.get(ConstantDefine.FILE_PATH.ASSESSMENT + "/Temp/Template.docx");
        Path dir = Paths.get(ConstantDefine.FILE_PATH.ASSESSMENT + "/" + order);
        Path file = Paths.get(ConstantDefine.FILE_PATH.ASSESSMENT + "/" + order + "/" + fileName);
        if (!Files.exists(dir)){
            Files.createDirectories(dir);
        }
        Files.copy(template, file, StandardCopyOption.REPLACE_EXISTING);

        Assessment entity = new Assessment();
        entity.setCreatedBy(getUserFromToken(request));
        entity.setFile(fileName);
        entity.setTemp(1);
        entity.setOrderAss(order);
        entity.setDelete(0);
        entity.setUndoStatus(1);
        Assessment saveAss = service.create(entity);
        ResponseModel responseModel = new ResponseModel();
        responseModel.setContent(saveAss.getId());
        responseModel.setStatusCode(HttpStatus.SC_OK);
        responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
        return responseModel;
    }

    @GetMapping("temp")
    public ResponseModel getAssessmentTemp() {
        final String action = "Temp";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);

        try {
            List<Assessment> assessments = service.getAssessmentTemp();
            if (assessments == null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy báo cáo tự đánh giá!!!");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }

            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(assessments);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("expert")
    public ResponseModel doSearchExpert(@RequestParam(value = "keyword", required = false) String keyword,
                                        @RequestParam(value = "sort", required = false) boolean sortDesc,
                                        @RequestParam(value = "programId", required = false) Integer evaluated,
                                        @RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int perPage,
                                        HttpServletRequest request) {
        final String action = "doSearch";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);

        try {
            Pageable paging = PageRequest.of(currentPage, perPage);
            Page<AssessmentDTO> pageResult = null;
            if (keyword.trim().equals("")) {
                keyword = null;
            } else {
                keyword = keyword.trim().toLowerCase();
            }
            UserInfo user = userInfoService.findByUsername(getUserFromToken(request));
            Integer userId = user.getId();
            pageResult = service.doSearch(userId, keyword, paging,evaluated);

            if ((pageResult == null || pageResult.isEmpty())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy báo cáo tự đánh giá!!!");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }

            PagingResponse<AssessmentDTO> result = new PagingResponse<>();
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

    @GetMapping()
    public ResponseModel doSearch(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "lang", required = false) String lang,
                                  @RequestParam(value = "sort", required = false) boolean sortDesc,
                                  @RequestParam (value = "reportType", required = false) Integer reportType,
                                  @RequestParam(value = "programId", required = false) Integer programId,
                                  @RequestParam(value = "directoryId", required = false) Integer directoryId,
                                  @RequestParam(value = "criteriaId", required = false) Integer criteriaId,
                                  @RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int perPage,
                                  HttpServletRequest request) {
        final String action = "doSearch";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);

        try {
            Pageable paging = PageRequest.of(currentPage, perPage);
            Page<AssDTO> pageResult = null;
            String user = getUserFromToken(request);
            if (keyword.trim().equals("")) {
                keyword = null;
            } else {
                keyword = keyword.trim().toLowerCase();
            }
            if (lang.trim().equalsIgnoreCase("vn")) {
                pageResult = service.doSearch(keyword,programId,directoryId,criteriaId,reportType, user, paging);
            } else {
                pageResult = service.doSearchEn(keyword,programId,directoryId,criteriaId,reportType, user, paging);
            }

            if ((pageResult == null || pageResult.isEmpty())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy báo cáo tự đánh giá!!!");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }

            PagingResponse<AssDTO> result = new PagingResponse<>();
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
            Assessment entity = service.retrieve(id);
//            Assessment entity=repository.findById(id).get();
            if (entity == null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy báo cáo tự đánh giá!!!");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }
            AssDTO dto = new AssDTO();
            dto.setId(entity.getId());
            dto.setName(entity.getName());
            dto.setNameEn(entity.getNameEn());
            dto.setDescription(entity.getDescription());
            dto.setDescriptionEn(entity.getDescriptionEn());
            dto.setCreatedBy(entity.getCreatedBy());
            dto.setCreatedDate(entity.getCreatedDate());
            dto.setUpdatedBy(entity.getUpdatedBy());
            dto.setUpdatedDate(entity.getUpdatedDate());
            dto.setEvaluated(entity.getEvaluated());
            dto.setFile(entity.getFile());
            dto.setComment(entity.getComment());
            dto.setProgramId(entity.getProgramId());
            dto.setDirectoryId(entity.getDirectoryId());
            dto.setCriteriaId(entity.getCriteriaId());
            dto.setUser(entity.getUser());
            dto.setViewers(entity.getViewers());

            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(dto);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("detail/{id}")
    public ResponseModel getDetail(@RequestParam(value = "lang", required = false) String lang, @PathVariable Integer id) {
        final String action = "doRetrieve";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);

        try {
            AssessmentDTO entity = null;
            if (lang.trim().equalsIgnoreCase("vn")) {
                entity = service.getDetailVN(id);
            } else {
                entity = service.getDetailEN(id);
            }
            if (entity == null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy báo cáo tự đánh giá!!!");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }

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

    @PostMapping()
    public ResponseModel doCreate(@RequestParam(value = "fileUpload", required = false) MultipartFile multipartFile,
                                  @RequestParam Integer id,
                                  @RequestParam Integer reportType,
                                  @RequestParam String name, @RequestParam String nameEn,
                                  @RequestParam String description, @RequestParam String descriptionEn,
                                  @RequestParam Integer programId, @RequestParam(required = false) List<UserInfo> user,
                                  @RequestParam(required = false) List<UserInfo> viewers,
                                  @RequestParam(required = false) Integer directoryId,
                                  @RequestParam(required = false) Integer criteriaId,HttpServletRequest request) {
        final String action = "doCreate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            String nameAss = name.trim();
            Assessment checkExisted = service.findByName(nameAss);

            if (checkExisted != null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.NAME_ALREADY_EXIST);
                responseModel.setErrorMessages("Tên báo cáo tự đánh giá đã tồn tại!!!");
                System.out.println(responseModel);
                return responseModel;
            } else {
                String createdBy = getUserFromToken(request);

                Assessment entity = service.retrieve(id);
                java.util.Date date = new java.util.Date();
                entity.setCreatedDate(date);
                entity.setUpdatedDate(date);
                entity.setDelete(ConstantDefine.STATUS.NOT_DELETE);
                entity.setUndoStatus(ConstantDefine.STATUS.CAN_BE_UNDO);
                entity.setName(name);
                entity.setNameEn(nameEn);
                entity.setDescription(description);
                entity.setDescriptionEn(descriptionEn);
                entity.setProgramId(programId);
                entity.setDirectoryId(directoryId);
                entity.setCriteriaId(criteriaId);
                entity.setUser(user);
                entity.setViewers(viewers);
                entity.setReportType(reportType);
                entity.setTemp(0);
                entity.setCreatedBy(getUserFromToken(request));
                entity.setUpdatedBy("");
                if (multipartFile != null){
                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                    entity.setFile(fileName);
                    String uploadDir = ConstantDefine.FILE_PATH.ASSESSMENT + "/" + entity.getOrderAss();
                    FileUploadUtil.cleanDir(uploadDir);
                    FileUploadUtil.saveFiles(uploadDir, fileName, multipartFile);
                    if (fileName.contains(".docx")){
                        XWPFDocument document = new XWPFDocument(OPCPackage.open(uploadDir + "/" + fileName));
                        XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                        entity.setContent(extractor.getText());
                    }
                }
                service.update(entity, id);
                UndoLog undoLog = UndoLog.undoLogBuilder()
                        .action(request.getMethod())
                        .requestObject(g.toJson(entity, Assessment.class))
                        .status(ConstantDefine.STATUS.UNDO_NEW)
                        .url(request.getRequestURL().toString())
                        .description("Thêm mới báo cáo "+entity.getName()+" bởi " + createdBy)
                        .createdDate(LocalDateTime.now())
                        .createdBy(createdBy)
                        .tableName(TABLE_NAME)
                        .idRecord(entity.getId())
                        .build();
                undoLogService.create(undoLog);

                service.replaceHyperLink(entity);

                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
                System.out.println(responseModel);
                return responseModel;
            }
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PutMapping()
    public ResponseModel doUpdate(@RequestParam(value = "fileUpload", required = false) MultipartFile multipartFile, @RequestParam Integer id,
                                  @RequestParam String name, @RequestParam String nameEn,
                                  @RequestParam String description, @RequestParam String descriptionEn,
                                  @RequestParam Integer programId, @RequestParam(required = false) List<UserInfo> user,
                                  @RequestParam(required = false) List<UserInfo> viewers,    @RequestParam(required = false) Integer directoryId,
                                  @RequestParam(required = false) Integer criteriaId,
                                  HttpServletRequest request) {
        final String action = "Update";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);

        try {
            String nameAss = name.trim();
            Assessment entity = service.retrieve(id);

            String createdBy = getUserFromToken(request);

            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .revertObject(g.toJson(entity, Assessment.class))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Cập nhập báo cáo "+entity.getName()+" bởi " + createdBy)
                    .updatedDate(LocalDateTime.now())
                    .createdBy(createdBy)
                    .tableName(TABLE_NAME)
                    .idRecord(entity.getId())
                    .build();

            Assessment checkExisted = null;
            if (!nameAss.equalsIgnoreCase(entity.getName().trim())) {
                checkExisted = service.findByName(nameAss);
            }

            if (checkExisted != null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.NAME_ALREADY_EXIST);
                responseModel.setErrorMessages("Tên báo cáo tự đánh giá đã tồn tại!!!");
                System.out.println(responseModel);
                return responseModel;
            } else {
                java.util.Date date = new java.util.Date();
                entity.setUpdatedDate(date);
                entity.setDelete(ConstantDefine.STATUS.NOT_DELETE);
                entity.setUndoStatus(ConstantDefine.STATUS.CAN_BE_UNDO);
                entity.setName(name);
                entity.setNameEn(nameEn);
                entity.setDescription(description);
                entity.setDescriptionEn(descriptionEn);
                entity.setProgramId(programId);
                entity.setDirectoryId(directoryId);
                entity.setCriteriaId(criteriaId);
                entity.setUser(user);
                entity.setViewers(viewers);
                entity.setUndoStatus(ConstantDefine.STATUS.NOT_Undo);

                entity.setUpdatedBy(createdBy);
                if (multipartFile != null) {
                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                    entity.setFile(fileName);
                    String uploadDir = ConstantDefine.FILE_PATH.ASSESSMENT + "/" + entity.getOrderAss();
                    FileUploadUtil.cleanDir(uploadDir);
                    FileUploadUtil.saveFiles(uploadDir, fileName, multipartFile);
                    if (fileName.contains(".docx")){
                        XWPFDocument document = new XWPFDocument(OPCPackage.open(uploadDir + "/" + fileName));
                        XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                        entity.setContent(extractor.getText());
                    }
                }
                service.update(entity, entity.getId());
                service.replaceHyperLink(entity);

                undoLog.setRequestObject(g.toJson(entity));
                undoLogService.create(undoLog);

                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
                System.out.println(responseModel);
                return responseModel;
            }
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @DeleteMapping("{id}")
    public ResponseModel doDelete(@PathVariable Integer id, HttpServletRequest request) throws Exception {
        final String action = "doDelete";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);

//        try {
//            Assessment assessment = service.retrieve(id);
//            String assessmentDir = ConstantDefine.FILE_PATH.ASSESSMENT + "/" + assessment.getOrderAss();
//            FileUploadUtil.removeDir(assessmentDir);
//            service.delete(id);
//            ResponseModel responseModel = new ResponseModel();
//            responseModel.setStatusCode(HttpStatus.SC_OK);
//            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
//            return responseModel;
//        } catch (Exception e) {
//            throw handleException(e);
//        } finally {
//            log.info(END_LOG, action, sw.getTotalTimeSeconds());
//        }

        try {
            Assessment entity = service.deleteAssessment(id);
                String createdBy = getUserFromToken(request);
            
                UndoLog undoLog = UndoLog.undoLogBuilder()
                        .action(request.getMethod())
                        .requestObject(g.toJson(entity, Assessment.class))
                        .status(ConstantDefine.STATUS.UNDO_NEW)
                        .url(request.getRequestURL().toString())
                        .description("Xóa báo cáo "+entity.getName()+" bởi " + createdBy)
                        .createdBy(createdBy)
                        .createdDate(LocalDateTime.now())
                        .tableName(TABLE_NAME)
                        .idRecord(entity.getId())
                        .build();
                undoLogService.create(undoLog);

                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
                return responseModel;

        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("download/{id}")
    public ResponseEntity<?> downloadFile(@PathVariable("id") Integer id) throws FileNotFoundException {
        Assessment assessment = service.retrieve(id);
        FileDownloadUtil downloadUtil = new FileDownloadUtil();
        Resource resource = null;
        try {
            String path = ConstantDefine.FILE_PATH.ASSESSMENT + "/" + assessment.getOrderAss();
            resource = downloadUtil.getFileAsResource(path, assessment.getFile());
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

//        File file = new File(ConstantDefine.FILE_PATH.ASSESSMENT + "/" + assessment.getOrderAss() + "/" + assessment.getFile());
//        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
//        return ResponseEntity.ok()
//                .contentLength(file.length())
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(resource);
    }

    @GetMapping("wopi/files/{id}/contents")
    public ResponseEntity<?> getFile(@PathVariable("id") Integer id) {
        Assessment assessment = service.retrieve(id);
        FileDownloadUtil downloadUtil = new FileDownloadUtil();
        Resource resource = null;
        try {
            String path = ConstantDefine.FILE_PATH.ASSESSMENT + "/" + assessment.getOrderAss();
            resource = downloadUtil.getFileAsResource(path, assessment.getFile());
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
        Assessment assessment = service.retrieve(id);
        String path = ConstantDefine.FILE_PATH.ASSESSMENT + "/" + assessment.getOrderAss() + "/" + assessment.getFile();
        UserInfo userInfo = userInfoService.findByUsername(getUserFromToken(request));
        List<UserInfo> userInfoList = assessment.getUser();
        FileInfo fileInfo = new FileInfo();
        fileInfo.setBaseFileName(assessment.getFile());
        fileInfo.setUserId(userInfo.getId());
        fileInfo.setSize(FileUploadUtil.getCharCount(path));
        fileInfo.setUserFriendlyName(userInfo.getEmail());
        if (userInfoList.contains(userInfo) || assessment.getCreatedBy().equals(getUserFromToken(request))){
            fileInfo.setUserCanWrite(true);
        } else {
            fileInfo.setUserCanWrite(false);
        }
//        fileInfo.setUserCanWrite(true);
        System.out.println("====================================" + fileInfo.isUserCanWrite() + "============================");
        String contentType = "application/json; charset=utf-8";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.ACCEPT_CHARSET)
                .body(fileInfo);
    }

    @PostMapping("wopi/files/{id}/contents")
    public ResponseEntity<?> saveFile(@PathVariable("id") Integer id, InputStream content, HttpServletRequest request) throws IOException, InvalidFormatException {
        Assessment assessment = service.retrieve(id);
        String path = ConstantDefine.FILE_PATH.ASSESSMENT + "/" + assessment.getOrderAss();
        File file = new File("/app/exhibitionmanagement/assessment/" + assessment.getOrderAss() + "/" + assessment.getFile());
        FileUtils.copyInputStreamToFile(content, file);

        XWPFDocument document = new XWPFDocument(OPCPackage.open(path + "/" + assessment.getFile()));
        XWPFWordExtractor extractor = new XWPFWordExtractor(document);
        assessment.setContent(extractor.getText());
        assessment.setUpdatedBy(getUserFromToken(request));
        service.update(assessment, id);
        service.replaceHyperLink(assessment);

        return ResponseEntity.ok().build();
    }

    @PutMapping("update-experts")
    public ResponseModel doUpdateExperts(@RequestBody EvaluatedDTO dto, HttpServletRequest request){
        final String action = "Update";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            Assessment entity = service.retrieve(dto.getId());
            entity.setEvaluated(dto.getEvaluated());
            entity.setComment(dto.getComment());
            entity.setUpdatedBy(getUserFromToken(request));
            service.update(entity, entity.getId());
            ResponseModel responseModel = new ResponseModel();
            responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
                System.out.println(responseModel);
                return responseModel;
        } catch (Exception e){
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("/proof/wopi/files/{id}/contents")
    public ResponseEntity<?> getFileProof(@PathVariable("id") Integer id) {
        List<ExhibitionFile> exhFileByProofId = exhibitionFileService.getListExhFileByProofId(id);
        FileDownloadUtil downloadUtil = new FileDownloadUtil();
        Resource resource = null;
        try {
            if (exhFileByProofId.size() > 0){
                String path = exhFileByProofId.get(0).getFilePath();
                resource = downloadUtil.getFileAsResource(path, exhFileByProofId.get(0).getFileName());
            }
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

    @GetMapping("/proof/wopi/files/{id}")
    public ResponseEntity<?> getDataFileProof(@PathVariable("id") Integer id) throws IOException {
        List<ExhibitionFile> listExhFileByProofId = exhibitionFileService.getListExhFileByProofId(id);
        FileInfo fileInfo = new FileInfo();
        if (listExhFileByProofId.size() > 0){
            String path = listExhFileByProofId.get(0).getFilePath() + "/" + listExhFileByProofId.get(0).getFileName();
            fileInfo.setBaseFileName(listExhFileByProofId.get(0).getFileName());
            fileInfo.setUserId(1956);
            fileInfo.setSize(FileUploadUtil.getCharCount(path));
              fileInfo.setUserCanWrite(false);
      }
        System.out.println("====================================" + fileInfo.isUserCanWrite() + "============================");
        String contentType = "application/json; charset=utf-8";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.ACCEPT_CHARSET)
                .body(fileInfo);
    }
    @DeleteMapping("delete-multi")
    public ResponseModel deleteMulti(@RequestBody Integer[] assess) {
        final String action = "doDeleteMulti";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            if(service.deleteAssessment(assess, g, getUserFromToken(request), request)){
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
                return responseModel;
            }else{
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.BEING_USED);
                return responseModel;
            }
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("getListAssessmentIdByUsername")
    public ResponseModel getListActiveUnits(HttpServletRequest request) {
        final String action = "getListAssessmentIdByUsername";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            List<Integer> entity=service.getAssessmentByCreatedBy(getUserFromToken(request));
            if (entity == null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy người dùng.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }
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


    @PostMapping("format-obj")
    public ResponseModel formatObj(@RequestBody Assessment entity, HttpServletRequest request) {
        System.out.println(" đã vào");
        final String action = "formatObj";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            AssessmentDTO dto = service.formatObj(entity);
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(dto);
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
