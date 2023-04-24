package com.ecommerce.core.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ecommerce.core.entities.Unit;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = { "Id", "Title", "Description", "Published" };
    static String SHEET = "units";

    public static boolean checkExcelFormat(MultipartFile file) {

        String contentType = file.getContentType();

        if (contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return true;
        } else {
            return false;
        }
    }

    public static List<Unit> convertExcelToListOfUnit(InputStream is) {
        List<Unit> list = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet(SHEET);
            int rowNumber = 0;
            Iterator<Row> iterator = sheet.iterator();
            while (iterator.hasNext()) {
                Row row = iterator.next();
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }
                Iterator<Cell> cells = row.iterator();
                int cid = 0;

                Unit p = new Unit();
                while (cells.hasNext()) {
                    Cell cell = cells.next();
                    switch (cid) {
                        case 0:
                            p.setId((int) cell.getNumericCellValue());
                            break;
                        case 1:
                            p.setUnitName(cell.getStringCellValue());
                            break;
                        case 2:
                            p.setUnitCode(cell.getStringCellValue());
                            break;
                        case 3:
                            p.setDescription(cell.getStringCellValue());
                            break;
                        case 4:
                            p.setCreatedBy(cell.getStringCellValue());
                            break;
                        case 5:
                            p.setUpdatedBy(cell.getStringCellValue());
                            break;
                        default:
                            break;
                    }
                    cid++;

                }
                list.add(p);
            }
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
        return list;
    }
}
