package com.ecommerce.core.controllers;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.*;
import com.ecommerce.core.entities.Document;
import com.ecommerce.core.entities.DocumentFile;
import com.ecommerce.core.entities.UndoLog;
import com.ecommerce.core.entities.Unit;
import com.ecommerce.core.service.*;
import com.ecommerce.core.util.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lowagie.text.DocumentException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
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
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("document")
@Slf4j
public class DocumentController extends BaseController{

    private final String START_LOG = "Document {}";

    private final String END_LOG = "Document {} finished in: {}";
    private final String TABLE_NAME = "document";
    private final Gson g = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    @Autowired
    DocumentService service;

    @Autowired
    UndoLogService undoLogService;

    @Autowired
    DocumentFileService documentFileService;

    @Autowired
    ReadFileService readFileService;

    @Autowired
    ProofService proofService;

    @Autowired
    HttpServletRequest request;

    @GetMapping()
    public ResponseModel doSearch(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "sort", required = false) boolean sortDesc,
                                  @RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int perPage,
                                  HttpServletRequest request){
        final String action = "doSearch";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);

        try {
            Pageable paging = PageRequest.of(currentPage, perPage);
            Page<ListDocumentDTO> pageResult = null;
            if (keyword.equals("")){
                keyword = "";
            } else {
                keyword = keyword.trim().toLowerCase();
            }
            pageResult = service.doSearch(keyword, paging);

            if ((pageResult == null || pageResult.isEmpty())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy văn bản!!!");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }

            PagingResponse<ListDocumentDTO> result = new PagingResponse<>();
            result.setTotal(pageResult.getTotalElements());
            result.setItems(pageResult.getContent());

            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(result);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e){
            throw handleException(e);
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("list-document")
    public ResponseModel getDocument(@PathVariable Integer id){
        final String action = "doRetrieve";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            DetailDocumentDTO dto = new DetailDocumentDTO();
            dto.setEntity(service.getDetailDocument(id));
            dto.setFile(documentFileService.getListFileByDocumentId(id));
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(dto);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e){
        	throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("get-unit-by-creater")
    public ResponseModel getUnitByUsername(HttpServletRequest request){
        final String action = "getUnitByCreater";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);

        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setContent(proofService.getUnitByUsername(getUserFromToken(request)));
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("getIdFileDTO")
    public ResponseModel getFilenameById(@RequestParam(name = "listArr") String list){
        final String action = "getFilenameById";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            System.out.println("LIST " + list);
            String[] listId = list.split(",");
            ResponseModel responseModel = new ResponseModel();
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setContent(service.getListIdAndFilename(listId));
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PostMapping(value = "add", consumes = { MediaType.APPLICATION_JSON_VALUE})
    public ResponseModel doCreate(@RequestBody DocumentDTO dto, HttpServletRequest request) throws ParseException {
        final String action = "doCreate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        Date dateTime = null;
        if(dto.getReleaseDate() != ""){
            dateTime = new SimpleDateFormat("yyyy-MM-dd").parse(dto.getReleaseDate());
        }
        System.out.println("unit " + dto.getUnit());
        List<Unit> likeUnit = dto.getUnit();
//        Document entity = new Document(null, dto.getDocumentNumber(), dto.getDocumentName(), dto.getDocumentNameEn(), dto.getDocumentType(), dateTime, dto.getSigner(), dto.getField(), dto.getReleaseBy(), dto.getDescription(), dto.getDescriptionEn(), getUserFromToken(request), null, null, null, dto.getUnit());
        Document entity = new Document();
        Document saveDocument = null;
        try{
            String dName = dto.getDocumentName().trim();
            Document checkExistedName = service.findByDocumentName(dName);

            if (checkExistedName != null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.NAME_ALREADY_EXIST);
                responseModel.setErrorMessages("Tên văn bản đã tồn tại!!!");
                System.out.println(responseModel);
                return responseModel;
            } else {
                String createdBy = getUserFromToken(request);
                entity.setDocumentName(dto.getDocumentName());
                entity.setDocumentNameEn(dto.getDocumentNameEn());
                entity.setDocumentNumber(dto.getDocumentNumber());
                entity.setDocumentType(dto.getDocumentType());
                entity.setReleaseDate(dateTime);
                entity.setSigner(dto.getSigner());
                entity.setField(dto.getField());
                entity.setReleaseBy(dto.getReleaseBy());
                entity.setDescription(dto.getDescription());
                entity.setDescriptionEn(dto.getDescriptionEn());
                entity.setCreatedBy(createdBy);
                entity.setUnit(likeUnit);
                entity.setUpdatedDate(LocalDateTime.now());
                entity.setDelete(ConstantDefine.STATUS.NOT_DELETE);
                entity.setUndoStatus(ConstantDefine.STATUS.CAN_BE_UNDO);

                saveDocument = service.create(entity);

                UndoLog undoLog = UndoLog.undoLogBuilder()
                        .action(request.getMethod())
                        .requestObject(g.toJson(entity))
                        .status(ConstantDefine.STATUS.UNDO_NEW)
                        .url(request.getRequestURL().toString())
                        .description("Thêm mới văn bản "+entity.getDocumentName()+" bởi " + createdBy)
                        .createdDate(LocalDateTime.now())
                        .createdBy(createdBy)
                        .tableName(TABLE_NAME)
                        .idRecord(entity.getId())
                        .build();
                undoLogService.create(undoLog);
            }

            ResponseModel responseModel = new ResponseModel();
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setContent(saveDocument);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e){
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PostMapping(value ="upload-file", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseModel uploadFile(@RequestParam("attachments")  MultipartFile multipartFile,
                                          @RequestParam(name="documentId") String documentId) {
        final String action = "doCreateFile";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        File file = new File(fileName);
        String uploadDir = "";

        try {
            String content = "";
            String type = readFileService.detectDocTypeUsingDetector(multipartFile.getInputStream());
            if (type.equals("application/pdf")) {
                content = readFileService.readPDF(multipartFile.getInputStream());
            } else{
                content = readFileService.readFile(multipartFile.getInputStream());
            }
            Integer docId = Integer.parseInt(documentId);
            DocumentFile documentFile = new DocumentFile();
            uploadDir = ConstantDefine.FILE_PATH.DOCUMENT + "/" + docId;
            FileUploadUtil.saveFiles(uploadDir, fileName, multipartFile);
            documentFile.setFileName(fileName);
            documentFile.setDocumentId(docId);
            documentFile.setFileType(type);
            documentFile.setFileContent(content);
            documentFile.setFilePath(uploadDir);
            documentFileService.create(documentFile);

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

    @PutMapping(value = "update", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseModel doUpdate(@RequestBody UpdateDocumentDTO dto, HttpServletRequest request) throws ParseException {
        final String action = "Update";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        Date dateTime = null;
        String createdBy = getUserFromToken(request);

        if (dto.getReleaseDate() != "") {
            dateTime = new SimpleDateFormat("yyyy-MM-dd").parse(dto.getReleaseDate());
        } else {
            dateTime = new Date();
        }

        try {
            Document entity = service.retrieve(dto.getId());

            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .revertObject(g.toJson(entity, Document.class))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Câp nhập văn bản "+entity.getDocumentName()+" bởi " + createdBy)
                    .updatedDate(LocalDateTime.now())
                    .createdBy(createdBy)
                    .tableName(TABLE_NAME)
                    .idRecord(entity.getId())
                    .build();

            String dNumber = dto.getDocumentNumber().trim();
            String dName = dto.getDocumentName().trim();
            Document checkExisted = null;
            Document checkExistedName = null;
            if (!dNumber.equalsIgnoreCase(entity.getDocumentNumber().trim())) {
                checkExisted = service.findByDocumentNumber(dNumber);
            }
            if (!dName.equalsIgnoreCase(entity.getDocumentName().trim())) {
                checkExistedName = service.findByDocumentName(dName);
            }

            if (checkExisted != null) {
                if (checkExisted.getDocumentNumber() != null) {
                    ResponseModel responseModel = new ResponseModel();
                    responseModel.setStatusCode(HttpStatus.SC_OK);
                    responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
                    responseModel.setErrorMessages("Số hiệu văn bản đã tồn tại!!!");
                    System.out.println(responseModel);
                    return responseModel;
                }
            } else if (checkExistedName != null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.NAME_ALREADY_EXIST);
                responseModel.setErrorMessages("Tên văn bản đã tồn tại!!!");
                System.out.println(responseModel);
                return responseModel;
            } else {
                entity.setDocumentNumber(dNumber);
                entity.setDocumentName(dName);
                entity.setDocumentNameEn(dto.getDocumentNameEn());
                entity.setDocumentType(dto.getDocumentType());
                entity.setReleaseDate(dateTime);
                entity.setSigner(dto.getSigner());
                entity.setField(dto.getField());
                entity.setUnit(dto.getUnit());
                entity.setDescription(dto.getDescription());
                entity.setDescriptionEn(dto.getDescriptionEn());
                entity.setUpdatedBy(getUserFromToken(request));
                entity.setUpdatedDate(LocalDateTime.now());
                service.update(entity, entity.getId());

                undoLog.setRequestObject(g.toJson(entity, Document.class));
                undoLogService.create(undoLog);
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

    @PostMapping(value ="update-file", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseModel updateFile(@RequestParam("attachments")  MultipartFile multipartFile,
                                    @RequestParam(name="documentId") Integer documentId) {
        final String action = "doUpdateFile";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        File file = new File(fileName);
        String uploadDir = ConstantDefine.FILE_PATH.DOCUMENT + "/" + documentId;

        try {
            String content = "";
            String type = readFileService.detectDocTypeUsingDetector(multipartFile.getInputStream());
            if (type.equals("application/pdf")) {
                content = readFileService.readPDF(multipartFile.getInputStream());
            }else {
                content = readFileService.readFile(multipartFile.getInputStream());
            }
            List<DocumentFile> documentFiles = documentFileService.getListDocumentFileByDocumentId(documentId);
            System.out.println(documentFiles);

            DocumentFile documentFile = new DocumentFile();
            FileUploadUtil.saveFiles(uploadDir, fileName, multipartFile);
            documentFile.setFileType(type);
            documentFile.setFileName(fileName);
            documentFile.setFileContent(content);
            documentFile.setFilePath(uploadDir);
            documentFile.setDocumentId(documentId);
            documentFile = documentFileService.create(documentFile);
            for(int i = 0; i < documentFiles.size(); i++){
                documentFileService.delete(documentFiles.get(i).getId());
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

    @GetMapping("download/{id}")
    public ResponseEntity<?> downloadFile(@PathVariable("id") Integer id) {
        DocumentFile documentFile = documentFileService.findById(id);
        FileDownloadUtil downloadUtil = new FileDownloadUtil();
        Resource resource = null;
        try {
            resource = downloadUtil.getExhFileAsResource(documentFile.getFilePath(), documentFile.getFileName());
        } catch (IOException e) {
            return null;
        }

        if (resource == null) {
            return new ResponseEntity<>("File not found", org.springframework.http.HttpStatus.NOT_FOUND);
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue).body(resource);
    }

    @GetMapping("{id}")
    public ResponseModel doRetrieve(@PathVariable Integer id) {
    	final String action = "doRetrieve";
    	StopWatch sw = new StopWatch();
        Document document = service.retrieve(id);
    	log.info(START_LOG, action);
    	
    	try {
            DetailDocumentDTO dto = new DetailDocumentDTO();
            dto.setEntity(service.getDetailDocument(id));
            dto.setFile(documentFileService.getListFileByDocumentId(id));
            dto.setUnit(document.getUnit());
    		if(dto == null) {
    			ResponseModel responseModel = new ResponseModel();
    			responseModel.setErrorMessages("Không tìm thấy Văn bản!!!");
    			responseModel.setStatusCode(HttpStatus.SC_OK);
    			responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
    			return responseModel;
    		}
    		
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


    @DeleteMapping("{id}")
    public ResponseModel doDelete(@PathVariable Integer id, HttpServletRequest request) {
    	final String action = "doDelete";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            Document entity = service.deleteDocumt(id);
            String createdBy = getUserFromToken(request);

            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .requestObject(g.toJson(entity, Document.class))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Xóa văn bản "+entity.getDocumentName()+" bởi " + createdBy)
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
		} catch (Exception e) {
			throw handleException(e);
		} finally {
			log.info(END_LOG, action, sw.getTotalTimeSeconds());
		}
    }

    @GetMapping("export/excel/{ids}")
    public void exportToExcel(@PathVariable String ids, HttpServletResponse response) throws IOException {
        System.out.println("LIST" + ids);
        List<ListDocumentDTO> document = service.getListDocumentDTO();
        if ("0".equals(ids)){
            DocumentExcelExporter exporter = new DocumentExcelExporter();
            exporter.export(document, response);
        } else {
            List<ListDocumentDTO> documentSelected = new ArrayList<>();
            String[] listId = ids.split(",");
            for (String s : listId) {
                for (ListDocumentDTO listDocumentDTO : document) {
                    if (listDocumentDTO.getId() == Integer.parseInt(s)) {
                        documentSelected.add(listDocumentDTO);
                    }
                }
            }
            DocumentExcelExporter exporter = new DocumentExcelExporter();
            exporter.export(documentSelected, response);
        }

    }

    @GetMapping("export/excel-en/{ids}")
    public void exportToExcelEn(@PathVariable String ids, HttpServletResponse response) throws IOException {
        List<ListDocumentDTO> document = service.getListDocumentDTO();
        if ("0".equals(ids)){
            DocumentExcelExporter exporter = new DocumentExcelExporter();
            exporter.exportEn(document, response);
        } else {
            List<ListDocumentDTO> documentSelected = new ArrayList<>();
            String[] listId = ids.split(",");
            for (String s : listId) {
                for (ListDocumentDTO listDocumentDTO : document) {
                    if (listDocumentDTO.getId() == Integer.parseInt(s)) {
                        documentSelected.add(listDocumentDTO);
                    }
                }
            }
            DocumentExcelExporter exporter = new DocumentExcelExporter();
            exporter.exportEn(documentSelected, response);
        }

    }

//    @GetMapping("export/excel-en")
//    public void exportToExcelEn(HttpServletResponse response) throws IOException {
//        List<ListDocumentDTO> document = service.getListDocumentDTO();
//        DocumentExcelExporter exporter = new DocumentExcelExporter();
//        exporter.exportEn(document, response);
//    }

    @GetMapping("export/pdf")
    public void exportToPDF(HttpServletResponse response) throws IOException, DocumentException {
        List<ListDocumentDTO> list = service.getListDocumentDTO();
        DocumentPDFExporter exporter = new DocumentPDFExporter();
        exporter.export(list, response);
    }

    @GetMapping("export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<Document> documentList = service.getDocument();
        DocumentCsvExporter exporter = new DocumentCsvExporter();
        exporter.export(documentList, response);
    }

    @DeleteMapping("delete-multi")
    public ResponseModel deleteMulti(@RequestBody Integer[] document) {
        final String action = "doDeleteMulti";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            if(service.deleteDocumt(document, g, getUserFromToken(request), request)){
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
}
