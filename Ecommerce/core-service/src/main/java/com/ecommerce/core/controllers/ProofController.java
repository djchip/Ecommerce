package com.ecommerce.core.controllers;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.*;
import com.ecommerce.core.entities.*;
import com.ecommerce.core.exceptions.*;
import com.ecommerce.core.service.*;
import com.ecommerce.core.service.impl.Export_Import.ProofExcelExport;
import com.ecommerce.core.util.FileDownloadUtil;
import com.ecommerce.core.util.FileUploadUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("proof")
public class ProofController extends BaseController {

    private final String START_LOG = "Proof {}";
    private final String END_LOG = "Proof {} finished in: {}";
    private final String TABLE_NAME = "proof";
    public static final Gson g = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private String listSta = null;
    private String listCri = null;
    private int programId = 0;
    private String proofCode;

    @Autowired
    ProofService service;
    @Autowired
    ReadFileService readFileService;

    @Autowired
    StandardCriteriaService standardCriteriaService;

    @Autowired
    RolePrivilegesService rolePrivilegesService;
    @Autowired
    ExhibitionFileService exhibitionFileService;
    @Autowired
    DirectoryService directoryService;
    @Autowired
    CriteriaService criteriaService;
    @Autowired
    ProofExhFileService proofExhFileService;
    @Autowired
    ExhibitionCollectionService collectionService;
    @Autowired
    ProofExhFileService pefService;

    @Autowired
    AppParamService documentFieldService;
    @Autowired
    ProgramsService programsService;
    @Autowired
    UndoLogService undoLogService;

    @Autowired
    DocumentService documentService;

    @Autowired
    FormService formService;

    @Autowired
    ReportService reportService;
    @Autowired
    DocumentFileService documentFileService;

    @Autowired
    private ProgramUserSevice programUserSevice;

    @Autowired
    private StandardUserService standardUserService;

    @Autowired
    private CriteriaUserService criteriaUserService;

    @Autowired
    private RolesService rolesService;

    @GetMapping()
    public ResponseModel doSearch(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "programId", required = false) Integer programId,
            @RequestParam(value = "standardId", required = false) Integer standardId,
            @RequestParam(value = "criteriaId", required = false) Integer criteriaId,
            @RequestParam(value = "sort", required = false) boolean sortDesc,
            @RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int perPage,
            HttpServletRequest request) {
        final String action = "doSearch";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            String username = getUserFromToken(request);
            List<String> listRoleCodeByUsername = rolesService.getListRolesCodeByUsername(username);

            Pageable pageable = PageRequest.of(currentPage, perPage);
            Page<ProofDTO> pageResult = null;
            if (keyword.trim().equals("")) {
                if(listRoleCodeByUsername.contains("ADMIN") || listRoleCodeByUsername.contains("Super Admin")){
                    pageResult = service.doSearch(programId, standardId, criteriaId, null, pageable);
                } else {
                    pageResult = service.doSearch(programId, standardId, criteriaId, username, pageable);
                }
            } else {
                keyword = "\"" + keyword.trim() + "\"";
                if(listRoleCodeByUsername.contains("ADMIN") || listRoleCodeByUsername.contains("Super Admin")){
                    pageResult = service.doSearchContent(programId, standardId, criteriaId, keyword, null, pageable);
                } else {
                    pageResult = service.doSearchContent(programId, standardId, criteriaId, keyword, username, pageable);
                }
            }
            if ((pageResult == null || pageResult.isEmpty())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy minh chứng!!!");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }
            PagingResponse<ProofDTO> result = new PagingResponse<>();
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

    @PostMapping(value = "upload-file-search-web")
    public ResponseModel uploadFileSearchWeb(@RequestParam(name = "proofCode", required = false) String proofCode,
                                             @RequestParam(name = "fileNumber", required = false) String fileNumber,
                                             @RequestParam(name = "filePathSearchWeb", required = false) String filePathSearchWeb,
                                             @RequestParam(name = "fileContentSearchWeb", required = false) String fileContentSearchWeb,
                                             @RequestParam(name = "typeOfFileSearchWeb", required = false) String typeOfFileSearchWeb,
                                             @RequestParam(name = "proofId", required = false) String proofId) {
        final String action = "uploadFileSearchWeb";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        String uploadDir = "";
        try {
            String[] lstObject = proofCode.split(";");
            String[] lstId = proofId.split(",");
            if (fileNumber.trim().equals("1")) {
                uploadDir = ConstantDefine.FILE_PATH.EXHIBITION + "/" + programId;
                for (String value : lstObject) {
                    String[] lstCode = value.split(",");
                    String fileNameSearchWeb = lstCode[2] + typeOfFileSearchWeb;
                    String destDir = ConstantDefine.FILE_PATH.EXHIBITION + "/" + programId + "/" + fileNameSearchWeb;
                    if (filePathSearchWeb != null) {
                        File original = new File(filePathSearchWeb);
                        File copied = new File(destDir);
                        FileUtils.copyFile(original, copied);
                        ExhibitionFile exhibitionFile = new ExhibitionFile();
                        exhibitionFile.setFileName(fileNameSearchWeb);
                        exhibitionFile.setFileContent(fileContentSearchWeb);
                        exhibitionFile.setFilePath(uploadDir);
                        exhibitionFile = exhibitionFileService.create(exhibitionFile);
                        for (String s : lstId) {
                            if (service.findById(Integer.parseInt(s)).getProofCode().equals(lstCode[2])) {
                                ProofExhFile proofExhFile = new ProofExhFile();
                                proofExhFile.setProofId(Integer.valueOf(s));
                                proofExhFile.setExhFileId(exhibitionFile.getId());
                                proofExhFileService.create(proofExhFile);
                            }
                        }
                    }
                }
            } else {
                for (String s : lstObject) {
                    String[] lstCode = s.split(",");
                    uploadDir = ConstantDefine.FILE_PATH.EXHIBITION + "/" + programId + "/" + lstCode[2];
                    String fileNameSearchWeb = lstCode[2] + typeOfFileSearchWeb;
                    String destDir = uploadDir + "/" + fileNameSearchWeb;
                    if (filePathSearchWeb != null) {
                        File original = new File(filePathSearchWeb);
                        File copied = new File(destDir);
                        FileUtils.copyFile(original, copied);
                        ExhibitionFile exhibitionFile = new ExhibitionFile();
                        exhibitionFile.setFileName(fileNameSearchWeb);
                        exhibitionFile.setFileContent(fileContentSearchWeb);
                        exhibitionFile.setFilePath(uploadDir);
                        exhibitionFile = exhibitionFileService.create(exhibitionFile);
                        for (String value : lstId) {
                            if (service.findById(Integer.parseInt(value)).getProofCode().equals(lstCode[2])) {
                                ProofExhFile proofExhFile = new ProofExhFile();
                                proofExhFile.setProofId(Integer.valueOf(value));
                                proofExhFile.setExhFileId(exhibitionFile.getId());
                                proofExhFileService.create(proofExhFile);
                            }
                        }
                    }
                }
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


    @PostMapping(value = "upload-single-file", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseModel uploadSingleFile(@RequestParam("attachments") MultipartFile multipartFile,
                                          @RequestParam(name = "proofId") String proofId,
                                          @RequestParam(name = "fileNumber") String fileNumber,
                                          @RequestParam(name = "proofCode") String proofCode) {
        final String action = "uploadSingleFile";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        String uploadDir = "";
        ExhibitionFile exhFile = new ExhibitionFile();
        try {
            String content;
            String type = readFileService.detectDocTypeUsingDetector(multipartFile.getInputStream());
            System.out.println("_____________________________________ type ____________________________________________" + type);
            if (type.equals("application/pdf")) {
                content = readFileService.readPDF(multipartFile.getInputStream());
            } else if (type.contains("doc")) {
//      } else if(type.contains("application/vnd.openxmlformats-officedocument.wordprocessingml.document")){
                BodyContentHandler handler = new BodyContentHandler();
                ParseContext pcontext = new ParseContext();
                OOXMLParser msofficeparser = new OOXMLParser();
                Metadata metadata = new Metadata();
                msofficeparser.parse(multipartFile.getInputStream(), handler, metadata, pcontext);
                content = handler.toString();
            } else {
                content = readFileService.readFile(multipartFile.getInputStream());
            }
            String[] lstObject = proofCode.split(";");
            if (fileNumber.trim().equals("1")) {
                uploadDir = ConstantDefine.FILE_PATH.EXHIBITION + "/" + programId;
                String[] lstId = proofId.split(",");
                FileUploadUtil.saveFiles(uploadDir, fileName, multipartFile);
                exhFile.setFileName(fileName);
                exhFile.setFileContent(content);
                exhFile.setFilePath(uploadDir);
                exhFile = exhibitionFileService.create(exhFile);
                for (int j = 0; j < lstId.length; j++) {
                    ProofExhFile proofExhFile = new ProofExhFile();
                    proofExhFile.setProofId(Integer.valueOf(lstId[j]));
                    proofExhFile.setExhFileId(exhFile.getId());
                    proofExhFileService.create(proofExhFile);
                }
            } else {
                for (int i = 0; i < lstObject.length; i++) {
                    String[] lstCode = lstObject[i].split(",");
                    uploadDir = ConstantDefine.FILE_PATH.EXHIBITION + "/" + programId + "/" + lstCode[2];
                    FileUploadUtil.saveFiles(uploadDir, fileName, multipartFile);
                    exhFile.setFileName(fileName);
                    exhFile.setFileContent(content);
                    exhFile.setFilePath(uploadDir);
                    exhFile = exhibitionFileService.create(exhFile);
                }
                String[] lstId = proofId.split(",");
                for (int j = 0; j < lstId.length; j++) {
                    ProofExhFile proofExhFile = new ProofExhFile();
                    proofExhFile.setProofId(Integer.valueOf(lstId[j]));
                    proofExhFile.setExhFileId(exhFile.getId());
                    proofExhFileService.create(proofExhFile);
                }
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

    @PostMapping(value = "upload-single-file-no-content", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseModel uploadSingleFileWithoutContent(@RequestParam("attachments") MultipartFile multipartFile,
                                                        @RequestParam(name = "proofId") String proofId,
                                                        @RequestParam(name = "fileNumber") String fileNumber,
                                                        @RequestParam(name = "proofCode") String proofCode) {
        final String action = "doCreateFile";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        File file = new File(fileName);
        String uploadDir = "";
        ExhibitionFile exhFile = new ExhibitionFile();
        try {
            String content = "";
            String type = readFileService.detectDocTypeUsingDetector(multipartFile.getInputStream());
            String[] lstObject = proofCode.split(";");
            if (fileNumber.trim().equals("1")) {
                uploadDir = ConstantDefine.FILE_PATH.EXHIBITION + "/" + programId;
                String[] lstId = proofId.split(",");
                FileUploadUtil.saveFiles(uploadDir, fileName, multipartFile);
                exhFile.setFileName(fileName);
                exhFile.setFileContent(content);
                exhFile.setFilePath(uploadDir);
                exhFile = exhibitionFileService.create(exhFile);
                for (int j = 0; j < lstId.length; j++) {
                    ProofExhFile proofExhFile = new ProofExhFile();
                    proofExhFile.setProofId(Integer.valueOf(lstId[j]));
                    proofExhFile.setExhFileId(exhFile.getId());
                    proofExhFileService.create(proofExhFile);
                }
            } else {
                for (int i = 0; i < lstObject.length; i++) {
                    String[] lstCode = lstObject[i].split(",");
                    uploadDir = ConstantDefine.FILE_PATH.EXHIBITION + "/" + programId + "/" + lstCode[2];
                    FileUploadUtil.saveFiles(uploadDir, fileName, multipartFile);
                    exhFile.setFileName(fileName);
                    exhFile.setFileContent(content);
                    exhFile.setFilePath(uploadDir);
                    exhFile = exhibitionFileService.create(exhFile);
                }
                String[] lstId = proofId.split(",");
                for (int j = 0; j < lstId.length; j++) {
                    ProofExhFile proofExhFile = new ProofExhFile();
                    proofExhFile.setProofId(Integer.valueOf(lstId[j]));
                    proofExhFile.setExhFileId(exhFile.getId());
                    proofExhFileService.create(proofExhFile);
                }
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

//    @PostMapping(value = "upload-single-file-no-content", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
//    public ResponseModel uploadSingleFileWithoutContent(@RequestParam("attachments") MultipartFile multipartFile,
//                                          @RequestParam(name = "proofId") String proofId,
//                                          @RequestParam(name = "fileNumber") String fileNumber,
//                                          @RequestParam(name = "proofCode") String proofCode) {
//        final String action = "uploadSingleFile";
//        StopWatch sw = new StopWatch();
//        log.info(START_LOG, action);
//        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
//        String uploadDir = "";
//        ExhibitionFile exhFile = new ExhibitionFile();
//        try {
//            String content = "";
////            String type = readFileService.detectDocTypeUsingDetector(multipartFile.getInputStream());
////            System.out.println("_____________________________________ type ____________________________________________" + type);
////            if (type.equals("application/pdf")) {
////                content = readFileService.readPDF(multipartFile.getInputStream());
////            } else if (type.contains("doc")) {
//////			} else if(type.contains("application/vnd.openxmlformats-officedocument.wordprocessingml.document")){
////                BodyContentHandler handler = new BodyContentHandler();
////                ParseContext pcontext = new ParseContext();
////                OOXMLParser msofficeparser = new OOXMLParser();
////                Metadata metadata = new Metadata();
////                msofficeparser.parse(multipartFile.getInputStream(), handler, metadata, pcontext);
////                content = handler.toString();
////            } else {
////                content = readFileService.readFile(multipartFile.getInputStream());
////            }
//            String[] lstObject = proofCode.split(";");
//            if (fileNumber.trim().equals("1")) {
//                uploadDir = ConstantDefine.FILE_PATH.EXHIBITION + "/" + programId;
//                String[] lstId = proofId.split(",");
//                FileUploadUtil.saveFiles(uploadDir, fileName, multipartFile);
//                exhFile.setFileName(fileName);
//                exhFile.setFileContent(content);
//                exhFile.setFilePath(uploadDir);
//                exhFile = exhibitionFileService.create(exhFile);
//                for (int j = 0; j < lstId.length; j++) {
//                    ProofExhFile proofExhFile = new ProofExhFile();
//                    proofExhFile.setProofId(Integer.valueOf(lstId[j]));
//                    proofExhFile.setExhFileId(exhFile.getId());
//                    proofExhFileService.create(proofExhFile);
//                }
//            } else {
//                for (int i = 0; i < lstObject.length; i++) {
//                    String[] lstCode = lstObject[i].split(",");
//                    uploadDir = ConstantDefine.FILE_PATH.EXHIBITION + "/" + programId + "/" + lstCode[2];
//                    FileUploadUtil.saveFiles(uploadDir, fileName, multipartFile);
//                    exhFile.setFileName(fileName);
//                    exhFile.setFileContent(content);
//                    exhFile.setFilePath(uploadDir);
//                    exhFile = exhibitionFileService.create(exhFile);
//                }
//                String[] lstId = proofId.split(",");
//                for (int j = 0; j < lstId.length; j++) {
//                    ProofExhFile proofExhFile = new ProofExhFile();
//                    proofExhFile.setProofId(Integer.valueOf(lstId[j]));
//                    proofExhFile.setExhFileId(exhFile.getId());
//                    proofExhFileService.create(proofExhFile);
//                }
//            }
//            ResponseModel responseModel = new ResponseModel();
//            responseModel.setStatusCode(HttpStatus.SC_OK);
//            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
//            return responseModel;
//        } catch (Exception e) {
//            throw handleException(e);
//        } finally {
//            log.info(END_LOG, action, sw.getTotalTimeSeconds());
//        }
//    }

    @PostMapping(value = "getListStaAndListCriSelected")
    public ResponseModel getListStaAndListCriSelected(@RequestBody(required = false) StaCriDTO dto) {
        final String action = "doGet";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            listSta = dto.getStandard();
            listCri = dto.getCriteria();
            System.out.println("MOT" + listSta);
            ResponseModel responseModel = new ResponseModel();
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("get-list-cri-by-staId")
    public ResponseModel getListCriteriaBySta(@RequestParam(value = "standardId", required = false) Integer staId) {
        final String action = "getListCriteriaBySta";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(criteriaService.getListCriteriaBySta(staId));
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("get-file-name-by-proofId")
    public ResponseModel getFileNameByProofId(@RequestParam(value = "proofId", required = false) Integer id) {
        final String action = "getFileNameByProofId";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(exhibitionFileService.getListExhFileByProofId(id));
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PostMapping(value = "copy", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseModel copyProof(@RequestBody CopyProofDTO dto, HttpServletRequest request) {
        final String action = "docopyProof";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
//            LocalDateTime dateTime = null;
//            if (exhDTO.getReleaseDate() != "") {
//                dateTime = LocalDate.parse(dto.getReleaseDate(), formatter).atStartOfDay();
//            }
            String[] lstObject = dto.getProofCode().split(";");
            List<Proof> rs = new ArrayList<>();
            Proof oldProof = service.retrieve(dto.getProofId());
            List<Unit> likedUnit = new ArrayList<>();
            likedUnit.addAll(oldProof.getUnit());
            List<ExhibitionFile> likedExhFile = new ArrayList<>();
            likedExhFile.addAll(oldProof.getListExhFile());
            for (int i = 0; i < lstObject.length; i++) {
                if (!lstObject[i].equals("")) {
                    Proof entity = new Proof();

                    String[] lstCode = lstObject[i].split(",");
                    Integer standardId = Integer.parseInt(lstCode[0]);
                    Integer criteriaId = Integer.parseInt(lstCode[1]);
                    String exhCode = lstCode[2];

                    Proof checkExisted = service.findByProofCode(exhCode);
                    if (checkExisted == null) {
                        Integer maxOrderOfStand = 0;
                        Integer maxOrderOfCri = 0;
                        Date date = new Date();
                        entity.setProofName(oldProof.getProofName());
                        entity.setProofNameEn(oldProof.getProofNameEn());
                        entity.setProofCode(exhCode);
                        entity.setOldProofCode(dto.getProofCode());
                        entity.setOldProofCode(exhCode);
                        entity.setStandardId(standardId);
                        entity.setCriteriaId(criteriaId);
                        entity.setDocumentType(oldProof.getDocumentType());
                        entity.setNumberSign(oldProof.getNumberSign());
                        entity.setReleaseDate(oldProof.getReleaseDate());
                        entity.setSigner(oldProof.getSigner());
                        entity.setField(oldProof.getField());
                        entity.setUploadedDate(LocalDateTime.now());
                        entity.setReleaseBy(oldProof.getReleaseBy());
                        entity.setDescription(oldProof.getDescription());
                        entity.setDescriptionEn(oldProof.getDescriptionEn());
                        entity.setNote(oldProof.getNote());
                        entity.setNoteEn(oldProof.getNoteEn());
                        entity.setUnit(likedUnit);
                        entity.setCreatedDate(date);
                        entity.setUpdatedDate(date);
                        entity.setListExhFile(likedExhFile);
                        entity.setExhFile(oldProof.getExhFile());
                        entity.setCreatedBy(getUserFromToken(request));
                        entity.setProgramId(dto.getProgramId());
                        entity.setStatus(0);
                        entity.setDeleted(ConstantDefine.STATUS.NOT_DELETE);

                        if (service.getMaxOrderOfStandard(standardId) != null) {
                            maxOrderOfStand = service.getMaxOrderOfStandard(standardId);

                        }
                        if (service.getMaxOrderOfCriteria(criteriaId) != null) {
                            maxOrderOfCri = service.getMaxOrderOfCriteria(criteriaId);

                        }
                        if (criteriaId != 0) {
                            entity.setOrderOfCriteria(maxOrderOfCri + 1);
                        }
                        entity.setOrderOfStandard(maxOrderOfStand + 1);
                        service.create(entity);
                        rs.add(entity);
                        service.updateExhFile(exhibitionFileService.getLatestId() + 1, entity.getId());
                        String createdBy = getUserFromToken(request);

                        UndoLog undoLog = UndoLog.undoLogBuilder()
                                .action(request.getMethod())
                                .requestObject(g.toJson(entity, Proof.class))
                                .status(ConstantDefine.STATUS.UNDO_NEW)
                                .url(request.getRequestURL().toString())
                                .description("Coppy minh chứng bởi " + createdBy)
                                .createdDate(LocalDateTime.now())
                                .createdBy(createdBy)
                                .tableName(TABLE_NAME)
                                .idRecord(entity.getId())
                                .build();
                        undoLogService.create(undoLog);


                    } else {
                        ResponseModel responseModel = new ResponseModel();
                        responseModel.setStatusCode(HttpStatus.SC_OK);
                        responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
                        responseModel.setErrorMessages("Mã minh chứng đã tồn tại!!!");
                        return responseModel;
                    }
                }
            }

            ResponseModel responseModel = new ResponseModel();
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setContent(rs);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PostMapping(value = "copy-document-to-proof", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseModel copyDocumentToProof(@RequestBody CopyDocumentToProofDTO dto, HttpServletRequest request) {
        final String action = "doCreate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            List<ExhibitionFile> likedExhFile = new ArrayList<>();
            String path = ConstantDefine.FILE_PATH.EXHIBITION + "/" + dto.getProgramId();
            List<DocumentFile> documentFiles = documentFileService.getListFileByDocumentId(dto.getId());
            System.out.println("id doc" + dto.getId());
            for (DocumentFile documentFile : documentFiles) {
                ExhibitionFile exhibitionFile = new ExhibitionFile();
                exhibitionFile.setFileContent(documentFile.getFileContent());
                exhibitionFile.setFileName(documentFile.getFileName());
                exhibitionFile.setFilePath(path);
                exhibitionFileService.create(exhibitionFile);
                likedExhFile.add(exhibitionFile);
            }
            List<Unit> likedUnit = dto.getUnit();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime dateTime = null;
            if (dto.getReleaseDate() != "") {
                dateTime = LocalDate.parse(dto.getReleaseDate(), formatter).atStartOfDay();
            }

            // Save minh chứng cho tiêu chuẩn
            List<Proof> rs = new ArrayList<>();
            String[] lstObject = dto.getProofCode().split(";");
            for (int i = 0; i < lstObject.length; i++) {
                if (!lstObject[i].equals("")) {
                    String[] lstCode = lstObject[i].split(",");
                    Integer standardId = Integer.parseInt(lstCode[0]);
                    Integer criteriaId = Integer.parseInt(lstCode[1]);
                    String exhCode = lstCode[2];
                    Proof checkExisted = service.findByProofCode(exhCode);
                    if (checkExisted == null) {
                        Integer maxOrderOfStand = 0;
                        Integer maxOrderOfCri = 0;
                        String createdBy = getUserFromToken(request);
                        java.util.Date date = new java.util.Date();
                        Proof entity = new Proof();
                        entity.setProofName(dto.getProofName());
                        entity.setProofNameEn(dto.getProofNameEn());
                        entity.setProofCode(exhCode);
                        entity.setOldProofCode(exhCode);
                        entity.setStandardId(standardId);
                        entity.setCriteriaId(criteriaId);
                        entity.setDocumentType(dto.getDocumentType());
                        entity.setNumberSign(dto.getNumberSign());
                        entity.setReleaseDate(dateTime);
                        entity.setSigner(dto.getSigner());
                        entity.setField(dto.getField());
                        entity.setCreatedDate(date);
                        entity.setUpdatedDate(date);
                        entity.setUploadedDate(LocalDateTime.now());
                        entity.setReleaseBy(dto.getReleaseBy());
                        entity.setDescription(dto.getDescription());
                        entity.setDescriptionEn(dto.getDescriptionEn());
                        entity.setNote(dto.getNote());
                        entity.setNoteEn(dto.getNoteEn());
                        entity.setUnit(likedUnit);
                        entity.setListExhFile(likedExhFile);
//					entity.setExhFile(exhDTO.getExhFile());
                        entity.setCreatedBy(getUserFromToken(request));
                        entity.setProgramId(dto.getProgramId());
                        entity.setStatus(0);
                        entity.setDeleted(ConstantDefine.STATUS.NOT_DELETE);
                        if (service.getMaxOrderOfStandard(standardId) != null) {
                            maxOrderOfStand = service.getMaxOrderOfStandard(standardId);
                        }
                        if (service.getMaxOrderOfCriteria(criteriaId) != null) {
                            maxOrderOfCri = service.getMaxOrderOfCriteria(criteriaId);
                        }
                        if (criteriaId != 0) {
                            entity.setOrderOfCriteria(maxOrderOfCri + 1);
                        }
                        entity.setOrderOfStandard(maxOrderOfStand + 1);
                        service.create(entity);
                        rs.add(entity);
                        service.updateExhFile(exhibitionFileService.getLatestId() + 1, entity.getId());

//                        String createdBy = getUserFromToken(request);

                        UndoLog undoLog = UndoLog.undoLogBuilder()
                                .action(request.getMethod())
                                .requestObject(g.toJson(entity, Proof.class))
                                .status(ConstantDefine.STATUS.UNDO_NEW)
                                .url(request.getRequestURL().toString())
                                .description("Coppy minh chứng từ văn bản bởi " + createdBy)
                                .createdDate(LocalDateTime.now())
                                .createdBy(createdBy)
                                .tableName(TABLE_NAME)
                                .idRecord(entity.getId())
                                .build();
                        undoLogService.create(undoLog);

                    } else {
                        ResponseModel responseModel = new ResponseModel();
                        responseModel.setStatusCode(HttpStatus.SC_OK);
                        responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
                        responseModel.setErrorMessages("Mã minh chứng đã tồn tại!!!");
                        return responseModel;
                    }
                }
            }
            ResponseModel responseModel = new ResponseModel();
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setContent(rs);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }


    @PostMapping(value = "add", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseModel doCreate(@RequestBody ExhDTO exhDTO, HttpServletRequest request)
            throws ParseException, IOException, TikaException {
        final String action = "doCreate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            List<Unit> likedUnit = exhDTO.getUnit();
            List<ExhibitionFile> likedExhFile = exhDTO.getListExhFile();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime dateTime = null;
            if (exhDTO.getReleaseDate() != "") {
                dateTime = LocalDate.parse(exhDTO.getReleaseDate(), formatter).atStartOfDay();
            }
            // Save minh chứng cho tiêu chuẩn
            List<Proof> rs = new ArrayList<>();
            String[] lstObject = exhDTO.getProofCode().split(";");
            for (int i = 0; i < lstObject.length; i++) {
                if (!lstObject[i].equals("")) {
                    String[] lstCode = lstObject[i].split(",");
                    System.out.println("LST CODE \n" + Arrays.toString(lstCode));
                    Integer criteriaId = Integer.parseInt(lstCode[1]);
                    Integer standardId = Integer.parseInt(lstCode[0]);
                    String exhCode = lstCode[2];

                    Proof checkExisted = service.findByProofCode(exhCode);
                    if (checkExisted == null) {
                        Integer maxOrderOfStand = 0;
                        Integer maxOrderOfCri = 0;
                        String createdBy = getUserFromToken(request);
                        java.util.Date date = new java.util.Date();
                        Proof entity = new Proof();
                        entity.setProofName(exhDTO.getProofName());
                        entity.setProofNameEn(exhDTO.getProofNameEn());
                        entity.setProofCode(exhCode);
                        entity.setOldProofCode(exhCode);
                        entity.setStandardId(standardId);
                        entity.setCriteriaId(criteriaId);
                        entity.setDocumentType(exhDTO.getDocumentType());
                        entity.setNumberSign(exhDTO.getNumberSign());
                        entity.setReleaseDate(dateTime);
                        entity.setSigner(exhDTO.getSigner());
                        entity.setField(exhDTO.getField());
                        entity.setUploadedDate(LocalDateTime.now());
                        entity.setReleaseBy(exhDTO.getReleaseBy());
                        entity.setDescription(exhDTO.getDescription());
                        entity.setDescriptionEn(exhDTO.getDescriptionEn());
                        entity.setNote(exhDTO.getNote());
                        entity.setNoteEn(exhDTO.getNoteEn());
                        entity.setUnit(likedUnit);
                        entity.setCreatedDate(date);
                        entity.setUpdatedDate(date);
                        entity.setListExhFile(likedExhFile);
                        entity.setExhFile(exhDTO.getExhFile());
                        entity.setCreatedBy(getUserFromToken(request));
                        entity.setProgramId(exhDTO.getProgramId());
                        entity.setStatus(0);
                        entity.setDeleted(ConstantDefine.STATUS.NOT_DELETE);
                        entity.setUndoStatus(ConstantDefine.STATUS.CAN_BE_UNDO);
                        entity.setCreatedDate(new Date());
                        programId = exhDTO.getProgramId();

                        proofCode = exhCode;
                        if (service.getMaxOrderOfStandard(standardId) != null) {
                            maxOrderOfStand = service.getMaxOrderOfStandard(standardId);
                        }
                        if (service.getMaxOrderOfCriteria(criteriaId) != null) {
                            maxOrderOfCri = service.getMaxOrderOfCriteria(criteriaId);
                        }
                        if (criteriaId != 0) {
                            entity.setOrderOfCriteria(maxOrderOfCri + 1);
                        }
                        entity.setOrderOfStandard(maxOrderOfStand + 1);
                        service.create(entity);
                        rs.add(entity);
                        service.updateExhFile(exhibitionFileService.getLatestId() + 1, entity.getId());
                        UndoLog undoLog = UndoLog.undoLogBuilder()
                                .action(request.getMethod())
                                .requestObject(g.toJson(entity, Proof.class))
                                .status(ConstantDefine.STATUS.UNDO_NEW)
                                .url(request.getRequestURL().toString())
                                .description("Thêm mới minh chứng " + entity.getProofName() + " bởi " + createdBy)
                                .createdDate(LocalDateTime.now())
                                .createdBy(createdBy)
                                .tableName(TABLE_NAME)
                                .idRecord(entity.getId())
                                .build();
                        undoLogService.create(undoLog);
                    } else {
                        ResponseModel responseModel = new ResponseModel();
                        responseModel.setStatusCode(HttpStatus.SC_OK);
                        responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
                        responseModel.setErrorMessages("Mã minh chứng đã tồn tại!!!");
                        return responseModel;
                    }
                }
            }
            ResponseModel responseModel = new ResponseModel();
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setContent(rs);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping({"gen-code-sta"})
    public ResponseModel getAutoCodeSta() {
        final String action = "doAutoCode";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            List<String> rs = new ArrayList<>();
            String[] listIdSta = null;
            if (listSta != null) {
                listIdSta = listSta.split(",");
                for (int i = 0; i < listIdSta.length; i++) {
                    Integer maxOrderStandard = 0;
                    if (service.getMaxOrderOfStandard(Integer.valueOf(listIdSta[i])) != null) {
                        maxOrderStandard = service.getMaxOrderOfStandard(Integer.valueOf(listIdSta[i]));
                    }
                    System.out.println("max" + maxOrderStandard);
                    Integer autoCodeSta = maxOrderStandard + 1;
                    String setCodeForSta = "0";
                    if (autoCodeSta < 10) {
                        setCodeForSta += autoCodeSta;
                    } else {
                        setCodeForSta = String.valueOf(autoCodeSta);
                    }
                    rs.add(setCodeForSta);
                }
            }
            responseModel.setContent(rs);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping({"gen-code-cri"})
    public ResponseModel getAutoCodeCri() {
        final String action = "doAutoCode";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            List<String> rs = new ArrayList<>();
            String[] listIdCri = null;
            if (listCri != null) {
                listIdCri = listCri.split(",");
                for (int i = 0; i < listIdCri.length; i++) {
                    Integer maxOrderCriteria = 0;
                    if (service.getMaxOrderOfCriteria(Integer.valueOf(listIdCri[i])) != null) {
                        maxOrderCriteria = service.getMaxOrderOfCriteria(Integer.valueOf(listIdCri[i]));
                    }
                    System.out.println("max" + maxOrderCriteria);
                    Integer autoCodeCri = maxOrderCriteria + 1;
                    String setCodeForCri = "0";
                    if (autoCodeCri < 10) {
                        setCodeForCri += autoCodeCri;
                    } else {
                        setCodeForCri = String.valueOf(autoCodeCri);
                    }
                    rs.add(setCodeForCri);
                }
            }
            responseModel.setContent(rs);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;


        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PutMapping(value = "update", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseModel doUpdate(@RequestBody ExhDTO exhDTO, HttpServletRequest request)
            throws ParseException, IOException, TikaException {
        final String action = "doUpdate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        LocalDateTime dateTime = null;
        Date date1 = null;
        if (exhDTO.getReleaseDate() != "") {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            dateTime = LocalDate.parse(exhDTO.getReleaseDate(), formatter).atStartOfDay();

//            date1 = new SimpleDateFormat("yyyy-MM-dd").parse(exhDTO.getReleaseDate());
        }
        try {
            Proof entity = service.retrieve(exhDTO.getId());
            String createdBy = getUserFromToken(request);
            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .revertObject(g.toJson(entity, Proof.class))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Cập nhập minh chứng " + entity.getProofName() + " bởi " + createdBy)
                    .updatedDate(LocalDateTime.now())
                    .createdBy(createdBy)
                    .tableName(TABLE_NAME)
                    .idRecord(entity.getId())
                    .build();

            entity.setProofName(exhDTO.getProofName());
            entity.setProofNameEn(exhDTO.getProofNameEn());
            entity.setDocumentType(exhDTO.getDocumentType());
            entity.setNumberSign(exhDTO.getNumberSign());
            entity.setReleaseDate(dateTime);
            entity.setSigner(exhDTO.getSigner());
            entity.setField(exhDTO.getField());
            entity.setReleaseBy(exhDTO.getReleaseBy());
            entity.setDescription(exhDTO.getDescription());
            entity.setDescriptionEn(exhDTO.getDescriptionEn());
            entity.setNote(exhDTO.getNote());
            entity.setNoteEn(exhDTO.getNoteEn());
            entity.setUnit(exhDTO.getUnit());
            entity.setUploadedDate(LocalDateTime.now());
            entity.setUpdatedBy(getUserFromToken(request));
            java.util.Date date = new java.util.Date();

            entity.setUpdatedDate(date);
            service.update(entity, entity.getId());
            service.updateExhFile(exhibitionFileService.getLatestId() + 1, entity.getId());

            undoLog.setRequestObject(g.toJson(entity, Proof.class));
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

    @DeleteMapping("deleteFile/{id}")
    public ResponseModel doDeleteExhFile(@PathVariable Integer id) {
        final String action = "doDelete";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            List<ProofExhFile> proofExhFiles = proofExhFileService.getListProofExhFileByProofId(id);
            System.out.println(proofExhFiles);
            for (int i = 0; i < proofExhFiles.size(); i++) {
                System.out.println(proofExhFiles.get(i).getId() + "LALALA");
                if (proofExhFileService.findById(proofExhFiles.get(i).getId()) != null) {
                    proofExhFileService.delete(proofExhFiles.get(i).getId());
                }
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

    @PostMapping(value = "update-file", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseModel updateFile(@RequestParam("attachments") MultipartFile multipartFile,
                                    @RequestParam(name = "proofId") Integer proofId,
                                    @RequestParam(name = "fileNumber") String fileNumber) {
        final String action = "doCreateFile";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        File file = new File(fileName);
        String uploadDir = "";

        try {
            String content;
            String type = readFileService.detectDocTypeUsingDetector(multipartFile.getInputStream());
            if (type.equals("application/pdf")) {
                content = readFileService.readPDF(multipartFile.getInputStream());
            } else if (type.contains("doc")) {
//			} else if(type.contains("application/vnd.openxmlformats-officedocument.wordprocessingml.document")){
                BodyContentHandler handler = new BodyContentHandler();
                ParseContext pcontext = new ParseContext();
                OOXMLParser msofficeparser = new OOXMLParser();
                Metadata metadata = new Metadata();
                msofficeparser.parse(multipartFile.getInputStream(), handler, metadata, pcontext);
                content = handler.toString();
            } else {
                content = readFileService.readFile(multipartFile.getInputStream());
            }

//            List<ProofExhFile> proofExhFiles = proofExhFileService.getListProofExhFileByProofId(proofId);
//            System.out.println(proofExhFiles);
//            for (int i = 0; i < proofExhFiles.size(); i++) {
//                System.out.println(proofExhFiles.get(i).getId() + "LALALA");
//                if (proofExhFileService.findById(proofExhFiles.get(i).getId()) != null) {
//                    proofExhFileService.delete(proofExhFiles.get(i).getId());
//                }
//            }
            if (fileNumber.trim().equals("1")) {
                ExhibitionFile exhFile = new ExhibitionFile();
                uploadDir = ConstantDefine.FILE_PATH.EXHIBITION + "/" + service.findById(proofId).getProgramId();
                FileUploadUtil.saveFiles(uploadDir, fileName, multipartFile);
                exhFile.setFileName(fileName);
                exhFile.setFileContent(content);
                exhFile.setFilePath(uploadDir);
                exhFile = exhibitionFileService.create(exhFile);
//                for (int i = 0; i < proofExhFiles.size(); i++) {
//                    proofExhFileService.delete(proofExhFiles.get(i).getId());
//                }
                ProofExhFile proofExhFile = new ProofExhFile();
                proofExhFile.setProofId(proofId);
                proofExhFile.setExhFileId(exhFile.getId());
                proofExhFileService.create(proofExhFile);
            } else {
                ExhibitionFile exhFile = new ExhibitionFile();
                uploadDir = ConstantDefine.FILE_PATH.EXHIBITION + "/" + service.findById(proofId).getProgramId() + "/" + service.findById(proofId).getProofCode();
                FileUploadUtil.saveFiles(uploadDir, fileName, multipartFile);
                exhFile.setFileName(fileName);
                exhFile.setFileContent(content);
                exhFile.setFilePath(uploadDir);
                exhFile = exhibitionFileService.create(exhFile);
//                for (int i = 0; i < proofExhFiles.size(); i++) {
//                    System.out.println(proofExhFiles.get(i).getId() + "LALALA");
//                    if(proofExhFileService.findById(proofExhFiles.get(i).getId()) != null){
//                        proofExhFileService.delete(proofExhFiles.get(i).getId());
//                    }
//                }
                ProofExhFile proofExhFile = new ProofExhFile();
                proofExhFile.setProofId(proofId);
                proofExhFile.setExhFileId(exhFile.getId());
                proofExhFileService.create(proofExhFile);
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

    @PostMapping(value = "update-file-no-content", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseModel updateFileWithoutFileContent(@RequestParam("attachments") MultipartFile multipartFile,
                                                      @RequestParam(name = "proofId") Integer proofId,
                                                      @RequestParam(name = "fileNumber") String fileNumber) {
        final String action = "doCreateFile";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        File file = new File(fileName);
        String uploadDir = "";

        try {
            String content = "";
//            List<ProofExhFile> proofExhFiles = proofExhFileService.getListProofExhFileByProofId(proofId);
//            System.out.println(proofExhFiles);
            if (fileNumber.trim().equals("1")) {
                ExhibitionFile exhFile = new ExhibitionFile();
                uploadDir = ConstantDefine.FILE_PATH.EXHIBITION + "/" + service.findById(proofId).getProgramId();
                FileUploadUtil.saveFiles(uploadDir, fileName, multipartFile);
                exhFile.setFileName(fileName);
                exhFile.setFileContent(content);
                exhFile.setFilePath(uploadDir);
                exhFile = exhibitionFileService.create(exhFile);
//                for (int i = 0; i < proofExhFiles.size(); i++) {
//                    proofExhFileService.delete(proofExhFiles.get(i).getId());
//                }
                ProofExhFile proofExhFile = new ProofExhFile();
                proofExhFile.setProofId(proofId);
                proofExhFile.setExhFileId(exhFile.getId());
                proofExhFileService.create(proofExhFile);
            } else {
                ExhibitionFile exhFile = new ExhibitionFile();
                uploadDir = ConstantDefine.FILE_PATH.EXHIBITION + "/" + service.findById(proofId).getProgramId() + "/" + service.findById(proofId).getProofCode();
                FileUploadUtil.saveFiles(uploadDir, fileName, multipartFile);
                exhFile.setFileName(fileName);
                exhFile.setFileContent(content);
                exhFile.setFilePath(uploadDir);
                exhFile = exhibitionFileService.create(exhFile);
//                for (int i = 0; i < proofExhFiles.size(); i++) {
//                    proofExhFileService.delete(proofExhFiles.get(i).getId());
//                }
                ProofExhFile proofExhFile = new ProofExhFile();
                proofExhFile.setProofId(proofId);
                proofExhFile.setExhFileId(exhFile.getId());
                proofExhFileService.create(proofExhFile);
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
        Proof proof = service.retrieve(id);
        ExhibitionFile exhibitionFile = exhibitionFileService.findById(id);
        System.out.println("FILE" + exhibitionFile);
        FileDownloadUtil downloadUtil = new FileDownloadUtil();
        Resource resource = null;
        try {
            resource = downloadUtil.getExhFileAsResource(exhibitionFile.getFilePath(), exhibitionFile.getFileName());
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
    public ResponseModel doRetrieve(@PathVariable Integer id, HttpServletRequest request) {
        final String action = "doRetrieve";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);

        try {
            DetailProofDTO dto = new DetailProofDTO();
            dto.setEntity(service.getDetailProof(id));
            System.out.println("DTO \n" + service.getDetailProof(id));
            dto.setFile(exhibitionFileService.getListExhFileByProofId(id));
            dto.setUnit(service.retrieve(id).getUnit());
            dto.setProgramName(programsService.findProgramByProofId(id) != null ? programsService.findProgramByProofId(id).getName() : null);
            dto.setProgramNameEn(programsService.findProgramByProofId(id) != null ? programsService.findProgramByProofId(id).getNameEn() : null);
            if (dto == null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy minh chứng");
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
//			service.delete(id);
            Proof entity = service.deleteProoff(id);
            String createdBy = getUserFromToken(request);
            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .requestObject(g.toJson(entity, Proof.class))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Xóa minh chứng  " + entity.getProofName() + " bởi " + createdBy)
                    .createdBy(createdBy)
                    .createdDate(LocalDateTime.now())
                    .tableName(TABLE_NAME)
                    .idRecord(entity.getId())
                    .build();
            undoLogService.create(undoLog);
//            String documentDir = "Files-Upload/" + id;
//            FileUploadUtil.removeDir(documentDir);
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

    @GetMapping("get-unit-by-creater")
    public ResponseModel getUnitByUsername(HttpServletRequest request) {
        final String action = "getUnitByCreater";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setContent(service.getUnitByUsername(getUserFromToken(request)));
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("/exportExcel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<Proof> proofList = service.getProof();
        ProofExcelExport excelExporter = new ProofExcelExport();
        excelExporter.export(proofList, response);
    }

    @GetMapping("/exportPDF")
    public void exportToPDF(HttpServletResponse response) throws IOException {
        List<Proof> proofList = service.getProof();
        ProofExcelExport excelExporter = new ProofExcelExport();
        excelExporter.export(proofList, response);
    }

    @PostMapping("import-excel")
    public ResponseModel importProof(@RequestParam("file") MultipartFile file,
                                     @RequestParam("programId") Integer programId,
                                     @RequestParam("organizationId") Integer organizationId,
                                     @RequestParam("forWhat") String forWhat,
                                     HttpServletRequest request) throws IOException {
        StopWatch sw = new StopWatch();
        sw.start();
        log.info("importProof");
        ResponseModel responseModel = new ResponseModel();
        try {
            service.importAutomatic(file, getUserFromToken(request), organizationId, programId, forWhat, request);
//            responseModel.setContent(content);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
        } catch (DetectExcelException excelException) {
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.EXCEL_WRONG_FORMAT);
        } catch (ExistsDirectory existsDirectory) {
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.ALREADY_EXIST_DIRECTORY);
        } catch (ExistsCriteria existsCriteria) {
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.ALREADY_EXIST_CRITERIA);
        } catch (ExistsProofException proofException) {
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.ALREADY_EXIST_PROOF);
        } catch (NotSameCodeException notSameCodeException) {
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.NOT_SAME_CODE);
        } catch (IllegalStateException illegalStateException) {
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.NOT_FOUND_APP_PARAMS);
        } catch (InvalidProofCode invalidProofCode) {
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.INVALID_PROOF_CODE);
        } catch (WrongFormat | IndexOutOfBoundsException wrongFormat) {
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.WRONG_FORMAT);
        } catch (Exception e) {
            throw e;
        } finally {
            sw.stop();
            log.info("importProof finish in: " + sw.getTotalTimeSeconds());
        }
        return responseModel;
    }

    @Transactional
    @PostMapping("auto-upload")
    public ResponseModel autoUpload(@RequestBody Integer programId) throws Exception {
        StopWatch sw = new StopWatch();
        sw.start();
        log.info("autoUpload");
        ResponseModel responseModel = new ResponseModel();
        try {
            // Check list file collection in database
            List<ExhibitionCollection> collections = collectionService.getListCollectionsByProgramId(programId);
            if (collections.isEmpty()) {
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }
            List<Proof> proofListResponse = new ArrayList<>();
            for (ExhibitionCollection exhibitionCollection : collections) {
                // check file trên server
                File collection = new File(exhibitionCollection.getPath() + "/" + exhibitionCollection.getName());
                // Check table minh chứng đã tồn tại code như file name hay chưa
                String fileName = collection.getName();
                String[] partFileName = fileName.split("\\.");
                String exhCode = fileName.replaceAll("\\." + partFileName[partFileName.length - 1], "");
                FileUploadUtil.createFolder(ConstantDefine.FILE_PATH.EXHIBITION + "/" + programId);
                if (exhibitionCollection.getIsDirectory().equals(ConstantDefine.IS_DIRECTORY.TRUE)) {
                    System.out.println("Step 1 :");
                    String[] partFile = exhibitionCollection.getPath().split("/");
                    exhCode = partFile[partFile.length - 1];
                    fileName = exhCode + "/" + fileName;
                    FileUploadUtil.createFolder(ConstantDefine.FILE_PATH.EXHIBITION + "/" + programId + "/" + exhCode);
                }
                List<Proof> listProof = service.getAllProofNeedFile(exhCode, programId);
                if (!listProof.isEmpty()) {
                    // Move file sang folder minh chứng
                    if (!collection.renameTo(new File(ConstantDefine.FILE_PATH.EXHIBITION + "/" + programId + "/" + fileName))) {
                        throw new IOException("failed rename " +
                                exhibitionCollection.getPath() + "/" + exhibitionCollection.getName() +
                                " to " + ConstantDefine.FILE_PATH.EXHIBITION + "/" + programId + "/" + fileName);
                    } else {
                        collection.delete();
                    }
                    // luu table exhfile
                    ExhibitionFile exhFile = new ExhibitionFile();
                    exhFile.setFileName(fileName);
                    exhFile.setFilePath(ConstantDefine.FILE_PATH.EXHIBITION + "/" + programId);
                    exhFile = exhibitionFileService.create(exhFile);
                    // Luu vao cac minh chung
                    Proof proof = listProof.get(0);
                    ProofExhFile pef = new ProofExhFile();
                    pef.setProofId(proof.getId());
                    pef.setExhFileId(exhFile.getId());
                    pefService.create(pef);
                    proof.setExhFile(exhFile.getId());
                    proof.setUpdatedDate(new Date());
                    if (exhibitionCollection.getIsDirectory().equals(ConstantDefine.IS_DIRECTORY.TRUE) && proofListResponse.size() > 0) {

                    } else {
                        proofListResponse.add(proof);
                    }
                }
                // remove trong table collection
                collectionService.delete(exhibitionCollection.getId());
            }
            service.saveAll(proofListResponse);
            Collections.reverse(proofListResponse);
            responseModel.setContent(proofListResponse);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw e;
        } finally {
            sw.stop();
            log.info("autoUpload finish in: " + sw.getTotalTimeSeconds());
        }
    }

    @PostMapping("format-obj")
    public ResponseModel formatObj(@RequestBody Proof entity) {
        final String action = "formatObj";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            ProofDTO dto = service.formatObj(entity);
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(dto);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;

        } catch (Exception e) {
            System.out.println(e);
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("have-not-file")
    public ResponseModel getProofHaveNotFile(@RequestParam(value = "programId", required = false) Integer programId,
                                             @RequestParam(defaultValue = "0") int currentPage,
                                             @RequestParam(defaultValue = "10") int perPage) {
        final String action = "getProofHaveNotFile";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            Pageable paging = PageRequest.of(currentPage, perPage);
            Page<Proof> pageResult = service.getProofHaveNotFile(programId, paging);
            PagingResponse<Proof> result = new PagingResponse<>();
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
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @DeleteMapping("delete-multi")
    public ResponseModel deleteMulti(@RequestBody Integer[] ids, HttpServletRequest request) {
        final String action = "doDeleteMulti";
        ResponseModel responseModel = new ResponseModel();
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            service.deleteMultiProof(ids, g, getUserFromToken(request), request);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (BeingUsedException beingUsedException) {
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.BEING_USED);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PostMapping("format-objs")
    public ResponseModel formatObjs(@RequestBody List<Proof> proofs) {
        final String action = "formatObj";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            List<ProofDTO> dto = service.formatObj(proofs);
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

    @GetMapping({"getListQuantityProofHasFile"})
    public ResponseModel getListQuantityProofHasFile(@RequestParam(value = "byYear") String byYear) {
        final String action = "getListQuantityProofHasFile";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            List<Integer> listQuantityProofHasFile = new ArrayList<>();
            ResponseModel responseModel = new ResponseModel();
            List<Programs> allPrograms = programsService.getAllProgramsByYear(Integer.parseInt(byYear));

            for (Programs programs : allPrograms) {
                int count = 0;
                List<Proof> listProofOfProgram = service.getListProofByProgramId(programs.getId());
                for (Proof proof : listProofOfProgram) {
                    if (proofExhFileService.getListProofExhFileByProofId(proof.getId()).size() > 0) {
                        count++;
                    }
                }
                listQuantityProofHasFile.add(count);
            }
            responseModel.setContent(listQuantityProofHasFile);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping({"getListQuantityProofHasNotFile"})
    public ResponseModel getListQuantityProofHasNotFile(@RequestParam(value = "byYear") String byYear) {
        final String action = "getListQuantityProofHasNotFile";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            List<Integer> getListQuantityProofHasNotFile = new ArrayList<>();
            ResponseModel responseModel = new ResponseModel();
            List<Programs> allPrograms = programsService.getAllProgramsByYear(Integer.parseInt(byYear));

            for (Programs programs : allPrograms) {
                int count = 0;
                List<Proof> listProofOfProgram = service.getListProofByProgramId(programs.getId());
                for (Proof proof : listProofOfProgram) {
                    if (proofExhFileService.getListProofExhFileByProofId(proof.getId()).size() == 0) {
                        count++;
                    }
                }
                getListQuantityProofHasNotFile.add(count);
            }
            responseModel.setContent(getListQuantityProofHasNotFile);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PostMapping(value = "privilegesProStaCri", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseModel privilegesProStaCri(@RequestBody ProStaCriDTO dto) {
        final String action = "privilegesProStaCri";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            List<CriteriaUserDTO> listOldCriteriaUSer = criteriaUserService.getListCriteriaUserByUserId(Integer.parseInt(dto.getUserId()), Integer.parseInt(dto.getOrgId()), Integer.parseInt(dto.getCategoryId()));
            List<CriteriaUserDTO> listNewCriteriaUSerId = new ArrayList<>();
            //Add new CriteriaUser
            System.out.println(dto + "DTO");
            if (dto.getListCriteriaId() != null) {
                for (int i = 0; i < dto.getListCriteriaId().size(); i++) {
                    String[] listId = dto.getListCriteriaId().get(i).split(",");
                    for (int j = 0; j < listId.length; j++) {
                        String[] ids = listId[j].split("\\.");
                        if(ids.length == 1) {
                            CriteriaUser checkExisted =  criteriaUserService.findByOrgIdAndCategoryIdAndUserIdAndProgramIdAndStaIdAndCriId(Integer.parseInt(dto.getOrgId()), Integer.parseInt(dto.getCategoryId()), Integer.parseInt(dto.getUserId()), Integer.parseInt(ids[0]) ,null, null);
                            if(checkExisted != null) {
                                CriteriaUser criteriaUser = new CriteriaUser();
                                criteriaUser.setUserId(Integer.parseInt(dto.getUserId()));
                                criteriaUser.setCategoryId(Integer.parseInt(dto.getCategoryId()));
                                criteriaUser.setProgramId(Integer.parseInt(ids[0]));
                                criteriaUser.setOrgId(Integer.parseInt(dto.getOrgId()));
                                CriteriaUserDTO newCriteriaUser = new CriteriaUserDTO(criteriaUser.getCriteriaId(), criteriaUser.getStandardId(), criteriaUser.getUserId(), criteriaUser.getProgramId(), criteriaUser.getCategoryId(), criteriaUser.getOrgId());
                                if(!listOldCriteriaUSer.contains(newCriteriaUser)){
                                    criteriaUserService.save(criteriaUser);
                                }
                                listNewCriteriaUSerId.add(newCriteriaUser);
                            } else {

                            }

                        } else if(ids.length == 2) {
                            CriteriaUser criteriaUser = new CriteriaUser();
                            criteriaUser.setUserId(Integer.parseInt(dto.getUserId()));
                            criteriaUser.setCategoryId(Integer.parseInt(dto.getCategoryId()));
                            criteriaUser.setProgramId(Integer.parseInt(ids[0]));
                            criteriaUser.setStandardId(Integer.parseInt(ids[1]));
                            criteriaUser.setOrgId(Integer.parseInt(dto.getOrgId()));
                            CriteriaUserDTO newCriteriaUser = new CriteriaUserDTO(criteriaUser.getCriteriaId(), criteriaUser.getStandardId(), criteriaUser.getUserId(), criteriaUser.getProgramId(), criteriaUser.getCategoryId(), criteriaUser.getOrgId());
                            if(!listOldCriteriaUSer.contains(newCriteriaUser)){
                                criteriaUserService.save(criteriaUser);
                            }
                            listNewCriteriaUSerId.add(newCriteriaUser);
                        } else {
                            CriteriaUser criteriaUser = new CriteriaUser();
                            criteriaUser.setUserId(Integer.parseInt(dto.getUserId()));
                            criteriaUser.setCategoryId(Integer.parseInt(dto.getCategoryId()));
                            criteriaUser.setProgramId(Integer.parseInt(ids[0]));
                            criteriaUser.setStandardId(Integer.parseInt(ids[1]));
                            criteriaUser.setCriteriaId(Integer.parseInt(ids[2]));
                            criteriaUser.setOrgId(Integer.parseInt(dto.getOrgId()));
                            CriteriaUserDTO newCriteriaUser = new CriteriaUserDTO(criteriaUser.getCriteriaId(), criteriaUser.getStandardId(), criteriaUser.getUserId(), criteriaUser.getProgramId(), criteriaUser.getCategoryId(), criteriaUser.getOrgId());
                            if(!listOldCriteriaUSer.contains(newCriteriaUser)){
                                criteriaUserService.save(criteriaUser);
                            }
                            listNewCriteriaUSerId.add(newCriteriaUser);
                        }

                    }
                }
            }

            //Delete old CriteriaUser
            System.out.println(listOldCriteriaUSer + " OLD");
            System.out.println(listNewCriteriaUSerId + " NEW");
            for (CriteriaUserDTO criteriaUser : listOldCriteriaUSer) {
//                CriteriaUserDTO criteriaUserDTO = new CriteriaUserDTO(criteriaUser.getCriteriaId(), criteriaUser.getStandardId(), criteriaUser.getUserId(), criteriaUser.getProgramId(), criteriaUser.getCategoryId());
                if (!listNewCriteriaUSerId.contains(criteriaUser)) {
                    System.out.println("DELETE " + criteriaUser);
                    criteriaUserService.deleteCriteriaUser(criteriaUser.getCriteriaId(), criteriaUser.getUserId(), criteriaUser.getProgramId(), criteriaUser.getStandardId(), criteriaUser.getCategoryId(), criteriaUser.getOrgId());
                }
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
