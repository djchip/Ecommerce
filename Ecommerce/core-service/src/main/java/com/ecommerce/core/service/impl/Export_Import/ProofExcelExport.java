package com.ecommerce.core.service.impl.Export_Import;

import com.ecommerce.core.entities.Proof;
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

public class ProofExcelExport {
    XSSFWorkbook workbook;
    XSSFSheet sheet;
    private List<Proof> proofList;

    public static boolean checkExcelFormat(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return true;
        } else {
            return false;
        }
    }
    public ProofExcelExport () {workbook = new XSSFWorkbook(); }
    private void writeHeaderLine() {
        sheet = workbook.createSheet("form");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();

        font.setFontHeight(14);
        style.setFont(font);

        createCell(row, 0, "Tên minh chứng", style);
        createCell(row, 1, "Số hiệu", style);
        createCell(row, 2, "Mã minh chứng", style);
        createCell(row, 3, "Lĩnh vực", style);
        createCell(row, 4, "Ngày tạo", style);
        createCell(row, 5, "Người tạo", style);
        createCell(row, 6, "Ngày cập nhật", style);
        createCell(row, 7, "Người cập nhật", style);
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
    private void writeDataLines(List<Proof> criteriaList) throws IOException {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (Proof proof: proofList) {
            XSSFRow row = sheet.getRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, proof.getProofName(), style);
            createCell(row, columnCount++, proof.getNumberSign(), style);
            createCell(row, columnCount++, String.valueOf(proof.getCreatedDate()), style);
            createCell(row, columnCount++, proof.getProofCode(), style);
            createCell(row, columnCount++, String.valueOf(proof.getUpdatedDate()), style);
            createCell(row, columnCount++, proof.getNumberSign(), style);
            createCell(row, columnCount++, proof.getProofCode(), style);
            createCell(row, columnCount++, proof.getField(), style);
            createCell(row, columnCount++, proof.getReleaseBy(), style);
        }
    }
    public void export(List<Proof>  proofList, HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines(proofList);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }
}