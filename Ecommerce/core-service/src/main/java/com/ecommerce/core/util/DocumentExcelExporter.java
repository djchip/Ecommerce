package com.ecommerce.core.util;

import com.ecommerce.core.dto.ListDocumentDTO;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class DocumentExcelExporter extends AbstractExporter{

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public  DocumentExcelExporter(){
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine(){
        sheet = workbook.createSheet("Document");
        XSSFRow row = sheet.createRow(0);

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Times New Roman");
        font.setFontHeight(16);
        cellStyle.setFont(font);
        cellStyle.setBorderBottom(BorderStyle.MEDIUM);
        cellStyle.setBorderLeft(BorderStyle.MEDIUM);
        cellStyle.setBorderRight(BorderStyle.MEDIUM);
        cellStyle.setBorderTop(BorderStyle.MEDIUM);

        createCell(row, 0, "STT", cellStyle);
        createCell(row, 1, "Tên văn bản", cellStyle);
        createCell(row, 2, "Loại văn bản", cellStyle);
        createCell(row, 3, "Số hiệu", cellStyle);
        createCell(row, 4, "Ngày ban hành", cellStyle);
        createCell(row, 5, "Người ký", cellStyle);
        createCell(row, 6, "Lĩnh vực", cellStyle);
        createCell(row, 7, "Đơn vị phát hành", cellStyle);
        createCell(row, 8, "Mô tả", cellStyle);
        createCell(row, 9, "Người tạo", cellStyle);
        createCell(row, 10, "Ngày tạo", cellStyle);
        createCell(row, 11, "Người cập nhật", cellStyle);
        createCell(row, 12, "Ngày cập nhật", cellStyle);
    }

    private void writeHeaderLineEn(){
        sheet = workbook.createSheet("Document");
        XSSFRow row = sheet.createRow(0);

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Times New Roman");
        font.setFontHeight(16);
        cellStyle.setFont(font);
        cellStyle.setBorderBottom(BorderStyle.MEDIUM);
        cellStyle.setBorderLeft(BorderStyle.MEDIUM);
        cellStyle.setBorderRight(BorderStyle.MEDIUM);
        cellStyle.setBorderTop(BorderStyle.MEDIUM);

        createCell(row, 0, "No", cellStyle);
        createCell(row, 1, "Document name", cellStyle);
        createCell(row, 2, "Document type", cellStyle);
        createCell(row, 3, "Number", cellStyle);
        createCell(row, 4, "Release date", cellStyle);
        createCell(row, 5, "Signer", cellStyle);
        createCell(row, 6, "Field", cellStyle);
        createCell(row, 7, "Release by", cellStyle);
        createCell(row, 8, "Description", cellStyle);
        createCell(row, 9, "Created by", cellStyle);
        createCell(row, 10, "Created date", cellStyle);
        createCell(row, 11, "Updated by", cellStyle);
        createCell(row, 12, "Updated date", cellStyle);
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

    public void export(List<ListDocumentDTO> listDocument, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "application/octet-stream", ".xlsx");

        writeHeaderLine();
        writeDataLine(listDocument);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    public void exportEn(List<ListDocumentDTO> listDocument, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "application/octet-stream", ".xlsx");

        writeHeaderLineEn();
        writeDataLineEn(listDocument);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    private void writeDataLine(List<ListDocumentDTO> listDocument) {
        int rowIndex = 1;

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        font.setFontName("Times New Roman");
        cellStyle.setFont(font);
        cellStyle.setBorderBottom(BorderStyle.MEDIUM);
        cellStyle.setBorderLeft(BorderStyle.MEDIUM);
        cellStyle.setBorderRight(BorderStyle.MEDIUM);
        cellStyle.setBorderTop(BorderStyle.MEDIUM);
        int i = 1;
        for (ListDocumentDTO d : listDocument){

            XSSFRow row = sheet.createRow(rowIndex++);
            int columnIndex = 0;
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
//            String releaseDate = format.format(d.getReleaseDate());
//            String createdDate = format.format(d.getCreatedDate());
//            String updatedDate = format.format(d.getUpdatedDate());
            createCell(row, columnIndex++, i++, cellStyle);
            createCell(row, columnIndex++, d.getDocumentName(), cellStyle);
            createCell(row, columnIndex++, d.getDocumentTypeName(), cellStyle);
            createCell(row, columnIndex++, d.getDocumentNumber(), cellStyle);
            createCell(row, columnIndex++, String.valueOf(d.getReleaseDate()), cellStyle);
            createCell(row, columnIndex++, d.getSigner(), cellStyle);
            createCell(row, columnIndex++, d.getFieldName(), cellStyle);
            createCell(row, columnIndex++, d.getReleaseByName(), cellStyle);
            createCell(row, columnIndex++, d.getDescription(), cellStyle);
            createCell(row, columnIndex++, d.getCreatedBy(), cellStyle);
            createCell(row, columnIndex++, String.valueOf(d.getCreatedDate()), cellStyle);
            createCell(row, columnIndex++, d.getUpdatedBy(), cellStyle);
            createCell(row, columnIndex++, String.valueOf(d.getUpdatedDate()), cellStyle);
        }
    }

    private void writeDataLineEn(List<ListDocumentDTO> listDocument) {
        int rowIndex = 1;

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        font.setFontName("Times New Roman");
        cellStyle.setFont(font);
        cellStyle.setBorderBottom(BorderStyle.MEDIUM);
        cellStyle.setBorderLeft(BorderStyle.MEDIUM);
        cellStyle.setBorderRight(BorderStyle.MEDIUM);
        cellStyle.setBorderTop(BorderStyle.MEDIUM);
        int i = 1;
        for (ListDocumentDTO d : listDocument){

            XSSFRow row = sheet.createRow(rowIndex++);
            int columnIndex = 0;
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
//            String releaseDate = format.format(d.getReleaseDate());
//            String createdDate = format.format(d.getCreatedDate());
//            String updatedDate = format.format(d.getUpdatedDate());
            createCell(row, columnIndex++, i++, cellStyle);
            createCell(row, columnIndex++, d.getDocumentNameEn(), cellStyle);
            createCell(row, columnIndex++, d.getDocumentTypeNameEn(), cellStyle);
            createCell(row, columnIndex++, d.getDocumentNumber(), cellStyle);
            createCell(row, columnIndex++, String.valueOf(d.getReleaseDate()), cellStyle);
            createCell(row, columnIndex++, d.getSigner(), cellStyle);
            createCell(row, columnIndex++, d.getFieldNameEn(), cellStyle);
            createCell(row, columnIndex++, d.getReleaseByNameEn(), cellStyle);
            createCell(row, columnIndex++, d.getDescriptionEn(), cellStyle);
            createCell(row, columnIndex++, d.getCreatedBy(), cellStyle);
            createCell(row, columnIndex++, String.valueOf(d.getCreatedDate()), cellStyle);
            createCell(row, columnIndex++, d.getUpdatedBy(), cellStyle);
            createCell(row, columnIndex++, String.valueOf(d.getUpdatedDate()), cellStyle);
        }
    }
}
