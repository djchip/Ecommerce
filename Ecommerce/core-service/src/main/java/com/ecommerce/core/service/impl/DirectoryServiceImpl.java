package com.ecommerce.core.service.impl;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.entities.*;
import com.ecommerce.core.exceptions.*;
import com.ecommerce.core.repositories.CriteriaRepository;
import com.ecommerce.core.service.*;
import com.ecommerce.core.util.CommonUtil;
import com.google.gson.Gson;
import com.ecommerce.core.controllers.DirectoryController;
import com.ecommerce.core.dto.DirectoryDTO;
import com.ecommerce.core.repositories.DirectoryRepository;
import com.ecommerce.core.repositories.ProofRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DirectoryServiceImpl implements DirectoryService {
    private static final Integer DELETED = 1;
    private static final Integer CAN_BE_DELETE = 0;
    private static final Integer UNDO_DELETE = 0;
    public static final String TABLE_NAME = "directory";
    private static final Integer UN_ACTIVE_UNDO_LOG = 1;
    public static final String IMPORT = "IMPORT";
    private final static String PROOF_STANDARD = "Tiêu chuẩn";

    @Autowired
    DirectoryRepository directoryRepository;
    @Autowired
    WorkbookService workbookService;
    @Autowired
    UndoLogService undoLogService;
    @Autowired
    ProgramsService programsService;
    @Autowired
    OrganizationService organizationService;
    @Autowired
    CategoriesService categoriesService;
    @Autowired
    AppParamService paramService;
    @Autowired
    ProofRepository proofRepository;
    @Autowired
    CriteriaRepository criteriaRepository;
    @Autowired
    UndoImportService undoImportService;
    @Autowired
    UserInfoServiceImpl userInfoService;


    @Override
    public List<Directory> getDirectory() {
        return directoryRepository.findAll();
    }

    @Override
    public List<Directory> getDirectoryByPro(Integer id) {
        return directoryRepository.findByProgram(id, 1);
    }

    @Override
    public List<Directory> getDirectoryByProgramId(Integer id, String userFromToken) {
        return directoryRepository.findDirectoryByProgramIdAndDeleteNot(id, userFromToken);
    }

    @Override
    public Page<Directory> doSearch(String keyword, Integer orgId, Integer programId, String username, Pageable paging) {
        return directoryRepository.doSearch(keyword, orgId, programId, username, paging);
    }

    @Override
    public Page<DirectoryDTO> doSearchExcel(String keyword, Integer orgId, Integer programId, String username, Pageable paging, String listId) {
        Page<DirectoryDTO> directories = directoryRepository.doSearchExcel(keyword, orgId, programId, username, paging);
        String[] ids = listId.split(",");
        System.out.println("listId + " + listId);
        System.out.println("ids + " + Arrays.toString(ids));
        System.out.println("LENGTH " + ids.length);
        if (ids.length > 0 && !"0".equals(ids[0])) {
            List<DirectoryDTO> listConvert = new ArrayList<>();
            for (String id : ids) {
                for (DirectoryDTO dto : directories.getContent()) {
                    if (dto.getId() == Integer.parseInt(id)) {
                        listConvert.add(dto);
                    }
                }
            }
            final int start = (int) paging.getOffset();
            final int end = Math.min((start + paging.getPageSize()), listConvert.size());
            final Page<DirectoryDTO> page = new PageImpl<>(listConvert.subList(start, end), paging, listConvert.size());
            return page;
        }
        return directories;
    }

    @Override
    public Page<DirectoryDTO> doSearchEn(String keyword, Integer proId, Pageable paging) {
        return directoryRepository.doSearchEn(keyword, proId, paging);
    }

    @Override
    public Directory findByDirectoryName(String name) {
        Optional<Directory> entity = directoryRepository.findByNameAndDeleteNot(name, ConstantDefine.IS_DELETED.TRUE);
        return entity.orElse(null);
    }

    @Override
    public List<DirectoryDTO> findAllDTO() {
        return directoryRepository.findAllDTO();
    }

    public Directory findByCode(String code) {
        Optional<Directory> entity = directoryRepository.findByCode(code);
        return entity.orElse(null);
    }

    @Override
    public Integer getMaxOrder() {
        return directoryRepository.getMaxOrder();
    }

    @Override
    public String findNameByProofId(Integer id) {
        return directoryRepository.findNameByProofId(id);
    }

    @Override
    public DirectoryDTO formatObj(Directory entity) {
        DirectoryDTO dto = new DirectoryDTO(
                entity.getId(), entity.getName(), entity.getNameEn(), entity.getCode(), entity.getDescription(), entity.getDescriptionEn(),
                entity.getCreate_by(), entity.getUpdate_by(), entity.getCreatedDate(), entity.getUpdatedDate(), null, entity.getProgramId(), entity.getOrderDir(),
                entity.getUndoStatus(), entity.getDelete(), null, null, null, entity.getCategoryId(), null, null
        );
//        Optional<ProgramsDTO> programsDTO = programsService.finbyID(entity.getProgramId());
//        programsDTO.ifPresent(value -> dto.setProgramName(value.getName()));
//
//        Optional<OrganizationDTO> organizationDTO = organizationService.finbyID(entity.getOrganizationId());
//        if (organizationDTO.isPresent()) {
//            dto.setOrganizaNam(organizationDTO.get().getName());
//            dto.setOrganizaNamEn(organizationDTO.get().getNameEn());
//            System.out.println("organizationDTO.toString() = " + organizationDTO.toString());
//        }

        Optional<Organization> programsDTO2 = organizationService.finbyID(entity.getOrganizationId());
        programsDTO2.ifPresent(organizationDTO -> dto.setOrganizaNam(organizationDTO.getName()));

        Optional<Categories> categories = categoriesService.finbyID(entity.getCategoryId());
        if (categories.isPresent()) {
            dto.setCategoryName(categories.get().getName());
            dto.setCategoryNameEn(categories.get().getNameEn());
        }

        return dto;
    }

    private Directory undoPut(UndoLog undoLog) {
        return DirectoryController.g.fromJson(undoLog.getRevertObject(), Directory.class);
    }

    @Override
    public Directory deleteDir(Integer id) throws Exception {
        Optional<Directory> optional = directoryRepository.findById(id);
        if (optional.isPresent()) {
            if (proofRepository.findByStandardIdAndDeletedNot(id, 1).size() > 0 || criteriaRepository.findByStandardIdAndDeleteNot(id, 1).size() > 0) {
                return null;
            }
            Directory userInfo = optional.get();
            userInfo.setDelete(DELETED);
            return directoryRepository.save(userInfo);
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public void undo(UndoLog undoLog) throws Exception {
        List<UndoLog> undoLogs = undoLogService.findByTableNameAndIdRecordAndStatusNotOrderByCreatedDateDesc(TABLE_NAME, undoLog.getIdRecord(), 1);
        for (UndoLog log : undoLogs) {
            if (log.getId().equals(undoLog.getId())) {
                Directory directory = null;
                if (!log.getAction().equals("IMPORT")) {
                    Optional<Directory> optional = directoryRepository.findById(log.getIdRecord());
                    if (optional.isPresent()) {
                        directory = optional.get();
                    } else {
                        throw new Exception();
                    }
                }
                switch (log.getAction()) {
                    case "POST":
                        if (checkBeingUse(directory.getId())) {
                            throw new StandardBeingUseException("standard being use exception");
                        }
                        directoryRepository.deleteById(directory.getId());
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                    case "DELETE":
                        if (!directoryRepository.findByCodeAndProgramIdAndDelete(directory.getCode(), directory.getProgramId(), 0).isEmpty()) {
                            throw new ExitsStandardException("Tiêu chuẩn đã tồn tại với mã: "
                                    + directory.getCode());
                        } else {
                            directory.setDelete(UNDO_DELETE);
                            log.setStatus(UN_ACTIVE_UNDO_LOG);
                            undoLogService.update(log, log.getId());
                            break;
                        }
                    case "PUT":
                        directory = undoPut(log);
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                    case "IMPORT":
                        undoImport(log);
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                }
                if (!log.getAction().equals("POST") && !log.getAction().equals("IMPORT")) {
                    assert directory != null;
                    directoryRepository.save(directory);
                }
                break;
            } else {
                log.setStatus(UN_ACTIVE_UNDO_LOG);
                undoLogService.update(log, log.getId());
            }
        }
    }

    private boolean checkBeingUse(Integer id) {
        return proofRepository.findByStandardIdAndDeletedNot(id, 1).size() > 0
                || criteriaRepository.findByStandardIdAndDeleteNot(id, 1).size() > 0;
    }

    @Override
    public List<Directory> save(List<Directory> directory) {
        return directoryRepository.saveAll(directory);
    }

    @Override
    public Directory save(Directory directory) {
        return directoryRepository.save(directory);
    }

    @Override
    public Directory findById(int id) {
        return directoryRepository.findById(id).get();
    }

//    @Override
//    public DetailStandardDTO getDetailDirectory(int id) {
//        return directoryRepository.getDetailbyID(id);
//    }

    @Override
    public Optional<DirectoryDTO> finbyID(Integer id) {
        return directoryRepository.finbyID(id);
    }

    @Override
    public Directory getById(int id) {
        return directoryRepository.getById(id);
    }

    @Override
    public DirectoryDTO getByIdEn(int id) {
        return directoryRepository.getByIdEn(id);
    }

    @Override
    public Directory create(Directory entity) {
        return directoryRepository.save(entity);
    }

    @Override
    public Directory retrieve(Integer id) {
        Optional<Directory> entity = directoryRepository.findById(id);
        return entity.orElse(null);
    }

    @Override
    public void update(Directory entity, Integer id) {
        directoryRepository.save(entity);
    }

    @Override
    public void delete(Integer id) throws Exception {
        Optional<Directory> optional = directoryRepository.findById(id);
        if (optional.isPresent()) {
            Directory directory = optional.get();
            directory.setDelete(DELETED);
//            userInfo.setUndoStatus(STATUS_DELETE);
            directoryRepository.save(directory);
        } else {
            throw new Exception();
        }
//        directoryRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void importDirectory(MultipartFile file, String userFromToken, Integer organizationId, Integer categoryId, HttpServletRequest request) throws IOException, DetectExcelException, ExistsDirectory, NotSameCodeException {
        Map<Directory, Directory> directoryMapUndo = new HashMap<>();
        // Get workbook
        Workbook workbook = workbookService.getWorkbook(file.getInputStream(), Objects.requireNonNull(file.getOriginalFilename()));
        // Get sheet
        Sheet sheet = workbook.getSheetAt(0);
        // Get all rows
        for (Row nextRow : sheet) {
            if (nextRow.getRowNum() == 0) {
                // Ignore header
                continue;
            }
            // Get all cells
            Iterator<Cell> cellIterator = nextRow.cellIterator();
            // Read cells and set value
            Directory directory = new Directory();
            // get code form app-param
            List<AppParam> params = paramService.findByOrganizationIdOrderByCreatedBy(organizationId);

            String codeAppParams;
            boolean EncodeAppParams;
            if (!params.isEmpty()) {
                codeAppParams = params.get(0).getName();
                EncodeAppParams = params.get(0).isEnCode();
            } else {
                throw new IllegalStateException();
            }
            while (cellIterator.hasNext()) {
                //Read cell
                Cell cell = cellIterator.next();
                Object cellValue = workbookService.getCellValue(cell);
                if (cellValue == null || cellValue.toString().isEmpty()) {
                    continue;
                }
                // Set value for proof object
                int columnIndex = cell.getColumnIndex();
                String value = null;
                if (columnIndex == 1) {
//                    if (columnIndex.contains(PROOF_STANDARD) ) {
                        value = (String) workbookService.getCellValue(cell);
                                        if (value.contains(PROOF_STANDARD) ) {

                    setCodeAndName(directory, value, codeAppParams, EncodeAppParams);
                    }
                    else {
                        throw new NotSameCodeException("Data does not match the Directory code");
                    }
                }
                if (columnIndex == 2) {
                    value = (String) workbookService.getCellValue(cell);
                    setNameEng(directory, value);
                }
            }
            directory.setOrganizationId(organizationId);
            directory.setCategoryId(categoryId);
            directory.setDelete(CAN_BE_DELETE);
            List<Directory> directories = directoryRepository.findByCodeAndOrganizationIdAndDelete(directory.getCode(), organizationId, 0);
            List<UserInfo> userInfos = new ArrayList<>();
            userInfos.add(userInfoService.findByUsername(userFromToken));
            if (directories.isEmpty()&& directory.getName() !=null ) {
                directory.setCreate_by(userFromToken);
                directory.setCreatedDate(new Date());
                directory.setUserInfos(userInfos);
                directory = directoryRepository.save(directory);
                directoryMapUndo.put(directory, null);
            } else if (directories.size() > 1) {
                throw new ExistsDirectory("The directory is already exist");
            }else if(directories.size() ==1 && directory.getName() != null ){
                directory.setId(directories.get(0).getId());
                directory.setUpdate_by(userFromToken);
                directory.setUpdatedDate(new Date());
                directory.setCreate_by(directories.get(0).getCreate_by());
                directory.setCreatedDate(directories.get(0).getCreatedDate());
                directory.setUserInfos(userInfos);
                directory = directoryRepository.save(directory);
                directoryMapUndo.put(directory, directories.get(0));
            }
        }
        workbook.close();
        insertUndo(directoryMapUndo, userFromToken, true, null, request);
    }

    @Override
    public List<Directory> finByOrgId(Integer orgId, String username) {
        return directoryRepository.findByOrganizationIdAndDeleteNot(orgId, DELETED);
    }

    @Override
    public List<Directory> findByOrgId(Integer orgId, Integer categoryId, String username) {
        return directoryRepository.findByOrganizationIdAndUsername(orgId, categoryId, username);
    }

    @Override
    public List<Directory> findByOrganizationIdAndUsername(Integer orgId, String username) {
        return directoryRepository.findByOrganizationIdAndUsername(orgId, username);
    }

    @Override
    public List<Directory> findByOrganizationIdAndUserId(Integer orgId, String userName) {
        return directoryRepository.findByOrganizationIdAndUserId(orgId, userName);
    }

    @Override
    public boolean checkCodeExists(String code, Integer orgId) {
        return directoryRepository.existsByCodeAndOrganizationIdAndDeleteIsNot(code, orgId, DELETED);
    }

    @Override
    public List<Directory> findByOrgId(Integer id) {
        return directoryRepository.findByOrgId(id);
    }

    @Override
    public List<Directory> findByOrganizationId(Integer id) {
        return directoryRepository.findByOrganizationId(id);
    }


    @Transactional
    @Override
    public int deleteDirectories(Integer[] standard, Gson g, String userFromToken, HttpServletRequest request) throws Exception {
        if (standard.length == 0) {
            return 2;
        }
        for (Integer id : standard) {
            if (proofRepository.findByStandardIdAndDeletedNot(id, 1).size() > 0 || criteriaRepository.findByStandardIdAndDeleteNot(id, 1).size() > 0)
                return 0;
        }
        for (Integer id : standard) {
            Directory directory = deleteDir(id);
            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .requestObject(g.toJson(directory, Directory.class))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Xóa tiêu chuẩn " + directory.getName() + " bởi " + userFromToken)
                    .createdDate(LocalDateTime.now())
                    .createdBy(userFromToken)
                    .tableName(TABLE_NAME)
                    .idRecord(directory.getId())
                    .build();
            undoLogService.create(undoLog);
        }
        return 1;
    }

    @Override
    public void updateExhCode(String oldExhCode, String newExhCode, int orgId) {
        directoryRepository.updateExhCode(oldExhCode, newExhCode, orgId);
    }

    @Override
    public List<Directory> findAll() {
        return directoryRepository.findAllByDeleteAndOrganizationIdNotNull(0);
    }

    @Override
    public List<Directory> SelectboxSta() {
        return directoryRepository.findByDeleteNot(0);
    }

    @Override
    public List<DirectoryDTO> formatObj(List<Directory> directories) {
        List<DirectoryDTO> dtos = new ArrayList<>();
        for (Directory directory : directories) {
            DirectoryDTO dto = new DirectoryDTO();
            dto.setName(directory.getName());
            dto.setNameEn(directory.getNameEn());
            dto.setCode(directory.getCode());
            dto.setDescription(directory.getDescription());
            dto.setDescriptionEn(directory.getDescriptionEn());
            Organization organization = organizationService.retrieve(directory.getOrganizationId());
            dto.setOrganizaNam(organization.getName());
            dto.setOrganizaNamEn(organization.getNameEn());
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public List<Integer> findAllStaIdByUsername(String username) {
        return directoryRepository.findAllStaIdByUsername(username);
    }

    @Override
    public List<Directory> getListStandardByOrgIdAndCategoryId(Integer orgId, Integer categoryId) {
        return directoryRepository.getListStandardByOrgIdAndCategoryId(orgId, categoryId);
    }

    @Override
    public Directory findByIdAndAndDelete(Integer id) {
        return directoryRepository.findByIdAndAndDelete(id, 0);
    }


    private void setCodeAndName(Directory directory, String value, String codeAppParams, boolean checkEncode) {
        List<String> myList = new ArrayList<>(Arrays.asList(value.split(":")));
        if (!myList.isEmpty()) {
            for (int i = 0; i < myList.size(); i++) {
                switch (i) {
                    case 0:
                        directory.setName(myList.get(i).trim());
                        String s = myList.get(i).replace("Tiêu chuẩn ", "");
                        if (checkEncode == true) {
                            if (s.length() == 1) {
                                s = "0" + s;
                            }
                        } else {
                            if (s.length() == 1) {
//                                s = s;
                            }
                        }
                        directory.setCode(codeAppParams + CommonUtil.trim0(s));
                        break;
                    case 1:
                        directory.setDescription(i < myList.size() ? myList.get(i).trim() : "");
                        break;
                }
            }
        }
    }

    private void setNameEng(Directory directory, String value) {
        List<String> myList = new ArrayList<>(Arrays.asList(value.split(":")));
        if (!myList.isEmpty()) {
            for (int i = 0; i < myList.size(); i++) {
                switch (i) {
                    case 0:
                        directory.setNameEn(myList.get(i).trim());
                        break;
                    case 1:
                        directory.setDescriptionEn(myList.get(i).trim());
                        break;
                }
            }
        }
    }

    public void insertUndo(Map<Directory, Directory> directoriesUndo, String userFromToken,
                           boolean importDirectory, UndoLog log, HttpServletRequest request) {
        UndoLog undoLog;
        if (importDirectory) {
            undoLog = UndoLog.undoLogBuilder()
                    .requestObject(DirectoryController.g.toJson(directoriesUndo.keySet()))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Import tiêu chuẩn bởi " + userFromToken)
                    .createdDate(LocalDateTime.now())
                    .createdBy(userFromToken)
                    .tableName(TABLE_NAME)
                    .action(IMPORT)
                    .build();
            undoLogService.create(undoLog);
        } else {
            undoLog = log;
        }
        for (Map.Entry<Directory, Directory> entry : directoriesUndo.entrySet()) {
            undoImportService.insertImportUndo(entry, undoLog.getId(), entry.getKey().getId(), userFromToken, DirectoryController.g, TABLE_NAME);
        }
    }

    public void undoImport(UndoLog log) {
        List<UndoImport> undoImports = undoImportService.findByUndoIdAndStatusAndTableName(log.getId(), 0, TABLE_NAME);
        if (!undoImports.isEmpty()) {
            for (UndoImport undoImport : undoImports) {
                if (undoImport.getRevertObject().equals("null")) {
                    directoryRepository.deleteById(undoImport.getIdRecord());
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
        Directory directory = DirectoryController.g.fromJson(undoImport.getRevertObject(), Directory.class);
        directoryRepository.save(directory);
    }
}
