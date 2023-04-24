package com.ecommerce.core.service.impl.Export_Import;

import com.ecommerce.core.entities.Criteria;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class CriteriaExcelExport {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Criteria> criteriaList;

    public static boolean checkExcelFormat(MultipartFile file) {


        String contentType = file.getContentType();
        if (contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return true;
        } else {
            return false;
        }
    }
    public CriteriaExcelExport() {
        workbook = new XSSFWorkbook();
    }
    private void writeHeaderLine() {
        sheet = workbook.createSheet("form");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();

        font.setFontHeight(14);
        style.setFont(font);

        createCell(row, 0, "Tên tiêu chuẩn", style);
//        createCell(row, 2, "Kiểu cây thư mục (tiêu chuẩn/tiêu chí)", style);
        createCell(row, 1, "Mô tả", style);
        createCell(row, 2, "Ngày tạo", style);
        createCell(row, 3, "Người tạo", style);
        createCell(row, 4, "Ngày cập nhật", style);
        createCell(row, 5, "Người cập nhật", style);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Data) {
            cell.setCellValue((Date) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }
    private void writeDataLines(List<Criteria> criteriaList) throws IOException {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

        for (Criteria criteria : criteriaList) {
            XSSFRow row = sheet.getRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, criteria.getName(), style);
            createCell(row, columnCount++, criteria.getDescription(), style);
            createCell(row, columnCount++, format.format(criteria.getCreatedDate()), style);
            createCell(row, columnCount++, criteria.getCreate_by(), style);
            createCell(row, columnCount++, format.format(criteria.getUpdatedDate()), style);
            createCell(row, columnCount++, criteria.getUpdate_by(), style);
        }
    }
    public void export(List<Criteria> criteriaList, HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines(criteriaList);
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }
}
