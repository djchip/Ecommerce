package com.ecommerce.core.service.impl;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.controllers.CriteriaController;
import com.ecommerce.core.entities.*;
import com.ecommerce.core.exceptions.CheckDecentralizeException;
import com.ecommerce.core.exceptions.CriterionBeingUsedException;
import com.ecommerce.core.exceptions.ExistsCriteria;
import com.ecommerce.core.exceptions.ExistsCriteriaException;
import com.ecommerce.core.repositories.CriteriaRepository;
import com.ecommerce.core.service.*;
import com.ecommerce.core.util.CommonUtil;
import com.google.gson.Gson;
import com.ecommerce.core.dto.CriteriaDTO;
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
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CriteriaServiceImpl implements CriteriaService {
    private static final Integer DELETED = 1;
    private static final Integer UNDO_DELETE = 0;
    private static final Integer STATUS_DELETE = 0;
    private static final String TABLE_NAME = "criteria";
    private static final int FIRST_INDEX = 0;
    public static final String IMPORT = "IMPORT";
    private String CODE;
    private static final Integer UN_ACTIVE_UNDO_LOG = 1;

    @Autowired
    CriteriaRepository criteriaRepository;
    @Autowired
    WorkbookService workbookService;
    @Autowired
    DirectoryRepository directoryRepository;
    @Autowired
    CategoriesService categoriesService;
    @Autowired
    RolesService rolesService;
    @Autowired
    UndoLogService undoLogService;
    @Autowired
    ProgramsService programsService;
    @Autowired
    DirectoryService directoryService;
    @Autowired
    OrganizationService organizationService;
    @Autowired
    AppParamService paramService;
    @Autowired
    ProofRepository proofRepository;
    @Autowired
    DirectoryServiceImpl directoryServiceImpl;
    @Autowired
    UndoImportService undoImportService;
    @Autowired
    UserInfoServiceImpl userInfoService;

    @Override
    public List<Criteria> getCriteria() {
        return criteriaRepository.findAllByDeleteNot();
    }

    @Override
    public Page<CriteriaDTO> doSearch(String keyword, Integer proId, Integer stanId, Integer orgId, String userName, Pageable paging) {
        return criteriaRepository.doSearch(keyword, proId, stanId, orgId, userName, paging);
    }

    @Override
    public Page<CriteriaDTO> doSearchExcel(String keyword, Integer proId, Integer stanId, Integer orgId, String userName, Pageable paging, String listId) {
        Page<CriteriaDTO> criterias = criteriaRepository.doSearch(keyword, proId, stanId, orgId, userName, paging);
        String[] ids = listId.split(",");
        if (ids.length > 0 && !"0".equals(ids[0])) {
            List<CriteriaDTO> listConvert = new ArrayList<>();
            for (String id : ids) {
                for (CriteriaDTO dto : criterias.getContent()) {
                    if (dto.getId() == Integer.parseInt(id)) {
                        listConvert.add(dto);
                    }
                }
            }
            final int start = (int) paging.getOffset();
            final int end = Math.min((start + paging.getPageSize()), listConvert.size());
            final Page<CriteriaDTO> page = new PageImpl<>(listConvert.subList(start, end), paging, listConvert.size());
            return page;
        }
        return criterias;
    }

    @Override
    public Page<CriteriaDTO> doSearchExcelEn(String keyword, Integer proId, Integer stanId, Integer orgId, String userName, Pageable paging, String listId) {
        Page<CriteriaDTO> criterias = criteriaRepository.doSearchEn(keyword, proId, stanId, orgId, userName, paging);
        String[] ids = listId.split(",");
        if (ids.length > 0 && !"0".equals(ids[0])) {
            List<CriteriaDTO> listConvert = new ArrayList<>();
            for (String id : ids) {
                for (CriteriaDTO dto : criterias.getContent()) {
                    if (dto.getId() == Integer.parseInt(id)) {
                        listConvert.add(dto);
                    }
                }
            }
            final int start = (int) paging.getOffset();
            final int end = Math.min((start + paging.getPageSize()), listConvert.size());
            final Page<CriteriaDTO> page = new PageImpl<>(listConvert.subList(start, end), paging, listConvert.size());
            return page;
        }
        return criterias;
    }

    @Override
    public Page<CriteriaDTO> doSearchEn(String keyword, Integer proId, Integer stanId, Integer orgId, String userName, Pageable paging) {
        return criteriaRepository.doSearchEn(keyword, proId, stanId, orgId, userName, paging);
    }

    @Override
    public Criteria findByDirectoryName(String name) {
        Optional<Criteria> entity = criteriaRepository.findByNameAndDeleteNot(name, ConstantDefine.IS_DELETED.TRUE);
        return entity.orElse(null);
    }

    @Override
    public List<Criteria> save(List<Criteria> criteria) {
        return criteriaRepository.saveAll(criteria);
    }

    @Override
    public Criteria save(Criteria directory) {
        return criteriaRepository.save(directory);
    }

    @Override
    public CriteriaDTO getById(Integer id) {
        return criteriaRepository.getById(id);
    }

    @Override
    public CriteriaDTO getByIdEn(Integer id) {
        return criteriaRepository.getByIdEn(id);
    }

    @Override
    public Criteria findById(int id) {
        return criteriaRepository.findById(id).get();
    }

    @Override
    public String findNameById(Integer id) {
        return criteriaRepository.findNameById(id);
    }

    @Override
    public Integer getMaxOrder(Integer stanId) {
        return criteriaRepository.getMaxOrder(stanId);
    }

    private Criteria undoPut(UndoLog undoLog) {
        Gson g = new Gson();
        return g.fromJson(undoLog.getRevertObject(), Criteria.class);
    }

    @Override
    public Criteria deleteDir(Integer id) throws Exception {
        Optional<Criteria> optional = criteriaRepository.findById(id);
        if (optional.isPresent()) {
//            if(proofRepository.existsByCriteriaIdAndStatus(id, 0)){
//                throw new BeingUsedException("Has proof using the program");
//            }
            List<Proof> checkExistedByCriteria = proofRepository.findByCriteriaIdAndDeletedNot(id, 1);
            if (checkExistedByCriteria != null && checkExistedByCriteria.size() > 0) {
//                throw new BeingUsedException("Has proof using the program");
                return null;
            }
            Criteria userInfo = optional.get();
            userInfo.setDelete(DELETED);
            return criteriaRepository.save(userInfo);
//            return optional.get();
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
                Criteria criteria = null;
                if (!log.getAction().equals("IMPORT")) {
                    Optional<Criteria> optional = criteriaRepository.findById(log.getIdRecord());
                    if (optional.isPresent()) {
                        criteria = optional.get();
                    } else {
                        throw new Exception();
                    }
                }
                switch (log.getAction()) {
                    case "POST":
                        if (proofRepository.existsByCriteriaIdAndDeleted(criteria.getId(), 0)) {
                            throw new CriterionBeingUsedException("criterion being used");
                        }
                        criteriaRepository.deleteById(criteria.getId());
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                    case "DELETE":
                        if (!criteriaRepository.findByCodeAndOrganizationIdAndProgramIdAndDelete(criteria.getCode(), criteria.getOrganizationId(), criteria.getProgramId(), 0).isEmpty()) {
                            throw new ExistsCriteriaException("Tiêu chuẩn đã tồn tại với mã: "
                                    + criteria.getCode());
                        } else {
                            criteria.setDelete(UNDO_DELETE);
                            log.setStatus(UN_ACTIVE_UNDO_LOG);
                            undoLogService.update(log, log.getId());
                            break;
                        }
                    case "PUT":
                        criteria = undoPut(log);
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                    case "IMPORT":
                        undoImport(log);
                        directoryServiceImpl.undoImport(log);
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                }
                if (!log.getAction().equals("POST") && !log.getAction().equals("IMPORT")) {
                    assert criteria != null;
                    criteriaRepository.save(criteria);
                }
                break;
            } else {
                log.setStatus(UN_ACTIVE_UNDO_LOG);
                undoLogService.update(log, log.getId());
            }
        }
    }

    @Override
    public Criteria create(Criteria entity) {
        return criteriaRepository.save(entity);
    }

    @Override
    public Criteria retrieve(Integer id) {
        Optional<Criteria> entity = criteriaRepository.findById(id);
        return entity.orElse(null);
    }

    @Override
    public void update(Criteria entity, Integer id) {
        criteriaRepository.save(entity);
    }

    @Override
    public void delete(Integer id) throws Exception {
        Optional<Criteria> optional = criteriaRepository.findById(id);
        if (optional.isPresent()) {
            Criteria userInfo = optional.get();
            userInfo.setDelete(DELETED);
            criteriaRepository.save(userInfo);
        } else {
            throw new Exception();
        }
    }

    @Override
    public List<Criteria> getDirectory() {
        // TODO Auto-generated method stub
        return null;
    }

    @Transactional
    @Override
    public void importCriteria(MultipartFile file, String userFromToken, Integer organizationId, Integer categoryId, HttpServletRequest request) throws Exception {
        List<Directory> directories = new ArrayList<>();
        Map<Criteria, Criteria> criteriaMapUndo = new HashMap<>();
        Map<Directory, Directory> directoryMapUndo = new HashMap<>();
        int indexDirectory = 0;
        // Get workbook
        Workbook workbook = workbookService.getWorkbook(file.getInputStream(), Objects.requireNonNull(file.getOriginalFilename()));
        // Get sheet
        Sheet sheet = workbook.getSheetAt(0);
        List<UserInfo> userInfos = new ArrayList<>();
        userInfos.add(userInfoService.findByUsername(userFromToken));

        // Get all rows
        for (Row nextRow : sheet) {
            if (nextRow.getRowNum() == 0) {
                // Ignore header
                continue;
            }
            // Get all cells
            Iterator<Cell> cellIterator = nextRow.cellIterator();
            // Read cells and set value
            Criteria criteria = new Criteria();
            boolean checkEncode;
            List<AppParam> params = paramService.findByOrganizationIdOrderByCreatedBy(organizationId);
            if (!params.isEmpty()) {
                CODE = params.get(0).getName();
                checkEncode = params.get(0).isEnCode();
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
                // Set value for criteria object
                int columnIndex = cell.getColumnIndex();
                String value;
                switch (columnIndex) {
                    case 1:
                        value = (String) workbookService.getCellValue(cell);
                        Directory directory = new Directory();
                        setDirectory(checkEncode, directory, value, organizationId, categoryId, userFromToken, directoryMapUndo);
                        directory.setUserInfos(userInfos);
                        directories.add(directory);
                        indexDirectory++;
                        break;
                    case 2:
                        value = (String) workbookService.getCellValue(cell);
                        setCodeAndName(criteria, value, directories.get(indexDirectory - 1), organizationId, categoryId, userFromToken);
                        criteria.setUserInfos(userInfos);
                        break;
                    case 3:
                        value = (String) workbookService.getCellValue(cell);
                        setNameEn(criteria, value);
                        break;
                    default:
                        break;
                }
            }
            List<Criteria> criteriaList = criteriaRepository.findByCodeAndOrganizationIdAndStandardIdAndDelete(criteria.getCode(), organizationId, criteria.getStandardId(), 0);
            if (criteriaList.isEmpty() && criteria.getCode() != null) {
                criteria.setCreate_by(userFromToken);
                criteria.setCreatedDate(new Date());
                criteriaMapUndo.put(criteria, null);
            } else if (criteriaList.size() > 1) {
                throw new ExistsCriteria("The criteria is already exist");
            } else if (criteriaList.size() == 1 && criteria.getCode() != null) {
                criteria.setId(criteriaList.get(0).getId());
                criteria.setUpdatedDate(new Date());
                criteria.setUpdate_by(userFromToken);
                criteria.setCreatedDate(criteriaList.get(0).getCreatedDate());
                criteria.setCreate_by(criteriaList.get(0).getCreate_by());
                criteriaMapUndo.put(criteria, criteriaList.get(0));
            } else {
                continue;
            }
            criteriaRepository.save(criteria);
        }
        workbook.close();
        UndoLog undoLog = insertUndo(criteriaMapUndo, userFromToken, true, null, request);
        directoryServiceImpl.insertUndo(directoryMapUndo, userFromToken, false, undoLog, request);
    }

    public UndoLog insertUndo(Map<Criteria, Criteria> criteriaMapUndo, String userFromToken,
                              boolean importCriteria, UndoLog log, HttpServletRequest request) {
        UndoLog undoLog;
        if (importCriteria) {
            undoLog = undoLogService.create(UndoLog.undoLogBuilder()
                    .requestObject(CriteriaController.g.toJson(criteriaMapUndo.keySet()))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Import tiêu chí bởi " + userFromToken)
                    .createdDate(LocalDateTime.now())
                    .createdBy(userFromToken)
                    .tableName(TABLE_NAME)
                    .action(IMPORT)
                    .build());
        } else {
            undoLog = log;
        }
        for (Map.Entry<Criteria, Criteria> entry : criteriaMapUndo.entrySet()) {
            undoImportService.insertImportUndo(entry, undoLog.getId(), entry.getKey().getId(), userFromToken, CriteriaController.g, TABLE_NAME);
        }
        return undoLog;
    }

    @Override
    public List<Criteria> findByStandardId(Integer id, Integer deleted) {
        return criteriaRepository.findByStandardIdAndDeleteNot(id, deleted);
    }

    @Override
    public List<Criteria> getListCriteriaBySta(Integer id) {
        return criteriaRepository.getListCriteriaBySta(id);
    }

    @Override
    public CriteriaDTO formatObj(Criteria entity) {
        CriteriaDTO dto = new CriteriaDTO(
                entity.getId(), entity.getName(), entity.getNameEn(), entity.getCode(), entity.getDescription(), entity.getCreate_by(), entity.getUpdate_by(), entity.getCreatedDate(),
                entity.getUpdatedDate(), null, null, entity.getStandardId(), entity.getProgramId(), entity.getDelete(), entity.getOrganizationId(), null, null, entity.getCategoryId(), null, null
        );
        Optional<DirectoryDTO> directoryDTO = directoryService.finbyID(entity.getStandardId());
        directoryDTO.ifPresent(value -> dto.setStandarName(value.getName()));

        Optional<Organization> programsDTO = organizationService.finbyID(entity.getOrganizationId());
        programsDTO.ifPresent(organizationDTO -> dto.setOrgramName(organizationDTO.getName()));

        Optional<Categories> categories = categoriesService.finbyID(entity.getCategoryId());
        if (categories.isPresent()) {
            dto.setCategoryName(categories.get().getName());
            dto.setCategoryNameEn(categories.get().getNameEn());
        }
        return dto;
    }

    @Override
    public List<CriteriaDTO> formatObj(List<Criteria> entity) {
        List<CriteriaDTO> dtos = new ArrayList<>();
        for (Criteria criteria : entity) {
            CriteriaDTO dto = new CriteriaDTO();
            dto.setName(criteria.getName());
            dto.setNameEn(criteria.getNameEn());
            dto.setCode(criteria.getCode());
            dto.setDescription(criteria.getDescription());
            if (criteria.getOrganizationId() != null) {
                Organization organization = organizationService.retrieve(criteria.getOrganizationId());
                dto.setOrgramName(organization.getName());
                dto.setOrgNameEng(organization.getNameEn());
            }
            if (criteria.getStandardId() != null) {
                Directory directory = directoryService.retrieve(criteria.getStandardId());
                dto.setStandarName(directory.getName());
                dto.setDirNameEng(directory.getNameEn());
            }
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public Optional<CriteriaDTO> finbyID(Integer id) {
        return criteriaRepository.finbyID(id);
    }

    @Override
    public boolean checkCodeExists(String code, Integer orgId, Integer staId) {
        return criteriaRepository.existsByCodeAndOrganizationIdAndStandardIdAndDeleteNot(code, orgId, staId, DELETED);
    }

    @Override
    public int deleteCriteria(Integer[] ids, Gson g, String deleteBy, HttpServletRequest request) throws Exception {
        if (ids.length == 0) {
            return 2;
        }
        for (Integer id : ids) {
            List<Proof> checkExistedByCriteria = proofRepository.findByCriteriaIdAndDeletedNot(id, 1);
            if (checkExistedByCriteria != null && checkExistedByCriteria.size() > 0) {
                return 0;
            }
        }

        for (Integer id : ids) {
            Criteria criteria = deleteDir(id);
            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .requestObject(g.toJson(criteria, Criteria.class))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Xóa tiêu chí " + criteria.getName() + " bởi " + deleteBy)
                    .createdDate(LocalDateTime.now())
                    .createdBy(deleteBy)
                    .tableName(TABLE_NAME)
                    .idRecord(criteria.getId())
                    .build();
            undoLogService.create(undoLog);
        }

        return 1;
    }

    @Override
    public void updateExhCode(String oldExhCode, String newExhCode, int orgId) {
        criteriaRepository.updateExhCode(oldExhCode, newExhCode, orgId);
    }

    @Override
    public List<Criteria> findByProgramIdAndDeleteNot(Integer programId, Integer delete) {
        return criteriaRepository.findByProgramIdAndDeleteNot(programId, delete);
    }

    @Override
    public List<Criteria> findAllByDelete() {
        return criteriaRepository.findAllByDeleteAndStandardIdNotNullAndOrganizationIdNotNull(0);
    }

    @Override
    public List<Integer> findAllCriIdByUsername(String username) {
        return criteriaRepository.findAllCriIdByUsername(username);
    }

    @Override
    public List<Criteria> findByOrganizationId(Integer id) {
        return criteriaRepository.findByOrganizationId(id);
    }

    @Override
    public List<Criteria> getCriteriaByDirectoryId(Integer id, String userFromToken) {
        return criteriaRepository.findCriteriaByStandardIdAndAndDeleteNot(id, userFromToken);
    }

    @Override
    public List<Criteria> getListCriteriaByOrgIdAndCategoryId(Integer orgId, Integer categoryId) {
        return criteriaRepository.getListCriteriaByOrgIdAndCategoryId(orgId, categoryId);
    }

    @Override
    public List<Criteria> getListCriteriaByOrgIdAndCategoryIdAndStandardId(Integer orgId, Integer categoryId, Integer standardId) {
        return criteriaRepository.getListCriteriaByOrgIdAndCategoryIdAndStandardId(orgId, categoryId, standardId);

    }

    private void setDirectory(boolean checkEncode, Directory directory, String value, Integer organizationId, Integer categoryId, String userFromToken, Map<Directory, Directory> directoryMapUndo) throws Exception {
        List<String> myList = new ArrayList<String>(Arrays.asList(value.split(":")));
        if (!myList.isEmpty()) {
            List<String> strings = new ArrayList<String>(Arrays.asList(myList.get(0).trim().split(" ")));
            String code = strings.get(2).trim().replaceAll("\\.", "");
            System.out.println("code = " + code);
            System.out.println("strings = " + strings);
            System.out.println("myList = " + myList);
            if (checkEncode == true) {
                if (code.length() == 1) {
                    code = "0" + code;
                }
            } else {
                if (code.length() == 1) {
                }
            }
            directory.setCode(CODE + CommonUtil.trim0(code));
            directory.setOrganizationId(organizationId);
            directory.setCategoryId(categoryId);
            directory.setDelete(STATUS_DELETE);
            directory.setName(myList.get(0));
            directory.setDescription(myList.size() > 1 ? myList.get(1) : "");
            List<Directory> directory1 = directoryRepository.findByCodeAndOrganizationIdAndDelete(directory.getCode(), organizationId, 0);
            if (directory1.isEmpty()) {
                directory.setCreatedDate(new Date());
                directory.setCreate_by(userFromToken);
                directory = directoryRepository.save(directory);
                directoryMapUndo.put(directory, null);
            } else if (directory1.size() == 1) {
                directory.setId(directory1.get(0).getId());
                directory.setUpdatedDate(new Date());
                directory.setUpdate_by(userFromToken);
                directory.setCreatedDate(directory1.get(0).getCreatedDate());
                directory.setCreate_by(directory1.get(0).getCreate_by());
                directory.setNameEn(directory1.get(0).getNameEn());
                directory.setDescriptionEn(directory1.get(0).getDescriptionEn());
                directory = directoryRepository.save(directory);
                directoryMapUndo.put(directory, directory1.get(0));
            } else {
                throw new Exception();
            }
        }
    }

    private void setCodeAndName(Criteria criteria, String value, Directory standard, Integer organizationId, Integer categoryId, String userFromToken) throws Exception {
        List<Directory> directories2 = null;
        List<String> listRoleCodeByUsername = rolesService.getListRolesCodeByUsername(userFromToken);

        if (listRoleCodeByUsername.contains("ADMIN") || listRoleCodeByUsername.contains("Super Admin")) {
            directories2 = directoryRepository.findByOrganizationIdAndUsername(organizationId, categoryId, null);
        } else {
            directories2 = directoryRepository.findByOrganizationIdAndUsername(organizationId, categoryId, userFromToken);
        }


        System.out.println("directories2 = " + directories2);
//          check xem có phân quyền chưa
        if (!directories2.isEmpty()) {
            for (Directory dir : directories2) {

                if (standard.getName().equals(dir.getName())) {
                    value = value.trim();
                    int index = value.indexOf(".");
                    int index_2 = value.indexOf(".", index + 1);
                    criteria.setName(index_2 > value.length() ? "" : value.substring(0, index_2));
                    criteria.setDescription(index_2 + 1 > value.length() ? "" : value.substring(index_2 + 1));
                    criteria.setStandardId(standard.getId());
                    //get code
                    List<String> listSplit = new ArrayList<String>(Arrays.asList(value.split(" ")));
                    String code = listSplit.get(2);
                    List<String> elements = new ArrayList<String>(Arrays.asList(code.split("\\.")));
                    String setCode = "";
                    if (elements.get(0).length() < 2) {
                        setCode += "0" + elements.get(0) + ".";
                    } else {
                        setCode += CommonUtil.trim0(elements.get(0)) + ".";
                    }
                    if (elements.get(1).length() < 2) {
                        setCode += "0" + elements.get(1);
                    } else {
                        setCode += CommonUtil.trim0(elements.get(1));
                    }
                    criteria.setCode(standard.getCode() + "." + setCode);
                    criteria.setDelete(ConstantDefine.IS_DELETED.FALSE);
                    criteria.setOrganizationId(organizationId);
                    criteria.setCategoryId(categoryId);
                }
//                else {
//                    throw new Exception();
//                }
            }
        } else {
            System.out.println("HHHH");
            throw new CheckDecentralizeException("The criteria is already exist");
        }
    }

    private void setNameEn(Criteria criteria, String value) {
        List<String> myList = new ArrayList<>(Arrays.asList(value.split(":")));
        if (!myList.isEmpty()) {
            for (int i = 0; i < myList.size(); i++) {
                switch (i) {
                    case 0:
                        criteria.setNameEn(myList.get(i).trim());
                        break;
                    case 1:
                        criteria.setDescriptionEn(myList.get(i).trim());
                        break;
                }
            }
        }
    }

    public void undoImport(UndoLog log) {
        List<UndoImport> undoImports = undoImportService.findByUndoIdAndStatusAndTableName(log.getId(), 0, TABLE_NAME);
        if (!undoImports.isEmpty()) {
            for (UndoImport undoImport : undoImports) {
                if (undoImport.getRevertObject().equals("null")) {
                    criteriaRepository.deleteById(undoImport.getIdRecord());
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
        Criteria criteria = CriteriaController.g.fromJson(undoImport.getRevertObject(), Criteria.class);
        criteriaRepository.save(criteria);
    }
}
