package com.ecommerce.core.entities;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class UnitExcel {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Unit> unitList;

    public UnitExcel(List<Unit> unitList) {
        this.unitList = unitList;
        workbook = new XSSFWorkbook();
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style){
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if(value instanceof Integer) {
            cell.setCellValue((Integer) value) ;
        } else if(value instanceof String) {
            cell.setCellValue((String) value);
        } else {
            cell.setCellValue((Date) value);
        }
        cell.setCellStyle(style);
    }

    private void writeHeaderLineEn() {
        sheet = workbook.createSheet("ListUnit");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "No", style);
        createCell(row, 1, "Unit name", style);
        createCell(row, 2, "Unit code", style);
        createCell(row, 3, "Description", style);
        createCell(row, 4, "Created date", style);
        createCell(row, 5, "Created by", style);
        createCell(row, 6, "Updated date", style);
        createCell(row, 7, "Updated by", style);
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("ListUnit");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "STT", style);
        createCell(row, 1, "Tên đơn vị", style);
        createCell(row, 2, "Mã đơn vị", style);
        createCell(row, 3, "Mô tả", style);
        createCell(row, 4, "Ngày tạo", style);
        createCell(row, 5, "Người tạo", style);
        createCell(row, 6, "Ngày cập nhật", style);
        createCell(row, 7, "Người cập nhật", style);
    }

    private void writeDateLines() {
        int rowCount = 1;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        int count = 1;

        for(Unit unit : unitList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, count++, style);
            createCell(row, columnCount++, unit.getUnitName(), style);
            createCell(row, columnCount++, unit.getUnitCode(), style);
            createCell(row, columnCount++, unit.getDescription(), style);
            createCell(row, columnCount++, String.valueOf(unit.getCreatedDate()).substring(0,10), style);
            createCell(row, columnCount++, unit.getCreatedBy(), style);
            createCell(row, columnCount++, String.valueOf(unit.getUpdatedDate()).substring(0,10), style);
            createCell(row, columnCount++, unit.getUpdatedBy(), style);
        }
    }

    private void writeDateLinesEn() {
        int rowCount = 1;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        int count = 1;

        for(Unit unit : unitList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, count++, style);
            createCell(row, columnCount++, unit.getUnitNameEn(), style);
            createCell(row, columnCount++, unit.getUnitCode(), style);
            createCell(row, columnCount++, unit.getDescriptionEn(), style);
            createCell(row, columnCount++, String.valueOf(unit.getCreatedDate()).substring(0,10), style);
            createCell(row, columnCount++, unit.getCreatedBy(), style);
            createCell(row, columnCount++, String.valueOf(unit.getUpdatedDate()).substring(0,10), style);
            createCell(row, columnCount++, unit.getUpdatedBy(), style);
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDateLines();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    public void exportEn(HttpServletResponse response) throws IOException {
        writeHeaderLineEn();
        writeDateLinesEn();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
