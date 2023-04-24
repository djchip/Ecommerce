package com.ecommerce.core.util;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.dto.DataLabelDTO;
import com.ecommerce.core.entities.DataSource;
import com.ecommerce.core.entities.Form;
import com.ecommerce.core.entities.Unit;
import com.ecommerce.core.service.DataSourceService;
import com.ecommerce.core.service.FormService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class FormExport extends AbstractExporter{

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    private FormService formService;
    private DataSourceService dataSourceService;

    @Autowired
    public FormExport(FormService formService, DataSourceService dataSourceService) {
        this.formService = formService;
        this.dataSourceService = dataSourceService;
    }

    private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle style) {
        XSSFCell cell = row.createCell(columnIndex);
        sheet.autoSizeColumn(columnIndex);

        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else   {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }


    private void writeRow(List<DataSource> listData, XSSFRow row, Integer minCol, Integer maxCol){
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        DataFormat dataFormat = workbook.createDataFormat();
        XSSFFont font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeight(12);
        cellStyle.setFont(font);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);

        if (!listData.isEmpty()){
            listData.forEach((data) ->{
                Object value;
                if (data.getValue().contains(".0")){
//                    value = Integer.parseInt(data.getValue().split("\\.")[0]);
                    value = Double.parseDouble(data.getValue());
                } else {
                    value = data.getValue();
                }
                createCell(row, data.getColIdx(), value, cellStyle);
                sheet.setColumnWidth(data.getColIdx(), 25 * 256);
            });
        }
        for (int i= minCol; i<= maxCol; i++){
            if (row.getCell(i) == null){
                createCell(row, i, "", cellStyle);
                sheet.setColumnWidth(i, 25 * 256);
            }
        }
    }

    public void export(Integer id, HttpServletResponse response) throws IOException, InvalidFormatException {
        super.setResponseHeader(response, "application/octet-stream", ".xlsx");
        Form entity = formService.findById(id);
        List<Integer> listKey = Arrays.asList(entity.getFormKey());
        List<DataLabelDTO> labelByKey = dataSourceService.getLabelByKey(entity.getFormKey());
        Integer minCol = 0;
        Integer maxCol = 0;
        if (!labelByKey.isEmpty()){
            minCol = labelByKey.get(0).getColIdx();
            maxCol = labelByKey.get(labelByKey.size() - 1).getColIdx();
        }

        Path template = Paths.get(entity.getPathFile());
        Path dir = Paths.get(ConstantDefine.FILE_PATH.FORM_DOWNLOAD + entity.getId());
        Path file = Paths.get(ConstantDefine.FILE_PATH.FORM_DOWNLOAD + entity.getId() + "/" + entity.getFileName());
        if (!Files.exists(dir)){
            Files.createDirectories(dir);
        }
        Files.copy(template, file, StandardCopyOption.REPLACE_EXISTING);

        workbook = new XSSFWorkbook(new File(ConstantDefine.FILE_PATH.FORM_DOWNLOAD + entity.getId() + "/" + entity.getFileName()));
        sheet = workbook.getSheetAt(0);
        AtomicReference<Integer> rowIndex = new AtomicReference<>(entity.getStartTitle() - 2 + entity.getNumTitle());

        for (int i = sheet.getNumMergedRegions() - 1; i>=0; i--){
            CellRangeAddress cellAddresses = sheet.getMergedRegion(i);
            if (cellAddresses.getFirstRow() >= (entity.getStartTitle() - 1 + entity.getNumTitle())){
                sheet.removeMergedRegion(i);
            }
        }

        for (int r = (entity.getStartTitle() - 1 + entity.getNumTitle()); r <= sheet.getLastRowNum(); r++){
            XSSFRow rowOld = sheet.getRow(r);
            sheet.removeRow(rowOld);
        }


        if (!entity.getUnits().isEmpty()){
            Collections.sort(entity.getUnits(), new Comparator<Unit>() {
                @Override
                public int compare(Unit o1, Unit o2) {
                    return o1.getClassify().compareTo(o2.getClassify());
                }
            });
            Integer finalMinCol = minCol;
            Integer finalMaxCol = maxCol;
            entity.getUnits().forEach((u) ->{
                Integer minRow = dataSourceService.getMinRow(listKey, u.getId()) == null ? 0 : dataSourceService.getMinRow(listKey, u.getId());
                Integer maxRow = dataSourceService.getMaxRow(listKey, u.getId()) == null ? 0 : dataSourceService.getMaxRow(listKey, u.getId());
                for (int i = minRow; i <= maxRow; i++){
                    if(dataSourceService.getList(entity.getFormKey(), u.getId(), i).size() > 0){
                        rowIndex.getAndSet(rowIndex.get() + 1);
                        XSSFRow row = sheet.createRow(rowIndex.get());
                        writeRow(dataSourceService.getList(entity.getFormKey(), u.getId(), i), row, finalMinCol, finalMaxCol);
                    }
                }
            });
        }

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
