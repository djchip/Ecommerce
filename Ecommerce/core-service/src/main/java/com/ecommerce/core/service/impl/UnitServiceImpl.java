package com.ecommerce.core.service.impl;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.controllers.UnitController;
import com.ecommerce.core.entities.*;
import com.ecommerce.core.exceptions.DetectExcelException;
import com.ecommerce.core.exceptions.ExistsUnit;
import com.ecommerce.core.exceptions.ExistsUnitException;
import com.ecommerce.core.exceptions.UnitException;
import com.ecommerce.core.repositories.UndoLogRepository;
import com.ecommerce.core.repositories.UnitRepository;
import com.ecommerce.core.service.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ecommerce.core.repositories.ProofRepository;
import com.ecommerce.core.repositories.UserInfoRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UnitServiceImpl implements UnitService {
    private static final Integer DELETE =1;
    private static final Integer UNDO_DELETE=0;
    public static final String TABLE_NAME="unit";
    private static final Integer UN_ACTIVE_UNDO_LOG = 1;
    private static final Integer CAN_BE_DELETE = 0;
    public static final String IMPORT = "IMPORT";
    public static final Gson g = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    @Autowired
    UnitRepository unitRepository;
    @Autowired
    UndoLogService undoLogService;

    private final Path root= Paths.get("upload");
    @Autowired
    private UndoLogRepository undoLogRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    ProofRepository proofRepository;

    @Autowired
    WorkbookService workbookService;
    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UndoImportService undoImportService;

    @Override
    public Page<Unit> doSearch(String keyword, Integer classify, Pageable paging) {
        return unitRepository.doSearch(keyword, classify, paging);
    }

    @Override
    public Unit findByUnitName(String unitName) {
        Optional<Unit> entity = unitRepository.findByUnitNameAndDeletedNot(unitName, ConstantDefine.IS_DELETED.TRUE);
        if (!entity.isPresent()) {
            return null;
        }
        return entity.get();
    }

    @Override
    public Unit findByUnitCode(String unitCode) {
        Optional<Unit> entity = unitRepository.findByUnitCodeAndDeletedNot(unitCode, ConstantDefine.IS_DELETED.TRUE);
        if (!entity.isPresent()) {
            return null;
        }
        return entity.get();
    }

    @Override
    public List<Unit> findAll() {
        return unitRepository.findAll();
    }

//    @Override
//    public void save(MultipartFile file) {
//        try {
//            List<Unit> units = ExcelHelper.convertExcelToListOfUnit(file.getInputStream());
//            this.unitRepository.saveAll(units);
//        } catch (IOException e) {
//            throw new RuntimeException("fail to store excel data: " + e.getMessage());
//        }
//    }


    public List<Unit> save(List<Unit> units) {
        return unitRepository.saveAll(units);
    }

    @Override
    public Unit create(Unit entity) {
        return unitRepository.save(entity);
    }

    @Override
    public Unit retrieve(Integer id) {
        Optional<Unit> entity = unitRepository.findById(id);
        if (!entity.isPresent()) {
            return null;
        }
        return entity.get();
    }

    @Override
    public void update(Unit entity, Integer id) {
        unitRepository.save(entity);
    }

    @Override
    public void delete(Integer id) {
        unitRepository.deleteById(id);
    }

    public List<Unit> getListUnits() {
        return unitRepository.getListUnits();
    }

    @Override
    public Unit findById(Integer id) {
        Optional<Unit> entity = unitRepository.findById(id);
        if (!entity.isPresent()) {
            return null;
        }
        return entity.get();
    }

    @Override
    public Unit deleteUnit(Integer id) throws Exception {
        Optional<Unit> optional=unitRepository.findById(id);
        if(optional.isPresent()){
            List<UserInfo> userInfos= userInfoRepository.findByUnit(id);
            List<Proof> proofs= proofRepository.findByUnitId(id);
            if(userInfos.size()>0 || proofs.size() >0){
                return null;
            }
            Unit unit =optional.get();
            unit.setDeleted(DELETE);
            unit.setUndoStatus(ConstantDefine.STATUS.CAN_NOT_UNDO);
            return unitRepository.save(unit);
        }
        else {
            throw new Exception();
        }    }

    private Unit undoPut(UndoLog undoLog){
        return UnitController.g.fromJson(undoLog.getRevertObject(), Unit.class);

    }

    @Override
    public void undo(UndoLog undoLog) throws Exception {
        List<UndoLog> undoLogs = undoLogService.findByTableNameAndIdRecordAndStatusNotOrderByCreatedDateDesc(TABLE_NAME, undoLog.getIdRecord(), 1);
        for (UndoLog log: undoLogs) {
            if (log.getId().equals(undoLog.getId())){
//                Optional<Unit> optional = unitRepository.findById(log.getIdRecord());
//                Unit unit;
//                if (optional.isPresent()) {
//                    unit = optional.get();
//                } else {
//                    throw new Exception();
//                }

                Unit unit = null;
                if (!log.getAction().equals("IMPORT")) {
                    Optional<Unit> optional = unitRepository.findById(log.getIdRecord());
                    if (optional.isPresent()) {
                        unit = optional.get();
                    } else {
                        throw new Exception();
                    }
                }
                switch (log.getAction()) {
                    case "POST":
                        if (existsByCategoryIdAndDeleted(unit.getId())) {
                            throw new ExistsUnitException("Unit being use");
                        }
                        unitRepository.deleteById(unit.getId());
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        System.out.println("ko dduwwcj pehep");
                        break;
                    case "DELETE":
                        if (!unitRepository.findByUnitNameAndDeleted(unit.getUnitName(), 0).isEmpty()){
                            throw new UnitException("đơn vị đã tồn tại : ");

                       }
                        else {
                            unit.setDeleted(UNDO_DELETE);
                            log.setStatus(UN_ACTIVE_UNDO_LOG);
                            undoLogService.update(log, log.getId());
                            break;
                        }
                    case "PUT":
                        unit = undoPut(log);
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                    case "IMPORT":
                        undoImport(log);
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                }
//                if(!undoLogService.existsByTableNameAndIdRecord(TABLE_NAME, unit.getId()) && !log.getAction().equals("POST")){
//                    unit.setUndoStatus(ConstantDefine.STATUS.CAN_NOT_UNDO);
//                }
                if(!log.getAction().equals("POST")&& !log.getAction().equals("IMPORT")){
                    unitRepository.save(unit);
                }
                break;
            }else {
                log.setStatus(UN_ACTIVE_UNDO_LOG);
                undoLogService.update(log, log.getId());
            }
        }
    }
    public void undoImport(UndoLog log) {
        List<UndoImport> undoImports = undoImportService.findByUndoIdAndStatusAndTableName(log.getId(), 0, TABLE_NAME);
        if (!undoImports.isEmpty()) {
            for (UndoImport undoImport : undoImports) {
                if (undoImport.getRevertObject().equals("null")) {
                    unitRepository.deleteById(undoImport.getIdRecord());
                } else {
                    undoImportPut(undoImport);
                }
                undoImport.setStatus(UN_ACTIVE_UNDO_LOG);
                undoImportService.update(undoImport, undoImport.getId());
            }
            log.setStatus(UN_ACTIVE_UNDO_LOG);
            undoLogService.update(log, log.getId());
        }
    }
    private void undoImportPut(UndoImport undoImport) {
        Unit unit = UnitController.g.fromJson(undoImport.getRevertObject(), Unit.class);
        unitRepository.save(unit);
    }

    @Override
    public Optional<Unit> finbyID(Integer id) {
        return unitRepository.findById(id);
    }

    @Override
    public Unit getUnitByUsername(String userName) {
        return unitRepository.getUnitByUsername(userName);
    }

    @Override
	public List<Unit> getListActiveUnits() {
		// TODO Auto-generated method stub
		return unitRepository.getListActiveUnits();
	}


    private boolean existsByCategoryIdAndDeleted(Integer unitID) {
        return userInfoRepository.findByUnitIdAndDeletedNot(unitID, 1).size() > 0 || proofRepository.findByUnitId(unitID).size()>0;


    }
    @Override
    public boolean deleteUnit(Integer[] ids, Gson g, String deleteBy, HttpServletRequest request) throws Exception {
        for (Integer id : ids) {
            Unit unit = deleteUnit(id);
            if (unit == null) {
                return false;
            }
            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .requestObject(g.toJson(unit, Unit.class))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Xóa đơn vị bởi " + deleteBy)
                    .createdDate(LocalDateTime.now())
                    .createdBy(deleteBy)
                    .tableName(TABLE_NAME)
                    .idRecord(unit.getId())
                    .build();
            undoLogService.create(undoLog);
        }
        return true;
    }
    @Transactional
    @Override
    public void importLog(MultipartFile file, String userFromToken, HttpServletRequest request) throws IOException, DetectExcelException, ExistsUnit {
        Map<Unit, Unit> unitUnitMap = new HashMap<>();

        Workbook workbook = workbookService.getWorkbook(file.getInputStream(), Objects.requireNonNull(file.getOriginalFilename()));
        Sheet sheet = workbook.getSheetAt(0);
        for (Row nextRow : sheet) {
            if (nextRow.getRowNum() == 0) {
                continue;
            }
            Iterator<Cell> cellIterator = nextRow.cellIterator();
            Unit unit = new Unit();
            while (cellIterator.hasNext()) {
                //Read cell
                Cell cell = cellIterator.next();
                Object cellValue = workbookService.getCellValue(cell);
                if (cellValue == null || cellValue.toString().isEmpty()) {
                    continue;
                }
                // Set value for proof object
                int columnIndex = cell.getColumnIndex();
                String value;
                switch (columnIndex) {
                    case 1:
                        unit.setUnitName((String) workbookService.getCellValue(cell));
                        break;
                    case 2:
                        unit.setUnitNameEn((String) workbookService.getCellValue(cell));
                        break;
                    case 3:
                        unit.setUnitCode((String) workbookService.getCellValue(cell));
                        break;
                    case 4:
                        String data=workbookService.getCellValue(cell).toString();
                        if(data.trim().equals("Khoa")){
                            unit.setClassify(2);
                        }else if(data.trim().equals("Đơn vị chức năng")){
                            unit.setClassify(1);
                        }else {
                            unit.setClassify(3);
                        }
                        break;
                    case 5:
                        unit.setEmail((String) workbookService.getCellValue(cell));
                        break;
                    case 6:
                        unit.setDescription((String) workbookService.getCellValue(cell));
                        break;

                    case 7:

                        unit.setDescriptionEn((String) workbookService.getCellValue(cell));
                        break;
                    default:
                        break;
                }
            }
            unit.setDeleted(0);
            unit.setUndoStatus(0);
            List<Unit> units = unitRepository.findByUnitNamee( unit.getUnitCode());
            if(units.isEmpty() && unit.getUnitName() != null && unit.getUnitCode() != null ){
                unit.setCreatedBy(userFromToken);
                unit.setUpdatedDate( new Date());
                unit.setCreatedDate(new Date());
                unitRepository.save(unit);
                unitUnitMap.put(unit,null);
            }
            else if(units.size()>1 && unit.getUnitName() != null ) {
                throw new ExistsUnit(" kkkk");
            }else if(units.size() ==1 && unit.getUnitName() != null ){
                unit.setId(units.get(0).getId());
                unit.setUpdatedDate(new Date());
                unit.setUpdatedBy(userFromToken);
                unit.setCreatedBy(units.get(0).getCreatedBy());
                unit.setCreatedDate(units.get(0).getCreatedDate());
                unitRepository.save(unit);
                unitUnitMap.put(unit,units.get(0));
            }
        }
        workbook.close();
        insertUndo(unitUnitMap, userFromToken, true, null, request);
    }

    public void insertUndo(Map<Unit, Unit> unitUnitMap, String userFromToken,
                           boolean importUnit, UndoLog log, HttpServletRequest request) {
        UndoLog undoLog;
        if (importUnit) {
            undoLog = UndoLog.undoLogBuilder()
                    .requestObject(UnitController.g.toJson(unitUnitMap.keySet()))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Import đơn vị bởi " + userFromToken)
                    .createdDate(LocalDateTime.now())
                    .createdBy(userFromToken)
                    .tableName(TABLE_NAME)
                    .action(IMPORT)
                    .build();
            undoLogService.create(undoLog);
        } else {
            undoLog = log;
        }
        for (Map.Entry<Unit, Unit> entry : unitUnitMap.entrySet()) {
            undoImportService.insertImportUndo(entry, undoLog.getId(), entry.getKey().getId(), userFromToken, UnitController.g, TABLE_NAME);
        }
    }



}
