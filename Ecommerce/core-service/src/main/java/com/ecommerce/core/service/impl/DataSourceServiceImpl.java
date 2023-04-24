package com.ecommerce.core.service.impl;

import com.ecommerce.core.constants.TypeEnum;
import com.ecommerce.core.entities.DataSource;
import com.ecommerce.core.entities.Form;
import com.ecommerce.core.entities.UserInfo;
import com.ecommerce.core.exceptions.DetectExcelException;
import com.ecommerce.core.repositories.DataSourceRepository;
import com.ecommerce.core.service.*;
import com.ecommerce.core.util.FileUploadUtil;
import com.ecommerce.core.dto.DataLabelDTO;
import com.ecommerce.core.dto.DataSourceDTO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DataSourceServiceImpl implements DataSourceService {

    private final int IS_ACTIVE = 1;
    private final int IS_NOT_ACTIVE = 0;

//    private final String PATH_FILE = "D:\\HocVien\\hoc-vien-nong-nghiep\\DocumentManagement\\neo-frontend\\src\\assets\\form-file";

//    private final String PATH_FILE = "/var/www/html/vuexy/assets/form-file";
//    private final String PATH_FILE_CSDL = "D:\\HocVien\\hoc-vien-nong-nghiep\\DocumentManagement\\neo-frontend\\src\\assets\\form-file\\CSDL\\";


    @Value("${form-path}")
    private String PATH_FILE;
    @Value("${csdl-path}")
    private String PATH_FILE_CSDL;

    @Autowired
    WorkbookService workbookService;

    @Autowired
    DataSourceRepository repository;

    @Autowired
    ReadFileService readFileService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    FormService formService;

    @Transactional
    @Override
    public DataSourceDTO importDataSource(MultipartFile file, String createdBy, Integer rowHeaderNumber, Boolean isForm,
                                          String formName, Integer year, Integer formKey,Integer startTitle, HttpServletRequest request) throws IOException, DetectExcelException {
        List<DataSource> list = new ArrayList<>();
        Map<Integer, DataSourceDTO> map = new HashMap<>();
        // Get workbook
        Workbook workbook = workbookService.getWorkbook(file.getInputStream(), Objects.requireNonNull(file.getOriginalFilename()));
        // Get form key
        if (formKey == null && isForm) { // them moi Bieu mau
            formKey = formService.getMaxFormKey();
            if (formKey == null){
                formKey = 1;
            }else {
                formKey += 1;
            }
        }
        if(formKey != null && isForm){
            repository.deleteByFormKey(formKey);
        }
//        else if (formKey != null && isForm) { // chinh sua bieu mau

//        } else if (formKey != null && !isForm) { // csdl

//        }
        UserInfo userInfo = userInfoService.findByUsername(createdBy);

        System.out.println("FORM KEY " + formKey);
        if (isForm && formKey != null) {
            deleteDataOfDatabase(formKey, userInfo.getUnit().getId(), TypeEnum.LABEL);
        } else if (!isForm) {
            deleteDataOfDatabase(formKey, userInfo.getUnit().getId(), TypeEnum.LABEL);
        }
        // Get sheet
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
                if (rowNum < startTitle - 1) {
                    // Ignore header
                    continue;
                }
                // Label Form
                if (isForm && rowNum > startTitle - 1 + rowHeaderNumber - 1) {
                    break;
                }
                // CSDL
                if (!isForm) {
                    if (rowNum < startTitle - 1 + rowHeaderNumber) {
                        continue;
                    }
                }
                TypeEnum typeEnum = null;
                if (rowNum < startTitle - 1 + rowHeaderNumber) {
                    typeEnum = TypeEnum.LABEL;
                } else {
                    typeEnum = TypeEnum.DATA;
                }
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                int lastColumn = row.getLastCellNum();
                for (int columnNum = 0; columnNum < lastColumn; columnNum++) {
                    Cell cell_ = row.getCell(columnNum, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    System.out.println(cell_ + "\n");
                     if (cell_ != null) {
                        DataSource dataSource = new DataSource();
                        dataSource.setValue(detectCellValue(cell_, map, columnNum));
                        setDataSource(dataSource, sheet.getSheetName(), createdBy, rowNum, columnNum, typeEnum, year, formKey, userInfo.getUnit().getId());
                        list.add(dataSource);
                    }
                }
            }
        }
        workbook.close();
        repository.saveAll(list);
        String fileExtension = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        String pathFile_ = "";
        if(isForm){
            pathFile_= PATH_FILE + "/" + formKey + "/" + formName;
        }else {
            pathFile_ = PATH_FILE_CSDL + "/" + formKey + "_" + userInfo.getUnit().getId() + "/" + formName;
        }

//        file.transferTo(new File(pathFile_));
//        String fileName = StringUtils.cleanPath(Objects.requireNonNull(formName));
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        FileUploadUtil.saveFiles(pathFile_, fileName, file);
        return setDataSourceDTO(fileName, pathFile_, rowHeaderNumber, formKey, startTitle);
    }

    @Override
    public Form importReport(MultipartFile file, Integer rowStart, Integer rowHeader, String userName) throws IOException, DetectExcelException {
        List<DataSource> list = new ArrayList<>();
        Form form = new Form();
        Map<Integer, DataSourceDTO> map = new HashMap<>();
        UserInfo userInfo = userInfoService.findByUsername(userName);

        Workbook workbook = workbookService.getWorkbook(file.getInputStream(), Objects.requireNonNull(file.getOriginalFilename()));
        Integer formKey = formService.getMaxFormKey();
        if (formKey == null){
            formKey = 1;
        }else {
            formKey += 1;
        }

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
                if (rowNum < rowStart-1) {
                    // Ignore header
                    continue;
                }
                // Label Form
                if (rowNum > rowStart + rowHeader - 2) {
                    break;
                }

                TypeEnum typeEnum = null;
                if (rowNum < rowStart + rowHeader - 1) {
                    typeEnum = TypeEnum.LABEL;
                }
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                int lastColumn = row.getLastCellNum();
                for (int columnNum = 0; columnNum < lastColumn; columnNum++) {
                    Cell cell_ = row.getCell(columnNum, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (cell_ != null) {
                        DataSource dataSource = new DataSource();
                        dataSource.setValue(detectCellValue(cell_, map, columnNum));
                        setDataSource(dataSource, sheet.getSheetName(), userName, rowNum, columnNum, typeEnum, rowStart, formKey, userInfo.getUnit().getId());
                        list.add(dataSource);
                    }
                }
            }
        }

        workbook.close();
        String pathFile_= PATH_FILE + "/" + formKey;
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        form.setName(file.getOriginalFilename());
        form.setFileName(file.getOriginalFilename());
        form.setCreateBy(userName);
        form.setFormKey(formKey);
        form.setYearOfApplication(rowStart);
        form.setNumTitle(rowHeader);
        form.setPathFile(pathFile_ + "/" + fileName);
        form.setStatus(0);
        form.setDeleted(1);
        repository.saveAll(list);
        formService.save(form);

        FileUploadUtil.saveFiles(pathFile_, fileName, file);
        return form;
    }

    @Override
    public DataSource getValueCol(TypeEnum typeEnum, Integer id, Integer unitId) {
        return repository.getValueCol(typeEnum, id, unitId);
    }

    @Override
    public String getMaxValue(Integer id) {
        return repository.getMaxValue(id);
    }

    @Override
    public String getMinValue(Integer id) {
        return repository.getMinValue(id);
    }

    private void deleteDataOfDatabase(Integer formKey, Integer unitId, TypeEnum type) {
        repository.deleteByFormKeyAndCreatedUnitAndTypeIsNot(formKey, unitId, type);
    }

    @Override
    public void deleteDatabase(Integer formKey) {
        repository.deleteByFormKeyAndType(formKey, TypeEnum.DATA);
    }

    @Override
    public void deleteForm(Integer formKey) {
        repository.deleteByFormKey(formKey);
    }

    @Override
    public List<DataSource> getLabel(List<Integer> listId) {
        return repository.findByTypeAndFormKeyIn(TypeEnum.LABEL, listId);
    }

    @Override
    public List<DataSource> getList(Integer key, Integer unitId, Integer rowIdx) {
        return repository.getList(key, unitId, rowIdx);
    }

    @Override
    public Integer getMinRow(List<Integer> key, Integer unitId) {
        return repository.getMinRow(key, unitId);
    }

    @Override
    public Integer getMaxRow(List<Integer> key, Integer unitId) {
        return repository.getMaxRow(key, unitId);
    }

    @Override
    public List<DataLabelDTO> getLabelByKey(Integer key) {
        List<Object[]> objects = repository.getLabelByKey(key);
        List<DataLabelDTO> labelDTOS = new ArrayList<>();
        for (Object[] item: objects){
            DataLabelDTO labelDTO = new DataLabelDTO();
            labelDTO.setId((Integer) item[0]);
            labelDTO.setValue((String) item[1]);
            labelDTO.setColIdx((Integer) item[2]);
            labelDTOS.add(labelDTO);
        }
        return labelDTOS;
    }

    @Override
    public Integer countRow(Integer key, Integer unitId) {
        return repository.countRow(key, unitId);
    }

    @Override
    public DataSource getCellValue(Integer key, Integer unitId, Integer colId, Integer rowId) {
        return repository.getValueCell(key, unitId, colId, rowId);
    }

    @Override
    public List<Integer> getListRow(List<Integer> listKey, Integer unitId) {
        return repository.getListRow(listKey, unitId);
    }

    private DataSourceDTO setDataSourceDTO(String fileName, String pathFile_, Integer
            rowHeaderNumber, Integer formKey, Integer startTitle) {
        DataSourceDTO sourceDTO = new DataSourceDTO();
        sourceDTO.setPathFile(pathFile_ + "/" + fileName);
        sourceDTO.setNumTitle(rowHeaderNumber);
        sourceDTO.setFormKey(formKey);
        sourceDTO.setStartTitle(startTitle);
        return sourceDTO;
    }

    private DataSource setResultDataSource(Sheet sheet, String createdBy, int rowNum, int columnNum, TypeEnum
            typeEnum,
                                           String value, Integer year, Integer formKey, Integer unitId) {
        DataSource dataSource = new DataSource();
        dataSource.setFormName(sheet.getSheetName());
        dataSource.setYear(year);
        dataSource.setCreatedBy(createdBy);
        dataSource.setCreatedDate(LocalDateTime.now());
        dataSource.setCreatedUnit(unitId);
        dataSource.setRowIdx(rowNum);
        dataSource.setColIdx(columnNum);
        dataSource.setIsActive(IS_ACTIVE);
        dataSource.setType(typeEnum);
        dataSource.setValue(value);
        dataSource.setFormKey(formKey);
        return dataSource;
    }

    private void setDataSource(DataSource dataSource, String sheetName, String createdBy, int rowNum, int columnNum,
                               TypeEnum typeEnum, Integer year, Integer formKey, Integer unitId) {
        dataSource.setFormName(sheetName);
        dataSource.setYear(year);
        dataSource.setCreatedBy(createdBy);
        dataSource.setCreatedDate(LocalDateTime.now());
        dataSource.setCreatedUnit(unitId);
        dataSource.setRowIdx(rowNum);
        dataSource.setColIdx(columnNum);
        dataSource.setIsActive(IS_ACTIVE);
        dataSource.setType(typeEnum);
        dataSource.setFormKey(formKey);
    }

    /**
     * Get the index of the merged cell in all the merged cells
     * if the given cell is in a merged cell.
     * Otherwise, it will return null.
     *
     * @param sheet  The Sheet object
     * @param row    The row number of this cell
     * @param column The column number of this cell
     * @return The index of all merged cells, which will be useful for {@link Sheet#getMergedRegion(int)}
     */
    private Integer getIndexIfCellIsInMergedCells(Sheet sheet, int row, int column) {
        int numberOfMergedRegions = sheet.getNumMergedRegions();
        for (int i = 0; i < numberOfMergedRegions; i++) {
            CellRangeAddress mergedCell = sheet.getMergedRegion(i);
            if (mergedCell.isInRange(row, column)) {
                return i;
            }
        }
        return null;
    }

    /**
     * Get the value from a merged cell
     *
     * @param sheet       The Sheet object
     * @param mergedCells The {@link CellRangeAddress} object fetched from {@link Sheet#getMergedRegion(int)} method
     * @return The content in this merged cell
     */
    private String readContentFromMergedCells(Sheet sheet, CellRangeAddress mergedCells) {
        if (mergedCells.getFirstRow() != mergedCells.getLastRow()) {
            return null;
        }
        return sheet.getRow(mergedCells.getFirstRow()).getCell(mergedCells.getFirstColumn()).getStringCellValue();
    }

    private String detectCellValue(Cell cell, Map<Integer, DataSourceDTO> map, int columnNum) {
        if (cell == null) {
            return "";
        }
        String cellValue;
        switch (cell.getCellType()) {
            case STRING:
                cellValue = cell.getStringCellValue();
                break;

            case FORMULA:
                Double formula = cell.getNumericCellValue();
                cellValue = Double.toString(formula);
                addSumColumn(map, columnNum, formula);
                break;

            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    cellValue = cell.getDateCellValue().toString();
                } else {
                    Double aDouble = cell.getNumericCellValue();
                    cellValue = Double.toString(aDouble);
                    addSumColumn(map, columnNum, aDouble);
                }
                break;

            case BOOLEAN:
                cellValue = Boolean.toString(cell.getBooleanCellValue());
                break;

            default:
                cellValue = "";
        }
        return cellValue;
    }

    private void addSumColumn(Map<Integer, DataSourceDTO> map, int columnNum, Double aDouble) {
        if (map.containsKey(columnNum)) {
            DataSourceDTO dto = map.get(columnNum);
            dto.setSumValue(dto.getSumValue() + aDouble);
            if (dto.getCountValue() == null) {
                dto.setCountValue(0);
            } else {
                dto.setCountValue(dto.getCountValue() + 1);
            }
            map.replace(columnNum, dto);
        } else {
            DataSourceDTO dto = new DataSourceDTO();
            dto.setSumValue(aDouble);
            map.put(columnNum, dto);
        }
    }

    @Override
    public DataSource create(DataSource entity) {
        return repository.save(entity);
    }

    @Override
    public DataSource retrieve(Integer id) {
        Optional<DataSource> entity = repository.findById(id);
        return entity.orElse(null);
    }

    @Override
    public void update(DataSource entity, Integer id) {
        repository.save(entity);
    }

    @Override
    public void delete(Integer id) throws Exception {
        DataSource entity = retrieve(id);
        entity.setIsActive(IS_NOT_ACTIVE);
        repository.save(entity);
    }

    private String getFileExtension(String fileName) {
        if (fileName.endsWith("xlsx")) {
            return ".xlsx";
        } else if (fileName.endsWith("xls")) {
            return ".xls";
        }
        return "";
    }

}
