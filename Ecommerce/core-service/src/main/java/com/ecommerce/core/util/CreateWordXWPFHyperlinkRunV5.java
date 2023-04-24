package com.ecommerce.core.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class CreateWordXWPFHyperlinkRunV5 {

  public static void main(String[] args) throws Exception {
   XSSFWorkbook workbook = new XSSFWorkbook(new File("\\app\\exhibitionmanagement\\document\\File3.xlsx"));
   XSSFSheet sheet = workbook.getSheetAt(0);
   System.out.println("Last Row Num: " + sheet.getLastRowNum());
      List<XSSFTable> tables = sheet.getTables();
      System.out.println("Size: " + tables.size());
      for (XSSFTable t: tables){
          System.out.println("ROW: " + t.getStartRowIndex());
          System.out.println("COUNT: " + t.getHeaderRowCount());
      }
//   System.out.println("Last Cell Num: " + sheet.getRow(24).getCell(4));
  XSSFCellStyle cellStyle = workbook.createCellStyle();
  XSSFFont font = workbook.createFont();
  font.setFontName("Times New Roman");
  font.setFontHeight(12);
  cellStyle.setFont(font);
  cellStyle.setAlignment(HorizontalAlignment.CENTER);
  cellStyle.setBorderBottom(BorderStyle.THIN);
  cellStyle.setBorderLeft(BorderStyle.THIN);
  cellStyle.setBorderRight(BorderStyle.THIN);
  cellStyle.setBorderTop(BorderStyle.THIN);

//      sheet.shiftColumns(4, sheet.getRow(10).getLastCellNum(), 1);
//      for (int i = 6; i<=sheet.getLastRowNum(); i++){
//          Row row;
//          if (sheet.getRow(i) == null){
//              row = sheet.createRow(i);
//          } else {
//              row = sheet.getRow(i);
//          }
//          if (i > 8){
//              String a = CellReference.convertNumToColString(5);
//              String b = CellReference.convertNumToColString(3);
//              System.out.println("Col: " + a + i);
//              =IF(D14="", "", (F14/D14*100)) || a+ (i+1) + "/" + b + (i+1) + "*100"
//              row.createCell(4).setCellFormula("IF(" + b+ (i+1) + "=\"\", \"\", " + a + (i+1) + "/" + b + (i+1) + "*100)");
//              row.getCell(4).setCellStyle(cellStyle);
//          } else {
//              row.createCell(4).setCellValue("Tỉ lệ Nam");
//              row.getCell(4).setCellStyle(cellStyle);
//          }
//      }
//      sheet.shiftColumns(7, sheet.getRow(10).getLastCellNum(), 1);
//      sheet.shiftRows(13, sheet.getLastRowNum(), 1);
//      sheet.shiftRows(18, sheet.getLastRowNum(), 1);
//      sheet.shiftRows(23, sheet.getLastRowNum(), 1);

      //

      //

      FileOutputStream os = new FileOutputStream(new File("\\app\\exhibitionmanagement\\document\\FileDataNew.xlsx"));
   workbook.write(os);
   os.close();
   workbook.close();
  }
}
