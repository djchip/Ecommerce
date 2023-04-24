package com.ecommerce.core.service.impl;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.controllers.ProofController;
import com.ecommerce.core.dto.ProofDTO;
import com.ecommerce.core.entities.*;
import com.ecommerce.core.exceptions.*;
import com.ecommerce.core.repositories.CriteriaRepository;
import com.ecommerce.core.service.*;
import com.ecommerce.core.util.CommonUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ecommerce.core.dto.CriteriaDTO;
import com.ecommerce.core.dto.DirectoryDTO;
import com.ecommerce.core.dto.ProgramsDTO;
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
public class ProofServiceImpl implements ProofService {

    private final static String PROOF_STANDARD = "Tiêu chuẩn";
    private final static String CRITERIA = "Tiêu chí";
    private static final Integer DELETED = 1;
    private static final Integer CAN_BE_DELETE = 0;
    private static final String TABLE_NAME = "proof";
    private static final Integer UN_ACTIVE_UNDO_LOG = 1;
    public static final String IMPORT = "IMPORT";
    @Autowired
    RolesService rolesService;

    @Autowired
    ProofRepository proofRepository;
    @Autowired
    ReadFileServiceImpl readFileService;
    @Autowired
    UndoLogService undoLogService;
    @Autowired
    CriteriaRepository criteriaRepository;
    @Autowired
    DirectoryRepository directoryRepository;
    @Autowired
    WorkbookService workbookService;
    @Autowired
    DirectoryService directoryService;
    @Autowired
    CriteriaService criteriaService;
    @Autowired
    UnitService unitService;
    @Autowired
    AppParamService paramService;
    @Autowired
    ProgramsService programsService;
    @Autowired
    AppParamService appParamService;
    @Autowired
    ExhibitionFileService exhibitionFileService;
    @Autowired
    UndoImportService undoImportService;
    @Autowired
    CriteriaServiceImpl criteriaServiceImpl;
    @Autowired
    DirectoryServiceImpl directoryServiceImpl;
    @Autowired
    UserInfoService userInfoService;

    @Override
    public Page<ProofDTO> doSearch(Integer programId, Integer standardId, Integer criteriaId, String username, Pageable pageable) {
        return proofRepository.doSearch(programId, standardId, criteriaId, username, pageable);
    }

    @Override
    public Page<ProofDTO> doSearchContent(Integer programId, Integer standardId, Integer criteriaId, String keyword, String username, Pageable pageable) {
        Page<Object[]> listData = proofRepository.doSearchContent(programId, standardId, criteriaId, keyword, username, pageable);
        List<ProofDTO> listProofDTO = new ArrayList<>();
        java.util.Date date = new java.util.Date();
        for (Object[] item : listData) {
            ProofDTO proofDTO = new ProofDTO();
            proofDTO.setId((Integer) item[0]);
            proofDTO.setProofCode((String) item[1]);
            proofDTO.setProofName((String) item[2]);
            proofDTO.setProofNameEn((String) item[3]);
            proofDTO.setDocumentType(item[4] != null ? Integer.parseInt(item[4].toString()) : null);
            proofDTO.setDocumentTypeName((String) item[5]);
            proofDTO.setDocumentTypeNameEn((String) item[6]);
            proofDTO.setNumberSign((String) item[7]);
            try {
                proofDTO.setReleaseDate(item[8] != null ? (LocalDateTime) item[8] : null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            proofDTO.setSigner((String) item[9]);

            proofDTO.setField(item[10] != null ? Integer.parseInt(item[10].toString()) : null);
            proofDTO.setFieldName((String) item[11]);
            proofDTO.setFieldNameEn((String) item[12]);
            proofDTO.setReleaseBy(item[13] != null ? Integer.parseInt(item[13].toString()) : null);
            proofDTO.setReleaseByName((String) item[14]);
            proofDTO.setReleaseByNameEn((String) item[15]);
            proofDTO.setDescription((String) item[16]);
            proofDTO.setDescriptionEn((String) item[17]);
            proofDTO.setNote((String) item[18]);
            proofDTO.setNoteEn((String) item[19]);
            proofDTO.setStandardName((String) item[20]);
            proofDTO.setStandardNameEn((String) item[21]);
            proofDTO.setCriteriaName((String) item[22]);
            proofDTO.setCriteriaNameEn((String) item[23]);
            proofDTO.setProgramName((String) item[24]);
            proofDTO.setProgramNameEn((String) item[25]);
            proofDTO.setCreatedBy((String) item[26]);
            proofDTO.setCreatedDate((Date) item[27]);
            proofDTO.setUpdatedBy((String) item[28]);
            proofDTO.setUpdatedDate((Date) item[29]);

            listProofDTO.add(proofDTO);
        }
        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), listProofDTO.size());

        final Page<ProofDTO> page = new PageImpl<>(listProofDTO.subList(start, end), pageable, listProofDTO.size());
        return page;
    }

    @Override
    public List<Proof> findByProofName(String proofName) {
        List<Proof> entity = proofRepository.findByProofName(proofName);
        if (entity != null) {
            return entity;
        }
        return null;
    }

    @Override
    public Proof findByProofCode(String proofCode) {
        Optional<Proof> entity = proofRepository.findByProofCode(proofCode);
        return entity.orElse(null);
    }


    @Transactional
    @Override
    public void importAutomatic(MultipartFile multipartFile, String createdBy, Integer organizationId, Integer programId, String forWhat, HttpServletRequest request) throws IOException, DetectExcelException, ExistsCriteria, ExistsDirectory, ExistsProofException, NotSameCodeException, InvalidProofCode, WrongFormat {
        Map<Proof, Proof> proofMap = new HashMap<>();
        Map<Criteria, Criteria> criteriaMap = new HashMap<>();
        Map<Directory, Directory> directoryMap = new HashMap<>();
        List<Criteria> criteria = new ArrayList<>();
        List<Directory> directories = new ArrayList<>();
        List<AppParam> params = paramService.findByOrganizationIdOrderByCreatedBy(organizationId);
        String CODE;
        boolean checkEncode;
        System.out.println("params = " + params);
        if (!params.isEmpty()) {
            CODE = params.get(0).getName();
            checkEncode = params.get(0).isEnCode();
        } else {
            throw new IllegalStateException();
        }
        // Get workbook
        Workbook workbook = workbookService.getWorkbook(multipartFile.getInputStream(), Objects.requireNonNull(multipartFile.getOriginalFilename()));
        // Get sheet
        Sheet sheet = workbook.getSheetAt(0);
        // Get all rows
        int indexDirectory = 0;
        int indexCriteria = 0;
        List<UserInfo> userInfos = new ArrayList<>();
        userInfos.add(userInfoService.findByUsername(createdBy));
        String checkCode = null;


        for (Row nextRow : sheet) {
            if (nextRow.getRowNum() == 0 || nextRow.getRowNum() == 1) {
                // Ignore header
                continue;
            }
            // Get all cells
            Iterator<Cell> cellIterator = nextRow.cellIterator();
            // Read cells and set value
            Proof proof = new Proof();
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
                    case 0:
                        Criteria criteria1 = new Criteria();
                        value = workbookService.getCellValue(cell) + "";
                        if (value.contains(CRITERIA)) {
                            createCriteria(value, criteria1, createdBy);
                            criteria1.setOrganizationId(organizationId);
                            criteria1.setUserInfos(userInfos);
                            criteria.add(criteria1);
                            indexCriteria++;
                        }
                        break;
                    case 1:
                        Directory directory = new Directory();
                        value = (String) workbookService.getCellValue(cell);

                        List<Directory> directories2 = null;
                        List<String> listRoleCodeByUsername = rolesService.getListRolesCodeByUsername(createdBy);

                        if (listRoleCodeByUsername.contains("ADMIN") || listRoleCodeByUsername.contains("Super Admin")) {
                            directories2 = directoryService.findByOrganizationIdAndUsername(organizationId, null);
                        } else {
                            directories2 = directoryService.findByOrganizationIdAndUsername(organizationId, createdBy);
                        }

                        if (value.contains(PROOF_STANDARD) ) {
                            List<String> myList = new ArrayList<String>(Arrays.asList(value.split(":")));
                            List<String> strings = new ArrayList<String>(Arrays.asList(myList.get(0).trim().split(" ")));
                            String Code = strings.get(2).trim().replaceAll("\\.", "");
                            System.out.println("value = " + value);
                            System.out.println("myList = " + myList);
                            System.out.println("strings = " + strings);
                            System.out.println("Code = " + Code);
                            List<String> test = new ArrayList<String>(Arrays.asList(value.split(":")));

                            if (checkEncode == true) {
                                Code = "0" + Code;
                            } else {
                                Code = Code;
                            }

                            checkCode = (CODE + Code);

                            if (!directories2.isEmpty()) {
                                for (Directory dir : directories2) {
                                    if (value.contains(dir.getName())) {


                                        createStandard(value, directory, createdBy);
                                        directory.setOrganizationId(organizationId);
                                        directory.setUserInfos(userInfos);
                                        directories.add(directory);
                                        indexDirectory++;
                                    }
                                }
                            }
                        }
                        else {
                            if (hasNotCodeDirectory(directories, indexDirectory)) {
                                int index = value.indexOf(".");
                                Directory dir = directories.get(indexDirectory - 1);
                                String code = getCodeStandard(value);
                                if (CODE.equals(code)) {
//                                    if(checkEncode == true){
//                                        code= code + 0;
//                                    } else {
//                                        code= code;
//                                    }
//                                    System.out.println("code = " + code);
//                                    System.out.println("CODE = " + CODE);
//                                    System.out.println("checkEncode = " + checkEncode);
//                                    System.out.println("CommonUtil.trim0(value.substring(code.length(), index)) = " + CommonUtil.trim0(value.substring(code.length(), index)));
//                                    System.out.println("dir = " + dir);
                                    String checkCodeinput = code + CommonUtil.trim0(value.substring(code.length(), index));
                                    System.out.println("checkCodeinput = " + checkCodeinput);
                                    System.out.println("checkCode = " + checkCode);

                                    if (checkCode.equals(checkCodeinput)) {
                                        dir.setCode(checkCodeinput);
                                        System.out.println("dir = " + dir.getCode());
                                    } else {
                                        throw new WrongFormat("Wrong format");

                                    }
                                    System.out.println("dir = " + dir);

                                } else {
                                    throw new NotSameCodeException("Data does not match the Directory code");
                                }
                                dir.setDelete(CAN_BE_DELETE);
                                List<Directory> directoriesCheck = directoryRepository.findByCodeAndOrganizationIdAndDelete(dir.getCode(), dir.getOrganizationId(), 0);
                                if (directoriesCheck.isEmpty()) {
                                    dir.setCreatedDate(new Date());
                                    dir.setCreate_by(createdBy);
                                    directoryMap.put(dir, null);
                                } else if (directoriesCheck.size() > 1) {
                                    throw new ExistsDirectory("Directory already exist with code: "
                                            + dir.getCode() + " organizationId: " + dir.getOrganizationId());
                                } else {
                                    setStandard(dir, directoriesCheck.get(0), createdBy);
                                    directoryMap.put(dir, directoriesCheck.get(0));
                                }
                                directories.set(indexDirectory - 1, directoryRepository.save(dir));
                            }
                            if (hasNotCodeCriteria(criteria, indexCriteria)) {
                                List<String> listValue = new ArrayList<String>(Arrays.asList(value.split("\\.")));
                                if (listValue.size() != 4) {
                                    throw new InvalidProofCode("Invalid proof code");
                                }
                                Criteria cri = criteria.get(indexCriteria - 1);
                                cri.setCode(directories.get(indexDirectory - 1).getCode() + "." + CommonUtil.trim0(listValue.get(1)) + "." + CommonUtil.trim0(listValue.get(2)));
                                cri.setStandardId(directories.get(indexDirectory - 1).getId());
                                cri.setDelete(CAN_BE_DELETE);
                                List<Criteria> criteriaList = criteriaRepository.findByCodeAndOrganizationIdAndStandardIdAndDelete(cri.getCode(), cri.getOrganizationId(), cri.getStandardId(), 0);
                                if (criteriaList.isEmpty() && cri.getCode() != null) {
                                    cri.setCreate_by(createdBy);
                                    cri.setCreatedDate(new Date());
                                    criteriaMap.put(cri, null);
                                } else if (criteriaList.size() > 1) {
                                    throw new ExistsCriteria("Criteria already exist with code: "
                                            + cri.getCode() + " OrganizationId: " + cri.getOrganizationId() + " standardId: " + cri.getStandardId());
                                } else if (criteriaList.size() == 1 && cri.getCode() != null) {
                                    setCriteria(cri, criteriaList.get(0), createdBy);
                                    criteriaMap.put(cri, criteriaList.get(0));
                                }
                                criteria.set(indexCriteria - 1, criteriaRepository.save(cri));
                            }


                            if (forWhat.equals("standard") && criteria.isEmpty()) {
                                throw new WrongFormat("Wrong format");
                            } else if (forWhat.equals("criteria") && criteria.size() > 0) {
                                throw new WrongFormat("Wrong format");
                            }
                            if (forWhat.equals("standard") && criteria.get(indexCriteria - 1).getCode() != null) {
                                proof.setProofCode(criteria.get(indexCriteria - 1).getCode() + "." + value.substring(value.lastIndexOf(".") + 1));
                                proof.setCriteriaId(criteria.get(indexCriteria - 1).getId());
                            } else if (forWhat.equals("criteria")) {
                                proof.setProofCode(directories.get(indexDirectory - 1).getCode() + "." + value.substring(value.lastIndexOf(".") + 1));
                            }
                            proof.setStandardId(directories.get(indexDirectory - 1).getId());
                        }
                        break;
                    case 3:
                        proof.setProofName(String.valueOf(workbookService.getCellValue(cell)));
                        break;
                    case 4:
                        proof.setProofNameEn(String.valueOf(workbookService.getCellValue(cell)));
                        break;
                    default:
                        break;
                }
            }
            if (proof.getProofCode() != null && !proof.getProofCode().isEmpty()) {
                List<Proof> proofList = proofRepository.findByProgramIdAndStandardIdAndProofCodeAndDeleted(programId,
                        proof.getStandardId(), proof.getProofCode(), 0);
                if (proofList.isEmpty()) {
                    proof.setCreatedDate(new Date());
                    proof.setCreatedBy(createdBy);
                    proof.setUpdatedDate(new Date());
                    proof.setProgramId(programId);
                    proof.setDeleted(CAN_BE_DELETE);
                    proof.setStatus(CAN_BE_DELETE);
                    proofMap.put(proof, null);
                } else if (proofList.size() > 1) {
                    throw new ExistsProofException("Proof already exist with code: "
                            + proof.getProofCode() + " programId: " + proof.getProgramId() + " standardId: "
                            + proof.getStandardId() + " criteriaId: " + proof.getCriteriaId());
                } else {
                    setProof(proof, proofList.get(0), programId, createdBy);
                    proofMap.put(proof, proofList.get(0));
                }
            }
        }
        workbook.close();
        proofRepository.saveAll(proofMap.keySet());
        UndoLog undoLog = insertUndo(proofMap, createdBy, request);
        if (forWhat.equals("standard")) {
            criteriaServiceImpl.insertUndo(criteriaMap, createdBy, false, undoLog, request);
        }
        directoryServiceImpl.insertUndo(directoryMap, createdBy, false, undoLog, request);
    }

    private UndoLog insertUndo(Map<Proof, Proof> proofMap, String createdBy, HttpServletRequest request) {
        UndoLog undoLog = undoLogService.create(UndoLog.undoLogBuilder()
                .requestObject(ProofController.g.toJson(proofMap.keySet()))
                .status(ConstantDefine.STATUS.UNDO_NEW)
                .url(request.getRequestURL().toString())
                .description("Import minh chứng bởi " + createdBy)
                .createdDate(LocalDateTime.now())
                .createdBy(createdBy)
                .tableName(TABLE_NAME)
                .action(IMPORT)
                .build());
        for (Map.Entry<Proof, Proof> entry : proofMap.entrySet()) {
            undoImportService.insertImportUndo(entry, undoLog.getId(), entry.getKey().getId(), createdBy, ProofController.g, TABLE_NAME);
        }
        return undoLog;
    }

    @Override
    public Proof create(Proof entity) {
        return proofRepository.save(entity);
    }

    @Override
    public Proof retrieve(Integer id) {
        Optional<Proof> entity = proofRepository.findById(id);
        return entity.orElse(null);
    }

    @Override
    public void update(Proof entity, Integer id) {
        proofRepository.save(entity);
    }

    @Override
    public void delete(Integer id) {
        proofRepository.deleteById(id);
    }

    @Override
    public List<Proof> getProof() {
        return proofRepository.findByDeletedNot(DELETED);
    }

    private void createCriteria(String value, Criteria criteria, String createdBy) {
        int index = value.indexOf(".");
        criteria.setName(value.substring(0, index + 2));
        criteria.setDescription(value.substring(index + 3, value.length()));
    }

    private void createStandard(String value, Directory standard, String createdBy) {
        int index = value.indexOf(":");
        standard.setName(value.substring(0, index));
        standard.setDescription(value.substring(index + 2, value.length()));
    }

    private boolean hasNotCodeDirectory(List<Directory> directories, int indexDirectory) {
        return directories.size() > 0 && directories.get(indexDirectory - 1).getCode() == null;
    }

    private boolean hasNotCodeCriteria(List<Criteria> criteria, int indexCriteria) {
        return criteria.size() > 0 && criteria.get(indexCriteria - 1).getCode() == null;
    }

    @Override
    public List<Proof> getAllProofNeedFile(String proofCode, Integer programId) {
        // TODO Auto-generated method stub
        return proofRepository.getAllProofNeedFile(proofCode, programId);
    }

    @Override
    public void updateExhFile(Integer id, Integer proofId) {
        proofRepository.updateExhFile(id, proofId);
    }

    @Override
    public Integer getMaxOrderOfStandard(Integer stanId) {
        return proofRepository.getMaxOrderOfStandard(stanId);
    }

    @Override
    public Integer getMaxOrderOfCriteria(Integer criId) {
        return proofRepository.getMaxOrderOfCriteria(criId);
    }

    @Override
    public Proof findById(int id) {
        return proofRepository.findById(id).get();
    }

    @Override
    public List<Proof> findAll() {
        return proofRepository.findAll();
    }

    @Override
    public List<Proof> findByStandardId(Integer id) {
        return proofRepository.findByStandardIdAndDeletedNot(id, 1);
    }

    @Override
    public List<Proof> findByCriteriaId(Integer id) {
        return proofRepository.findByCriteriaId(id);
    }

    @Override
    public Unit getUnitByUsername(String creater) {
        return proofRepository.getUnitByUsername(creater);
    }

    @Override
    public Proof deleteProof(Integer id) {
        Optional<Proof> optional = proofRepository.findById(id);
        if (optional.isPresent()) {
            Proof proof = optional.get();
            proof.setStatus(1);
            return proofRepository.save(proof);
        }
        return null;
    }

    @Override
    public ProofDTO getDetailProof(Integer id) {
        return proofRepository.getDetailProof(id);
    }

    @Override
    public List<Proof> findByUnit(Integer unitId) {
        return proofRepository.findByReleaseByAndDeletedNot(unitId, ConstantDefine.IS_DELETED.TRUE);
    }

    @Override
    public Proof deleteProoff(Integer id) throws Exception {
        Optional<Proof> optional = proofRepository.findById(id);
        if (optional.isPresent()) {
            Proof proof = optional.get();
            proof.setDeleted(DELETED);
            proof.setUndoStatus(ConstantDefine.STATUS.CAN_BE_UNDO);
            return proofRepository.save(proof);
        } else {
            throw new Exception();
        }
    }


    private Proof undoPut(UndoLog undoLog) {
        Gson g = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return g.fromJson(undoLog.getRevertObject(), Proof.class);
    }

    @Transactional
    @Override
    public void undo(UndoLog undoLog) throws Exception {
        List<UndoLog> undoLogs = undoLogService.findByTableNameAndIdRecordAndStatusNotOrderByCreatedDateDesc(TABLE_NAME, undoLog.getIdRecord(), 1);
        for (UndoLog log : undoLogs) {
            if (log.getId().equals(undoLog.getId())) {
                Proof proof = null;
                if (!log.getAction().equals("IMPORT")) {
                    Optional<Proof> optional = proofRepository.findById(log.getIdRecord());
                    if (optional.isPresent()) {
                        proof = optional.get();
                    } else {
                        throw new Exception();
                    }
                }
                switch (log.getAction()) {
                    case "POST":
                        proofRepository.deleteById(proof.getId());
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                    case "DELETE":
                        if (!proofRepository.findByProofNameAndDeleted(proof.getProofName(), 0).isEmpty()) {
                            throw new ExistsCriteria("minh chứng đã tồn tại với mã: "
                                    + proof.getProofCode());
                        } else {
                            proof.setDeleted(CAN_BE_DELETE);
                            log.setStatus(UN_ACTIVE_UNDO_LOG);
                            undoLogService.update(log, log.getId());
                            break;
                        }
                    case "PUT":
                        proof = undoPut(log);
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                    case "IMPORT":
                        undoImport(log);
                        criteriaServiceImpl.undoImport(log);
                        directoryServiceImpl.undoImport(log);
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                }
                if (!log.getAction().equals("POST") && !log.getAction().equals("IMPORT")) {
                    assert proof != null;
                    proofRepository.save(proof);
                }
                break;
            } else {
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
                    proofRepository.deleteById(undoImport.getIdRecord());
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
        Proof proof = ProofController.g.fromJson(undoImport.getRevertObject(), Proof.class);
        proofRepository.save(proof);
    }

    @Override
    public ProofDTO formatObj(Proof entity) {
        ProofDTO dto = new ProofDTO(
                entity.getId(), entity.getProofCode(), entity.getProofName(), entity.getProofNameEn(),
                entity.getDocumentType(), null, null, entity.getNumberSign(), entity.getReleaseDate(),
                entity.getSigner(), entity.getField(), null, null, entity.getReleaseBy(), null, null, entity.getDescription(), entity.getDescriptionEn(),
                entity.getNote(), entity.getNoteEn(), null, null, null, null, null, null, entity.getCreatedBy(), entity.getCreatedDate(),
                entity.getUpdatedBy(), entity.getUpdatedDate(), null, null
        );
//        Optional<DirectoryDTO> directoryDTO = directoryService.finbyID(entity.getStandardId());
//        directoryDTO.ifPresent(value -> dto.setStandardName(value.getName()));

//        Optional<CriteriaDTO> criteriaDTO = criteriaService.finbyID(entity.getCriteriaId());
//        criteriaDTO.ifPresent(value -> dto.setCriteriaName(value.getName()));

        Optional<ProgramsDTO> programsDTO = programsService.finbyID(entity.getProgramId());
        if (programsDTO.isPresent()) {
            dto.setProgramName(programsDTO.get().getName());
            dto.setProgramNameEn(programsDTO.get().getNameEn());
        }
        Optional<DirectoryDTO> directoryDTO = directoryService.finbyID(entity.getStandardId());
        if (directoryDTO.isPresent()) {
            DirectoryDTO directoryDTO1 = directoryDTO.get();
            dto.setStandardName(directoryDTO1.getName());
        }
        if (entity.getCriteriaId() != null) {
            Optional<CriteriaDTO> criteriaDTO = criteriaService.finbyID(entity.getCriteriaId());
            if (criteriaDTO.isPresent()) {
                CriteriaDTO criteriaDTO1 = criteriaDTO.get();
                dto.setCriteriaName(criteriaDTO1.getName());
                System.out.println("dto.  tieu chis= " + criteriaDTO1.toString());
            }
        }
        if (entity.getReleaseBy() != null) {
            Optional<Unit> unit = unitService.finbyID(entity.getReleaseBy());


            if (unit.isPresent()) {
                dto.setReleaseByName(unit.get().getUnitName());
                dto.setReleaseByNameEn(unit.get().getUnitNameEn());
                System.out.println("dto.  unit= " + unit.toString());
            }
        }
        if (entity.getField() != null) {
            Optional<ExhibitionFile> exhibitionFile = exhibitionFileService.finbyID(entity.getField());
            if (exhibitionFile.isPresent()) {
                dto.setFieldName(exhibitionFile.get().getFileName());
//            dto.setReleaseByNameEn(exhibitionFile.get().getUnitNameEn());
                System.out.println("dto.  exhibitionFile= " + exhibitionFile.toString());
            }
        }

//        Optional<AppParamDTO> appParamDTO = appParamService.finbyID(entity.getField());
//        if (appParamDTO.isPresent()) {
//            dto.setFieldName(appParamDTO.get().getName());
//            dto.setFieldNameEn(appParamDTO.get().getNameEn());
//            System.out.println("dto.  appp= " + appParamDTO.toString());
//        }
        return dto;
    }

    @Override
    public List<Proof> findByProId(Integer proId) {
        return proofRepository.findByProgramIdAndDeletedNot(proId, DELETED);
    }

    @Override
    public Page<Proof> getProofHaveNotFile(Integer programId, Pageable paging) {
        return proofRepository.findByProgramIdAndExhFileIsNullAndDeletedOrderByUpdatedDateDesc(programId, 0, paging);
    }

    @Override
    public void saveAll(List<Proof> proofs) {
        proofRepository.saveAll(proofs);
    }

    @Transactional
    @Override
    public boolean deleteMultiProof(Integer[] ids, Gson g, String userFormToken, HttpServletRequest request) throws Exception {
        for (int id : ids) {
            Proof proof = deleteProoff(id);
            if (proof == null) {
                return false;
            }
            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .requestObject(g.toJson(proof, Proof.class))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Xóa minh chứng  " + proof.getProofName() + " bởi " + userFormToken)
                    .createdDate(LocalDateTime.now())
                    .createdBy(userFormToken)
                    .tableName(TABLE_NAME)
                    .idRecord(proof.getId())
                    .build();
            undoLogService.create(undoLog);
        }
        return true;
    }

    public void updateExhCode(String oldExhCode, String newExhCode, int orgId) {
        proofRepository.updateExhCode(oldExhCode, newExhCode, orgId);
    }

    @Override
    public List<ProofDTO> formatObj(List<Proof> proofs) {
        List<ProofDTO> dtos = new ArrayList<>();
        for (Proof proof : proofs) {
            ProofDTO dto = new ProofDTO();
            dto.setProofName(proof.getProofName());
            dto.setProofNameEn(proof.getProofNameEn());
            dto.setProofCode(proof.getProofCode());
            if (proof.getCriteriaId() != null) {
                Criteria criteria = criteriaService.retrieve(proof.getCriteriaId());
                dto.setCriteriaName(criteria.getName());
                dto.setCriteriaNameEn(criteria.getNameEn());
            }
            if (proof.getStandardId() != null) {
                Directory directory = directoryService.retrieve(proof.getStandardId());
                dto.setDirName(directory.getName());
                dto.setDirNameEn(directory.getNameEn());
            }
            if (proof.getProgramId() != null) {
                Programs programs = programsService.retrieve(proof.getProgramId());
                dto.setProgramName(programs.getName());
                dto.setProgramNameEn(programs.getNameEn());
            }
            dtos.add(dto);
//            dtos.sort(Comparator.comparing(ProofDTO::getUpdatedDate).reversed());
        }

        return dtos;
    }

    @Override
    public List<Proof> getListProofByProgramId(int id) {
        return proofRepository.getListProofByProgramId(id);
    }

    @Override
    public List<Proof> findByOrganizationId(Integer id) {
        return proofRepository.findByOrganizationId(id);
    }

    String getCodeStandard(String value) {
        if (value.isEmpty()) {
            return "";
        }
        StringBuilder code = new StringBuilder();
        for (Character c : value.toCharArray()) {
            if (!Character.isDigit(c)) {
                code.append(c.toString());
            } else {
                break;
            }
        }
        return code.toString();
    }

    void setProof(Proof proof, Proof proofList, Integer programId, String createdBy) {
        proof.setDeleted(0);
        proof.setCreatedDate(proofList.getCreatedDate());
        proof.setCreatedBy(proofList.getCreatedBy());
        proof.setProgramId(programId);
        proof.setUpdatedDate(new Date());
        proof.setUpdatedBy(createdBy);
        proof.setExhFile(proofList.getExhFile());
        proof.setStatus(proofList.getStatus());
        proof.setDescription(proofList.getDescription());
        proof.setDescriptionEn(proofList.getDescriptionEn());
        proof.setDocumentType(proofList.getDocumentType());
        proof.setField(proofList.getField());
        proof.setNote(proofList.getNote());
        proof.setNoteEn(proofList.getNoteEn());
        proof.setId(proofList.getId());
        proof.setNumberSign(proofList.getNumberSign());
        proof.setOldProofCode(proofList.getOldProofCode());
        proof.setReleaseBy(proofList.getReleaseBy());
        proof.setSigner(proofList.getSigner());
        proof.setUnit(proofList.getUnit());
        proof.setReleaseDate(proofList.getReleaseDate());
        proof.setUploadedDate(proofList.getUploadedDate());
        proof.setListExhFile(proofList.getListExhFile());
        proof.setOrderOfCriteria(proofList.getOrderOfCriteria());
        proof.setOrderOfStandard(proofList.getOrderOfStandard());
    }

    void setStandard(Directory dir, Directory index_0, String createdBy) {
        dir.setCreatedDate(index_0.getCreatedDate());
        dir.setCreate_by(index_0.getCreate_by());
        dir.setUpdatedDate(new Date());
        dir.setUpdate_by(createdBy);
        dir.setNameEn(index_0.getNameEn());
        dir.setDescriptionEn(index_0.getDescriptionEn());
        dir.setId(index_0.getId());
        dir.setOrderDir(index_0.getOrderDir());
    }

    private void setCriteria(Criteria cri, Criteria criteria, String createdBy) {
        cri.setId(criteria.getId());
        cri.setUpdatedDate(new Date());
        cri.setUpdate_by(createdBy);
        cri.setCreatedDate(criteria.getCreatedDate());
        cri.setCreate_by(criteria.getCreate_by());
        cri.setNameEn(criteria.getNameEn());
        cri.setDescriptionEn(criteria.getDescriptionEn());
    }
}
