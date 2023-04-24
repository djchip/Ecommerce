package com.ecommerce.core.service;

import com.ecommerce.core.exceptions.DetectExcelException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.InputStream;

public interface WorkbookService {
    Workbook getWorkbook(InputStream inputStream, String excelFilePath) throws IOException, DetectExcelException;
    Object getCellValue(Cell cell);
}
