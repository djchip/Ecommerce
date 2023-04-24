package com.ecommerce.core.service.impl;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.entities.SoftwareLog;
import com.ecommerce.core.entities.SoftwareVersion;
import com.ecommerce.core.entities.UndoLog;
import com.ecommerce.core.exceptions.DetectExcelException;
import com.ecommerce.core.exceptions.ExistsSoftwareLog;
import com.ecommerce.core.repositories.SoftwareLogRepository;
import com.ecommerce.core.repositories.SoftwareVersionRepository;
import com.ecommerce.core.service.SoftwareLogService;
import com.ecommerce.core.service.UndoImportService;
import com.ecommerce.core.service.UndoLogService;
import com.ecommerce.core.service.WorkbookService;
import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SoftwareLogServiceIml implements SoftwareLogService {
    private static final Integer UNDO_DELETE = 0;
    private static final Integer DELETED = 1;
    private static final Integer CAN_BE_DELELTED = 0;
    private static final Integer STATUS_DELETE = 3;
    private static final Integer FIRST_INDEX = 0;
    private final String TABLE_NAME = "software_log";
    private static final Integer UN_ACTIVE_UNDO_LOG = 1;
    public static final String IMPORT = "IMPORT";

    @Autowired
    SoftwareLogRepository softwareLogRepository;
    @Autowired
    UndoLogService undoLogService;
    @Autowired
    SoftwareVersionRepository repo;
    @Autowired
    WorkbookService workbookService;
    @Autowired
    UndoImportService undoImportService;
    @Override
    public SoftwareLog create(SoftwareLog entity) {
        // TODO Auto-generated method stub
        return softwareLogRepository.save(entity);
    }

    @Override
    public SoftwareLog retrieve(Integer id) {
        // TODO Auto-generated method stub
        Optional<SoftwareLog> entity = softwareLogRepository.findById(id);
        if (!entity.isPresent()) {
            return null;
        }
        return entity.get();
    }

    @Override
    public void update(SoftwareLog entity, Integer id) {
        // TODO Auto-generated method stub
        softwareLogRepository.save(entity);
    }

    @Override
    public void delete(Integer id) {
        // TODO Auto-generated method stub
        softwareLogRepository.deleteById(id);
    }
//    @Transactional
//    @Override
//    public void undo(Integer id) throws Exception {
//        List<UndoLog> undoLogs = undoLogService.findByTableNameAndIdRecordAndStatusNotOrderByCreatedDateDesc(TABLE_NAME, id, 1);
//        if (!undoLogs.isEmpty()) {
//            UndoLog undoLog = undoLogs.get(FIRST_INDEX);
//            Optional<SoftwareLog> optional = softwareLogRepository.findById(id);
//            SoftwareLog softwareLog;
//            if (optional.isPresent()) {
//                softwareLog = optional.get();
//            } else {
//                throw new Exception();
//            }
//            switch (undoLog.getAction()) {
//                case "POST":
//                    softwareLogRepository.deleteById(id);
//                    undoLogService.delete(undoLog.getId());
//                    break;
//                case "DELETE":
//                    softwareLog.setDeleted(UNDO_DELETE);
//                    undoLogService.delete(undoLog.getId());
//                    break;
//                case "PUT":
//                    softwareLog = undoPut(undoLog);
//                    undoLogService.delete(undoLog.getId());
//                    break;
//            }
//            if(!undoLogService.existsByTableNameAndIdRecord(TABLE_NAME, id) && !undoLog.getAction().equals("POST")){
//                softwareLog.setUndoStatus(ConstantDefine.STATUS.CAN_NOT_UNDO);
//            }
//            if(!undoLog.getAction().equals("POST")){
//                softwareLogRepository.save(softwareLog);
//            }
//        }
//    }

    @Override
    public SoftwareLog deleteError(Integer id) throws Exception {
        Optional<SoftwareLog> optional = softwareLogRepository.findById(id);
        if (optional.isPresent()) {
            SoftwareLog softwareLog = optional.get();
            softwareLog.setDeleted(DELETED);
            softwareLog.setUndoStatus(ConstantDefine.STATUS.CAN_BE_UNDO);
            return softwareLogRepository.save(softwareLog);
        } else {
            throw new Exception();
        }
    }

    @Transactional
    @Override
    public void undo(UndoLog undoLog) throws Exception {
        List<UndoLog> undoLogs = undoLogService.findByTableNameAndIdRecordAndStatusNotOrderByCreatedDateDesc(TABLE_NAME, undoLog.getIdRecord(), 1);
        for (UndoLog log: undoLogs) {
            if (log.getId().equals(undoLog.getId())){
                Optional<SoftwareLog> optional = softwareLogRepository.findById(log.getIdRecord());
                SoftwareLog softwareLog;
                if (optional.isPresent()) {
                    softwareLog = optional.get();
                } else {
                    throw new Exception();
                }
                switch (log.getAction()) {
                    case "POST":
                        softwareLogRepository.deleteById(softwareLog.getId());
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                    case "DELETE":
                        softwareLog.setDeleted(UNDO_DELETE);
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                    case "PUT":
                        softwareLog = undoPut(log);
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                }
                if(!undoLogService.existsByTableNameAndIdRecord(TABLE_NAME, softwareLog.getId()) && !log.getAction().equals("POST")){
                    softwareLog.setUndoStatus(ConstantDefine.STATUS.CAN_NOT_UNDO);
                }
                if(!log.getAction().equals("POST")){
                    softwareLogRepository.save(softwareLog);
                }
                break;
            }else {
                log.setStatus(UN_ACTIVE_UNDO_LOG);
                undoLogService.update(log, log.getId());
            }
        }
    }
    private SoftwareLog undoPut(UndoLog undoLog) {
        Gson g = new Gson();
        //        userInfo.setUndoStatus(ConstantDefine.STATUS.CAN_NOT_UNDO);
        return g.fromJson(undoLog.getRevertObject(), SoftwareLog.class);
    }

    @Override
    public Page<SoftwareLog> doSearch(String keyword,Integer status, LocalDateTime startDate, LocalDateTime endDate, Pageable paging) {
        // TODO Auto-generated method stub
        return softwareLogRepository.doSearch(keyword,status, startDate, endDate, paging);
    }

    @Override
    public boolean deleteUser(Integer[] ids, Gson g, String createdBy, HttpServletRequest request) throws Exception {
        for(Integer id: ids){
            SoftwareLog entity= deleteError(id);
            if(entity == null){
                return false;
            }
            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .requestObject(g.toJson(entity, SoftwareLog.class))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Xóa nhật ký phiên bản bởi " + createdBy)
                    .createdBy(createdBy)
                    .createdDate(LocalDateTime.now())
                    .tableName(TABLE_NAME)
                    .idRecord(entity.getId())
                    .build();
            undoLogService.create(undoLog);
        }
        return true;
    }

    @Override
    public SoftwareLog findByError(String error) {
        Optional<SoftwareLog> entity = softwareLogRepository.findByError(error, ConstantDefine.IS_DELETED.TRUE);
        return entity.orElse(null);
    }
    @Override
    public SoftwareLog findById(Integer id) {
        return softwareLogRepository.findById(id).get();    }
    @Override
    public SoftwareVersion findBySVersion(String version) {
        Optional<SoftwareVersion> entity = repo.findBySVersion(version);
        if (!entity.isPresent()) {
            return null;
        }
        return entity.get();
    }
    @Override
    public List<SoftwareLog> findByVersion(String version) {
        List<SoftwareLog> entity = softwareLogRepository.findByVersion(version);
        return entity;
    }
    @Override
    public SoftwareLog save(SoftwareLog softwareLog) {
        return softwareLogRepository.save(softwareLog);
    }
    @Override
    public List<SoftwareLog> save(List<SoftwareLog> softwarelog) {
        return softwareLogRepository.saveAll(softwarelog);
    }

    @Transactional
    @Override
    public void importLog(MultipartFile file, String userFromToken) throws IOException, DetectExcelException, ExistsSoftwareLog {
        Workbook workbook = workbookService.getWorkbook(file.getInputStream(), Objects.requireNonNull(file.getOriginalFilename()));
        Sheet sheet = workbook.getSheetAt(0);
        for (Row nextRow : sheet) {
            if (nextRow.getRowNum() == 0) {
                // Ignore header
                continue;
            }
            Iterator<Cell> cellIterator = nextRow.cellIterator();
            SoftwareLog softwareLog = new SoftwareLog();
            SoftwareVersion softwareVersion ;
            while (cellIterator.hasNext()) {
                //Read cell
                Cell cell = cellIterator.next();
                Object cellValue = workbookService.getCellValue(cell);
                if (cellValue == null || cellValue.toString().isEmpty()) {
                    continue;
                }
                int columnIndex = cell.getColumnIndex();
                String value;
                switch (columnIndex) {
                    case 1:
                        softwareLog.setError((String) workbookService.getCellValue(cell));
                        break;
                    case 2:
                        softwareLog.setAmendingcontent((String) workbookService.getCellValue(cell));
                        break;
                    case 3:
                        softwareLog.setNote((String) workbookService.getCellValue(cell));
                        break;
                    default:
                        break;
                }
            }
            if (!softwareLogRepository.existsByAmendingcontentAndErrorAndDeletedIsNot(softwareLog.getError(), softwareLog.getAmendingcontent(), 1)) {
                softwareLog.setCreatedBy(userFromToken);
                softwareLog.setErrorlogtime(new Date());
                softwareLog.setDeleted(CAN_BE_DELELTED);
                softwareLog.setStatus(0);

                softwareLog.setVersion(repo.getLastestVersion().getVersion());
                softwareLogRepository.save(softwareLog);
            } else {
                throw new ExistsSoftwareLog("Software Error Log is already exist");
            }
        }
        workbook.close();
    }

    @Override
    public SoftwareLog formatObj(SoftwareLog entity) {
        SoftwareLog softwareLog = new SoftwareLog(entity.getId(), entity.getError(), entity.getAmendingcontent(), entity.getVersion(), entity.getErrorlogtime(), entity.getSuccessfulrevisiontime(), entity.getCreatedBy(), entity.getUpdatedBy(), entity.getNote(), entity.getStatus(), entity.getUndoStatus(), entity.getDeleted());
    return entity;
    }
}
