package com.ecommerce.core.util;

import com.ecommerce.core.entities.ObjectDetail;
import com.ecommerce.core.service.ObjectDetailService;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class DatabaseExcelExporter extends AbstractExporter{

    @Autowired
    ObjectDetailService objDetailService;

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public DatabaseExcelExporter(){
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine(List<ObjectDetail> objectDetailList){
        sheet = workbook.createSheet("Cơ sở dữ liệu học viện");
        XSSFRow row = sheet.createRow(0);

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Times New Roman");
        font.setFontHeight(14);
        cellStyle.setFont(font);

        if (objectDetailList.size()>0){
            createCell(row, 0, "STT", cellStyle);
            int columnIndex = 1;
            for (ObjectDetail o: objectDetailList){
                createCell(row, columnIndex++, o.getFieldName(), cellStyle);
            }
        }
    }

    private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle style) {
        XSSFCell cell = row.createCell(columnIndex);
        sheet.autoSizeColumn(columnIndex);

        if(value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if(value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    public void export(List<ObjectDetail> objectDetailList, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "application/octet-stream", ".xlsx");

        writeHeaderLine(objectDetailList);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
