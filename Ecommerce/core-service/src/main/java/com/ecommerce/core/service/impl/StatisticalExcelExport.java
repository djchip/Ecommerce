package com.ecommerce.core.service.impl;

import com.ecommerce.core.constants.TypeEnum;
import com.ecommerce.core.entities.Unit;
import com.ecommerce.core.service.StatisticalReportService;
import com.ecommerce.core.service.DataSourceService;
import com.ecommerce.core.service.FormService;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatisticalExcelExport extends AbstractExporter {

    private XSSFWorkbook workbook = new XSSFWorkbook();
    private XSSFSheet sheet;

    @Autowired
    public StatisticalExcelExport(StatisticalReportService service,
                                  FormService formService, DataSourceService dataSourceService) {
        this.service = service;
        this.formService = formService;
        this.dataSourceService = dataSourceService;
    }

    private StatisticalReportService service;
    private FormService formService;
    private DataSourceService dataSourceService;

    private void writeHeaderLine(String name, String items, boolean isNo) {
        sheet = workbook.createSheet("VNUA");

        XSSFCellStyle cellStyleTitle = workbook.createCellStyle();
        XSSFFont fontTitle = workbook.createFont();
        fontTitle.setBold(true);
        fontTitle.setFontName("Times New Roman");
        fontTitle.setFontHeight(12);
        cellStyleTitle.setFont(fontTitle);
        cellStyleTitle.setAlignment(HorizontalAlignment.CENTER);

        XSSFRow rowTitle = sheet.createRow(0);
        Cell cell = rowTitle.createCell(0);
        cell.setCellValue("HỌC VIỆN NÔNG NGHIỆP VIỆT NAM");
        cell.setCellStyle(cellStyleTitle);

        rowTitle = sheet.createRow(1);
        Cell cell1 = rowTitle.createCell(0);
        cell1.setCellValue("TRUNG TÂM ĐẢM BẢO CHẤT LƯỢNG");
        cell1.setCellStyle(cellStyleTitle);

        rowTitle = sheet.createRow(3);
        Cell cell2 = rowTitle.createCell(0);
        cell2.setCellValue(name.toUpperCase());
        cell2.setCellStyle(cellStyleTitle);

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 10));
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 10));
        sheet.createFreezePane(0,6);

        XSSFRow row = sheet.createRow(5);
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Times New Roman");
        font.setFontHeight(13);
        cellStyle.setFont(font);
        cellStyle.setBorderBottom(BorderStyle.MEDIUM);
        cellStyle.setBorderLeft(BorderStyle.MEDIUM);
        cellStyle.setBorderRight(BorderStyle.MEDIUM);
        cellStyle.setBorderTop(BorderStyle.MEDIUM);

        Integer index;
        if (isNo) {
            createCell(row, 0, "STT", cellStyle);
            createCell(row, 1, "Đơn vị", cellStyle);
            sheet.setColumnWidth(1, 25 * 256);
            index = 1;
        } else {
            createCell(row, 0, "Đơn vị", cellStyle);
            sheet.setColumnWidth(0, 25 * 256);
            index = 0;
        }
        final JSONArray listItems = (JSONArray) JSONValue.parse(items);
        for (final Object i : listItems) {
            index++;
            sheet.setColumnWidth(index, 25 * 256);
            final JSONObject item = (JSONObject) i;
            String itemName = item.get((Object) "itemName") == null ? null : item.get((Object) "itemName").toString();
            createCell(row, index, itemName, cellStyle);
        }
    }

    private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle style) {
        XSSFCell cell = row.createCell(columnIndex);
        sheet.autoSizeColumn(columnIndex);

        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLine(String items, boolean isNo, List<Unit> units) {
        int rowIndex = 6;
        int iC = 1;
        String temp = "0.0";

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        font.setFontName("Times New Roman");
        cellStyle.setFont(font);
        cellStyle.setBorderBottom(BorderStyle.MEDIUM);
        cellStyle.setBorderLeft(BorderStyle.MEDIUM);
        cellStyle.setBorderRight(BorderStyle.MEDIUM);
        cellStyle.setBorderTop(BorderStyle.MEDIUM);
//        ...
//        items = "[{\"itemId\":\"\",\"itemName\":\"Sinh viên\",\"itemNameEn\":\"Student\",\"itemQuantity\":\"SUM\",\"itemCost\":\"SUM\",\"itemUnit\":true,\"itemSynthetic\":true,\"itemRadio\":\"CSDL\",\"isCollapsed\":false,\"itemDb\":[{\"itemDbId\":\"\",\"itemDbName\":1,\"itemDbYear\":2101}],\"itemFieldDb\":[{\"itemFieldId\":\"\",\"itemFieldName\":10,\"itemFieldCalculate\":\"SUM\"},{\"itemFieldId\":\"\",\"itemFieldName\":25,\"itemFieldCalculate\":\"SUM\"},{\"itemFieldId\":\"\",\"itemFieldName\":26,\"itemFieldCalculate\":\"SUM\"}],\"itemFieldRC\":[],\"itemCalculate\":\"SUM\"},{\"itemId\":\"\",\"itemName\":\"Giảng viên\",\"itemNameEn\":\"\",\"itemQuantity\":\"SUM\",\"itemCost\":\"SUM\",\"itemUnit\":true,\"itemSynthetic\":true,\"itemRadio\":\"CSDL\",\"isCollapsed\":false,\"itemDb\":[{\"itemDbId\":\"\",\"itemDbName\":1,\"itemDbYear\":2101}],\"itemFieldDb\":[{\"itemFieldId\":\"\",\"itemFieldName\":8,\"itemFieldCalculate\":\"SUM\"}],\"itemFieldRC\":[],\"itemCalculate\":null},{\"itemId\":\"\",\"itemName\":\"Tỉ lệ GV/SV\",\"itemNameEn\":\"\",\"itemQuantity\":null,\"itemCost\":null,\"itemUnit\":true,\"itemSynthetic\":false,\"itemRadio\":\"BC\",\"isCollapsed\":false,\"itemDb\":[],\"itemFieldDb\":[],\"itemFieldRC\":[{\"itemFieldId\":\"\",\"itemFieldNameRC\":0},{\"itemFieldId\":\"\",\"itemFieldNameRC\":1}],\"itemCalculate\":\"RAT\"}]";
        final JSONArray listItems = (JSONArray) JSONValue.parse(items);

        Integer iN = -1;

        for (Unit u : units) {
            XSSFRow row = sheet.createRow(rowIndex++);
            Integer index = 1;
            if (isNo) {
                index = 2;
                createCell(row, 0, iC++, cellStyle);
                createCell(row, 1, u.getUnitName(), cellStyle);
            } else {
                createCell(row, 0, u.getUnitName(), cellStyle);
            }
            List<String> listValueCol = new ArrayList<>(listItems.size());
            for (final Object i : listItems) {
                iN++;
                boolean isCheck = false;
                final JSONObject item = (JSONObject) i;
                List<String> listValue = new ArrayList<>();
                List<String> listValueBC = new ArrayList<>();

                String itemCalculate = item.get((Object) "itemCalculate") == null ? null : item.get((Object) "itemCalculate").toString();
                boolean itemUnit = (boolean) item.get((Object) "itemUnit");
                boolean itemSynthetic = (boolean) item.get((Object) "itemSynthetic");
                String itemRadio = item.get((Object) "itemRadio").toString();

                if (itemRadio.equalsIgnoreCase("CSDL")) {
                    final JSONArray itemsDb = (JSONArray) JSONValue.parse(item.get((Object) "itemDb").toString());
                    if (itemsDb != null) {
                        for (final Object d : itemsDb) {
                            final JSONObject db = (JSONObject) d;
                            String itemDbName = db.get((Object) "itemDbName") == null ? null : db.get((Object) "itemDbName").toString();
                            String itemDbYear = db.get((Object) "itemDbYear") == null ? null : db.get((Object) "itemDbYear").toString();
                        }
                        final JSONArray itemsFieldDb = (JSONArray) JSONValue.parse(item.get((Object) "itemFieldDb").toString());
                        if (itemsFieldDb.size() >= 2) {
                            isCheck = true;
                        }
                        if (itemsFieldDb != null) {
                            for (final Object fieldDb : itemsFieldDb) {
                                final JSONObject fDb = (JSONObject) fieldDb;
                                String itemFieldName = fDb.get((Object) "itemFieldName") == null ? null : fDb.get((Object) "itemFieldName").toString();
                                String itemFieldCalculate = fDb.get((Object) "itemFieldCalculate") == null ? null : fDb.get((Object) "itemFieldCalculate").toString();
                                if (itemFieldCalculate != null) {
                                    switch (itemFieldCalculate) {
                                        case "SUM":
                                            temp = dataSourceService.getValueCol(TypeEnum.SUM, Integer.parseInt(itemFieldName), u.getId()) == null ? "0.0" :  dataSourceService.getValueCol(TypeEnum.SUM, Integer.parseInt(itemFieldName), u.getId()).getValue();
                                            break;
                                        case "COUNT":
                                            temp = dataSourceService.getValueCol(TypeEnum.COUNT, Integer.parseInt(itemFieldName), u.getId()) == null ? null : dataSourceService.getValueCol(TypeEnum.COUNT, Integer.parseInt(itemFieldName), u.getId()).getValue();
                                            break;
                                        case "AVG":
                                            temp = dataSourceService.getValueCol(TypeEnum.AVG, Integer.parseInt(itemFieldName), u.getId()) == null ? null : dataSourceService.getValueCol(TypeEnum.AVG, Integer.parseInt(itemFieldName), u.getId()).getValue();
                                            break;
                                        case "MAX":
                                            temp = dataSourceService.getMaxValue(Integer.parseInt(itemFieldName));
                                            break;
                                        case "MIN":
                                            temp = dataSourceService.getMinValue(Integer.parseInt(itemFieldName));
                                            break;
                                        default:
                                            temp = "0.0";
                                            break;
                                    }
                                    listValue.add(temp);
                                    if (!isCheck){
                                        listValueCol.add(temp);
                                    }
                                }
                            }
                        }
                    }
                }
                if (itemRadio.equalsIgnoreCase("BC")) {
                    final JSONArray itemsFieldRC = (JSONArray) JSONValue.parse(item.get((Object) "itemFieldRC").toString());
                    if (itemsFieldRC.size() >= 2) {
                        isCheck = true;
                    }
                    if (itemsFieldRC != null) {
                        for (final Object fieldRC : itemsFieldRC) {
                            final JSONObject fRC = (JSONObject) fieldRC;
                            String itemFieldName = fRC.get((Object) "itemFieldNameRC") == null ? null : fRC.get((Object) "itemFieldNameRC").toString();
                            temp = listValueCol.get(Integer.parseInt(itemFieldName));
                            listValueBC.add(temp);
                            if(!isCheck){
                                listValueCol.add(temp);
                            }
                        }
                    }
                }
                String itemQuantity = item.get((Object) "itemQuantity") == null ? null : item.get((Object) "itemQuantity").toString();
                if (itemCalculate != null && isCheck) {
                    switch (itemCalculate) {
                        case "SUM":
                            double a = 0;
                            if (itemRadio.equalsIgnoreCase("CSDL")){
                                for (String v: listValue){
                                    if (v != null){
                                        a += Double.parseDouble(v);
                                    }
                                }
                            } else {
                                for (String b: listValueBC){
                                    if (b != null){
                                        a += Double.parseDouble(b);
                                    }
                                }
                            }
                            temp = String.valueOf(a);
                            listValueCol.add(temp);
                            break;
                        case "COUNT":
                            break;
                        case "AVG":
                            double av = 0;
                            if (itemRadio.equalsIgnoreCase("CSDL")){
                                for (String v: listValue){
                                    av += Double.parseDouble(v);
                                }
                                temp = String.valueOf(av/listValue.size());
                            } else {
                                for (String b: listValueBC){
                                    av += Double.parseDouble(b);
                                }
                                temp = String.valueOf(av/listValueBC.size());
                            }
                            listValueCol.add(temp);
                            break;
                        case "MAX":
                            if (itemRadio.equalsIgnoreCase("CSDL")){
                                temp = Collections.max(listValue);
                            } else {
                                temp = Collections.max(listValueBC);
                            }
                            listValueCol.add(temp);
                            break;
                        case "MIN":
                            if (itemRadio.equalsIgnoreCase("CSDL")){
                                temp = Collections.min(listValue);
                            } else {
                                temp = Collections.min(listValueBC);
                            }
                            break;
                        case "SUB":
                            if (itemRadio.equalsIgnoreCase("CSDL")){
                                double x = listValue.get(0) == null ? 0 : Double.parseDouble(listValue.get(0));
                                double y = listValue.get(1) == null ? 0 : Double.parseDouble(listValue.get(1));
                                temp = String.valueOf(x-y);
                            } else {
                                double x = listValueBC.get(0) == null ? 0 : Double.parseDouble(listValueBC.get(0));
                                double y = listValueBC.get(1) == null ? 0 : Double.parseDouble(listValueBC.get(1));
                                temp = String.valueOf(x-y);
                            }
                            listValueCol.add(temp);
                            break;
                        case "RAT":
                            if (itemRadio.equalsIgnoreCase("BC")){
                                double x = listValueBC.get(0) == null ? 0 : Double.parseDouble(listValueBC.get(0));
                                double y = listValueBC.get(1) == null ? 0 : Double.parseDouble(listValueBC.get(1));
                                temp = x == 0 || y == 0 ? "0.0" : String.valueOf((x/y)*100) + " %";
                            } else {
                                double x = listValueBC.get(0) == null ? 0 : Double.parseDouble(listValueBC.get(0));
                                double y = listValueBC.get(1) == null ? 0 : Double.parseDouble(listValueBC.get(1));
                                temp = x == 0 || y == 0 ? "0.0" : String.valueOf((x/y)*100) + " %";
                            }
                            listValueCol.add(temp);
                            break;
                        default:
                            temp = "0.0";
                            listValueCol.add(temp);
                            break;
                    }
                }
                createCell(row, index++, temp, cellStyle);
            }
        }
    }

    public void export(String name, String items, boolean isNo, List<Unit> units, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "application/octet-stream", ".xlsx");
        writeHeaderLine(name, items, isNo);
        writeDataLine(items, isNo, units);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
