package com.ecommerce.core.controllers;

import com.ecommerce.core.config.JwtConfig;
import com.ecommerce.core.entities.*;
import com.ecommerce.core.helper.ExcelHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.PagingResponse;
import com.ecommerce.core.dto.ResponseModel;
import com.ecommerce.core.dto.UnitDTO;
import com.ecommerce.core.exceptions.DetectExcelException;
import com.ecommerce.core.exceptions.ExistsUnit;
import com.ecommerce.core.service.ProofService;
import com.ecommerce.core.service.UndoLogService;
import com.ecommerce.core.service.UnitService;
import com.ecommerce.core.service.UserInfoService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("unit")
public class UnitController extends BaseController{
    private final String START_LOG = "Unit {}";
    private final String END_LOG = "Unit {} finished in: {}";
    private final String TABLE_NAME = "unit";
    public static final Gson g = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    @Autowired
    UnitService service;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    ProofService proofService;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    UndoLogService undoLogService;

    @Autowired
    HttpServletRequest request;

    @GetMapping("get-all")
    public ResponseModel doSearch(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "classify", required = false) Integer classify,
                                  @RequestParam(value = "sort", required = false) boolean sortDesc,
                                  @RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int perPage,
                                  HttpServletRequest request) {
        final String action = "doSearch";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            Pageable paging = PageRequest.of(currentPage, perPage);
            Page<Unit> pageResult = null;
            if (keyword.equals("")) {
                keyword = null;
            } else {
                keyword = keyword.trim();
            }
            pageResult = service.doSearch(keyword, classify, paging);

            if ((pageResult == null || pageResult.isEmpty())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy đơn vị.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }

            PagingResponse<Unit> result = new PagingResponse<>();
            result.setTotal(pageResult.getTotalElements());
            result.setItems(pageResult.getContent());

            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(result);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PostMapping("add-unit")
    public ResponseModel doCreate(@RequestBody Unit entity, HttpServletRequest request) {
        final String action = "doCreate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);

        try {
            String customUnitName = entity.getUnitName().trim();
            Unit checkExistedByUnitName = service.findByUnitName(customUnitName);
            String customUnitCode = entity.getUnitCode().trim();
            Unit checkExistedByUnitCode = service.findByUnitCode(customUnitCode);
            if (checkExistedByUnitName != null) {
                //unitname đã tồn tại
                if (checkExistedByUnitName.getUnitName() != null) {
                    ResponseModel responseModel = new ResponseModel();
                    responseModel.setStatusCode(HttpStatus.SC_OK);
                    responseModel.setCode(ResponseFontendDefine.NAME_ALREADY_EXIST);
                    responseModel.setErrorMessages("Tên đơn vị đã tồn tại.");
                    return responseModel;
                }
            } else if (checkExistedByUnitCode != null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
                responseModel.setErrorMessages("Mã đơn vị đã tồn tại.");
                return responseModel;
            } else {
                String createdBy = getUserFromToken(request);

                entity.setCreatedBy(createdBy);
                java.util.Date date = new java.util.Date();
                entity.setCreatedDate(date);
                entity.setUpdatedDate(date);
                entity.setDeleted(ConstantDefine.STATUS.NOT_DELETE);
                entity.setUndoStatus(ConstantDefine.STATUS.CAN_BE_UNDO);
                service.create(entity);

                UndoLog undoLog = UndoLog.undoLogBuilder()
                        .action(request.getMethod())
                        .requestObject(g.toJson(entity))
                        .status(ConstantDefine.STATUS.UNDO_NEW)
                        .url(request.getRequestURL().toString())
                        .description("Thêm mới đơn vị "+entity.getUnitName()+" bởi " + createdBy)
                        .createdDate(LocalDateTime.now())
                        .createdBy(createdBy)
                        .tableName(TABLE_NAME)
                        .idRecord(entity.getId())
                        .build();
                undoLogService.create(undoLog);
            }

            ResponseModel responseModel = new ResponseModel();
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("detail-unit/{id}")
    public ResponseModel doRetrieve(@PathVariable Integer id) {
        final String action = "doRetrieve";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            Unit entity = service.retrieve(id);
            if (entity == null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy đơn vị.");
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

    @PutMapping("update-unit")
    public ResponseModel doUpdate(@RequestBody Unit dto, HttpServletRequest request) {
        final String action = "doDelete";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            Unit entity = service.retrieve(dto.getId());
            String createdBy = getUserFromToken(request);

            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .revertObject(g.toJson(entity, Unit.class))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Cập nhập đơn vị "+entity.getUnitName()+" bởi " + createdBy)
                    .updatedDate(LocalDateTime.now())
                    .createdBy(createdBy)

                    .tableName(TABLE_NAME)
                    .idRecord(entity.getId())
                    .build();

            String inputCode = dto.getUnitCode();
            Unit checkExistedCode = null;
            if(!inputCode.equalsIgnoreCase(entity.getUnitCode())){
                checkExistedCode = service.findByUnitCode(inputCode);
            }
            if(!dto.getUnitName().equals("")) {
                String customUnitname = dto.getUnitName().trim();
                dto.setUnitName(customUnitname);
            }
            Unit checkExistedByUnitName = service.findByUnitName(dto.getUnitName().trim());
            if(checkExistedCode != null){
                if(checkExistedCode.getUnitCode() != null){
                    ResponseModel responseModel = new ResponseModel();
                    responseModel.setStatusCode(HttpStatus.SC_OK);
                    responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
                    responseModel.setErrorMessages("Mã đơn vị đã tồn tại!!!");
                    System.out.println(responseModel);
                    return responseModel;
                }
            } else if (checkExistedByUnitName != null && !checkExistedByUnitName.getUnitName().equalsIgnoreCase(entity.getUnitName())){
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.NAME_ALREADY_EXIST);
                responseModel.setErrorMessages("Tên đơn vị đã tồn tại!!!");
                System.out.println(responseModel);
                return responseModel;
            } else {
                entity.setUnitName(dto.getUnitName());
                entity.setUnitNameEn(dto.getUnitNameEn());
                entity.setUnitCode(dto.getUnitCode());
                entity.setDescription(dto.getDescription());
                entity.setDescriptionEn(dto.getDescriptionEn());
                entity.setClassify(dto.getClassify());
                entity.setEmail(dto.getEmail());
                String bearerToken = request.getHeader("Authorization").substring(7);
                Claims claims = Jwts.parser().setSigningKey(jwtConfig.getSecret().getBytes()).parseClaimsJws(bearerToken)
                        .getBody();
                String name = claims.getSubject();
                entity.setUpdatedBy(name);
                java.util.Date date = new java.util.Date();
                entity.setUpdatedDate(date);
                service.update(entity, entity.getId());

                undoLog.setRequestObject(g.toJson(entity, Unit.class));
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

    @DeleteMapping("delete-unit/{id}")
    public ResponseModel doDelete(@PathVariable Integer id, HttpServletRequest request) {
        final String action = "doDelete";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
//            service.delete(id);
            ResponseModel responseModel = new ResponseModel();
            List<UserInfo> userInfos = userInfoService.findByUnit(id);
            List<Proof> proofs = proofService.findByUnit(id);
            if (userInfos.size() > 0 || proofs.size() > 0){
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_CANNOT_DELETE);
            } else {
                Unit entity = service.deleteUnit(id);
                String createdBy = getUserFromToken(request);
                Gson g = new Gson();
                UndoLog undoLog = UndoLog.undoLogBuilder()
                        .action(request.getMethod())
                        .requestObject(g.toJson(entity, Unit.class))
                        .status(ConstantDefine.STATUS.UNDO_NEW)
                        .url(request.getRequestURL().toString())
                        .description("Xóa đơn vị "+entity.getUnitName()+" bởi " + createdBy)
                        .createdBy(createdBy)
                        .createdDate(LocalDateTime.now())
                        .tableName(TABLE_NAME)
                        .idRecord(entity.getId())
                        .build();
                undoLogService.create(undoLog);
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            }
            return responseModel;

        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping ("export")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormater.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=unit_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
        List<Unit> units = service.getListUnits();
        UnitExcel excel = new UnitExcel(units);
        excel.export(response);
    }

    @GetMapping ("exportEn")
    public void exportToExcelEn(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormater.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=unit_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
        List<Unit> units = service.getListUnits();
        UnitExcel excel = new UnitExcel(units);
        excel.exportEn(response);
    }


    @RequestMapping(value = "/import-excel", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> uploadFiles(@RequestParam("file") MultipartFile files, HttpServletRequest request) throws IOException {
        String message = "";
        List<Unit> units = new ArrayList<>();
        int count = 0;

        XSSFWorkbook workbook = new XSSFWorkbook(files.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
            if (index > 0) {
                Unit unit = new Unit();
                java.util.Date date = new java.util.Date();

                XSSFRow row = worksheet.getRow(index);
                unit.setUnitName(row.getCell(0).getStringCellValue());
                unit.setUnitCode(row.getCell(1).getStringCellValue());
                unit.setDescription(row.getCell(2).getStringCellValue());
                unit.setCreatedBy(row.getCell(3).getStringCellValue());
                unit.setCreatedDate(date);
                unit.setUpdatedDate(date);
                Unit checkExisted = service.findByUnitName(unit.getUnitName());
                if(checkExisted == null) {
                    units.add(unit);
                    count++;
                }
            }
        }
        if (ExcelHelper.checkExcelFormat(files)) {
            try {
                service.save(units);
                message = "Uploaded the file successfully: " + files.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.SC_OK).body(new ResponseMessage(message));
            } catch (Exception e) {
                message = "Could not upload the file: " + files.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.SC_EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        }
        message = "Please upload an excel file!";
        return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(new ResponseMessage(message));
    }


    @GetMapping("get-unit")
    public ResponseModel getListUnits() {
        final String action = "getListUnits";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(service.getListUnits());
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("{listUnit}")
    @ResponseBody
    public ResponseModel getListUnitSelected(@PathVariable String listUnit) {
        final String action = "getUnitById";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            List<Unit> units = new ArrayList<>();
            String[] listUnitId = listUnit.split(",");
            for(int i = 0; i < listUnitId.length; i++){
                units.add(service.findById(Integer.parseInt(listUnitId[i])));
            }
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(units);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }
    
    @GetMapping("listActiveUnits")
    public ResponseModel getListActiveUnits() {
        final String action = "getListActiveUnits";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(service.getListActiveUnits());
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
    public ResponseModel deleteMulti(@RequestBody Integer[] unit) {
        final String action = "doDeleteMulti";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            if(service.deleteUnit(unit, g, getUserFromToken(request), request)){
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

    @PostMapping("import-excelll")
    public ResponseModel importLog(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        StopWatch sw = new StopWatch();
        sw.start();
        log.info("importLog");
        ResponseModel responseModel = new ResponseModel();
        try {
            service.importLog(file, getUserFromToken(request), request);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
        } catch (DetectExcelException excelException) {
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.EXCEL_WRONG_FORMAT);
        } catch (ExistsUnit unit) {
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
        } catch (Exception e) {
            throw e;
        } finally {
            sw.stop();
            log.info("importLog finish in: " + sw.getTotalTimeSeconds());
        }
        return responseModel;
    }


    @PostMapping("format-objs")
    public ResponseModel formatObjs(@RequestBody List<Unit> list, HttpServletRequest request) {
        final String action = "formatObj";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            List<UnitDTO> dtos = undoLogService.formatObj(list);
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(dtos);
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
}
