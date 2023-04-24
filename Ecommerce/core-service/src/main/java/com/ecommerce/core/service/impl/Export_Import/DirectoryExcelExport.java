package com.ecommerce.core.service.impl.Export_Import;

import com.ecommerce.core.entities.Directory;
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
import java.util.Date;
import java.util.List;

public class DirectoryExcelExport {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Directory> listDirectory;

    public static boolean checkExcelFormat(MultipartFile file) {

        String contentType = file.getContentType();

        if (contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return true;
        } else {
            return false;
        }
    }

    public DirectoryExcelExport() {
        workbook = new XSSFWorkbook();

    }


    private void writeHeaderLine() {
        sheet = workbook.createSheet("form");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
//        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);


        createCell(row, 0, "Tên cây thư mục", style);
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

    private void writeDataLines(List<Directory> listDirectory) throws IOException {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);


        for (Directory directory : listDirectory) {

            XSSFRow row = sheet.createRow(rowCount++);
            int columnCount = 0;
//            createCell(row, columnCount++, directory.getId().toString(), style);
            createCell(row, columnCount++, directory.getName(), style);
//            createCell(row, columnCount++, directory.getFoder(), style);
            createCell(row, columnCount++, directory.getDescription(), style);
            createCell(row, columnCount++, String.valueOf(directory.getCreatedDate()), style);
            createCell(row, columnCount++, directory.getCreate_by(), style);
            createCell(row, columnCount++, String.valueOf(directory.getUpdatedDate()), style);
            createCell(row, columnCount++, directory.getUpdate_by(), style);

        }
    }

    public void export(List<Directory> listDirectory, HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines(listDirectory);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }
}
