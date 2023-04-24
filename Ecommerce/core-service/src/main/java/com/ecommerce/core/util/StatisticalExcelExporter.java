package com.ecommerce.core.util;

import com.ecommerce.core.entities.DataSource;
import com.ecommerce.core.entities.Unit;
import com.ecommerce.core.service.DataSourceService;
import com.ecommerce.core.service.FormService;
import com.ecommerce.core.service.StatisticalReportService;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
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
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class StatisticalExcelExporter extends AbstractExporter{

    private XSSFWorkbook workbook = new XSSFWorkbook();
    private XSSFSheet sheet;

    private StatisticalReportService service;
    private FormService formService;
    private DataSourceService dataSourceService;

    @Autowired
    public StatisticalExcelExporter(StatisticalReportService service, FormService formService, DataSourceService dataSourceService) {
        this.service = service;
        this.formService = formService;
        this.dataSourceService = dataSourceService;
    }

    private CellStyle createStyle(Integer size, boolean isBold, boolean isBorder){
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(isBold);
        font.setFontName("Times New Roman");
        font.setFontHeight(size);
        cellStyle.setFont(font);
        if (isBorder){
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
        }
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        return cellStyle;
    }

    private void writeHeaderLine(String name, String items, boolean isNo){
        sheet = workbook.createSheet("VNUA");

        XSSFRow rowTitle = sheet.createRow(0);
        Cell cell = rowTitle.createCell(0);
        cell.setCellValue("HỌC VIỆN NÔNG NGHIỆP VIỆT NAM");
        cell.setCellStyle(createStyle(12, false, false));

        rowTitle = sheet.createRow(1);
        Cell cell1 = rowTitle.createCell(0);
        cell1.setCellValue("TRUNG TÂM ĐẢM BẢO CHẤT LƯỢNG");
        cell1.setCellStyle(createStyle(12, true, false));

        rowTitle = sheet.createRow(3);
        Cell cell2 = rowTitle.createCell(0);
        cell2.setCellValue(name.toUpperCase());
        cell2.setCellStyle(createStyle(13, true, false));

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 10));
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 10));
        sheet.createFreezePane(0,6);

        XSSFRow row = sheet.createRow(5);

        Integer index;
        if (isNo){
            createCell(row, 0, "STT", true);
            index = 0;
        } else {
            index = -1;
        }
        final JSONArray listItems = (JSONArray) JSONValue.parse(items);
        for (final Object i: listItems){
            index++;
            sheet.setColumnWidth(index, 25 * 256);
            final JSONObject item = (JSONObject) i;
            String itemName = item.get((Object) "itemName") == null ? null : item.get((Object) "itemName").toString();
            createCell(row, index, itemName, true);
        }
    }

    private void createCell(XSSFRow row, int columnIndex, Object value, boolean isBold) {
        XSSFCell cell = row.createCell(columnIndex);
        sheet.autoSizeColumn(columnIndex);

        if(value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if(value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if(value instanceof Double) {
            cell.setCellValue((Double) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(createStyle(12, isBold, true));
    }

    private void writeRow(List<Unit> units, String items, boolean isNo, List<Integer> key){
        AtomicReference<Integer> indexRow = new AtomicReference<>(5);
        Integer indexCol;
        AtomicReference<Integer> index = new AtomicReference<>(0);
        AtomicReference<Integer> idx = new AtomicReference<>(-1);
        AtomicBoolean isCheck = new AtomicBoolean(false);
        Collections.sort(units, new Comparator<Unit>() {
            @Override
            public int compare(Unit o1, Unit o2) {
                return o1.getClassify().compareTo(o2.getClassify());
            }
        });

        final JSONArray listItems = (JSONArray) JSONValue.parse(items);
        for (int u = 0; u< units.size(); u ++){
            Integer tempClassify = 0;
            if (u != 0 && units.get(u).getClassify() > units.get(u-1).getClassify()){
                tempClassify++;
            }

            Integer minRow = dataSourceService.getMinRow(key, units.get(u).getId());
            Integer maxRow = dataSourceService.getMaxRow(key, units.get(u).getId());
            for (int i = minRow == null ? 0 : minRow; i <= (maxRow == null ? 0 : maxRow); i++){
                indexRow.getAndSet(indexRow.get() + 1);
                index.getAndSet(index.get() + 1);
                XSSFRow row = sheet.createRow(indexRow.get());

                for (int j=0; j< listItems.size(); j++){
                    if (isNo){
                        indexCol = j+1;
                        createCell(row, 0, index.get(), false);
                    } else {
                        indexCol = j;
                    }
                    List<Object> valueRow = new ArrayList<>();
                    final JSONObject item = (JSONObject) listItems.get(j);
                    boolean itemSynthetic = (boolean) item.get((Object) "itemSynthetic");
                    if (itemSynthetic){
                        isCheck.set(true);
                    }

                    String itemRadio = item.get((Object) "itemRadio").toString();
                    String itemCalculate = item.get((Object) "itemCalculate") == null ? null : item.get((Object) "itemCalculate").toString();
                    if (itemRadio.equalsIgnoreCase("CSDL")){
                        final JSONArray itemsFieldDb = (JSONArray) JSONValue.parse(item.get((Object) "itemFieldDb").toString());
                        if ( itemsFieldDb.size()>= 2){
                            Integer idC = -1;
                            List<String> field = new ArrayList<>();
                            for (final Object fieldDb : itemsFieldDb){
                                final JSONObject fDb = (JSONObject) fieldDb;
                                String itemFieldName = fDb.get((Object) "itemFieldName") == null ? null : fDb.get((Object) "itemFieldName").toString();
                                idC = Integer.parseInt(itemFieldName);
                                DataSource dataSource = dataSourceService.retrieve(idC);
                                String value = dataSourceService.getCellValue(dataSource.getFormKey(), units.get(u).getId(), dataSource.getColIdx(), i) == null ? "0" : dataSourceService.getCellValue(dataSource.getFormKey(), units.get(u).getId(), dataSource.getColIdx(), i).getValue();
                                field.add(value);
                            }
                            AtomicReference<Object> obj = new AtomicReference<>("");
                            try {
                                double temp = 0;
                                switch (itemCalculate){
                                    case "SUM":
                                        for (String s : field){
                                            temp += s == "" ? 0 : Double.parseDouble(s);
                                        }
                                        obj.set(temp);
                                        break;
                                    case "COUNT":
                                        obj.set(field.size());
                                        break;
                                    case "MAX":
                                        temp = field.stream().mapToDouble(Double :: parseDouble).max().getAsDouble();
                                        obj.set(temp);
                                        break;
                                    case "MIN":
                                        temp = field.stream().mapToDouble(Double :: parseDouble).min().getAsDouble();
                                        obj.set(temp);
                                        break;
                                    case "AVG":
                                        for (String s : field){
                                            temp += s == "" ? 0 : Double.parseDouble(s);
                                        }
                                        obj.set((temp/field.size()));
                                        break;
                                    case "SUB":
                                        temp = Double.parseDouble(field.get(0)) - Double.parseDouble(field.get(1));
                                        obj.set(temp);
                                        break;
                                    case "RAT":
                                        temp = Double.parseDouble(field.get(1)) == 0 ? null : (Double.parseDouble(field.get(0)) / Double.parseDouble(field.get(1)) * 100);
                                        obj.set(temp);
                                        break;
                                    default:
                                        obj.set("");
                                        break;
                                }
                            } catch (Exception e){
                                obj.set("");
                            }
                            createCell(row, indexCol, obj.get(), false);
                        } else {
                            Integer idC = -1;
                            for (final Object fieldDb : itemsFieldDb){
                                final JSONObject fDb = (JSONObject) fieldDb;
                                String itemFieldName = fDb.get((Object) "itemFieldName") == null ? null : fDb.get((Object) "itemFieldName").toString();
                                idC = Integer.parseInt(itemFieldName);
                            }
                            DataSource value = null;
                            if (idC > 0){
                                DataSource dataSource = dataSourceService.retrieve(idC);
                                value = dataSourceService.getCellValue(dataSource.getFormKey(), units.get(u).getId(), dataSource.getColIdx(), i);
                            }
                            Object obj = "";
                            if (value != null){
                                if (value.getValue().contains(".0")){
//                                    obj = Integer.parseInt(value.getValue().split("\\.")[0]);
                                    obj = Double.parseDouble(value.getValue());
                                } else {
                                    obj = value.getValue();
                                }
                            }
                            valueRow.add(obj);
                            createCell(row, indexCol, obj, false);
                        }
                    }
                    if (itemRadio.equalsIgnoreCase("BC")){
                        final JSONArray itemsFieldRC = (JSONArray) JSONValue.parse(item.get((Object) "itemFieldRC").toString());
                        List<String> filedRC = new ArrayList<>();
                        if (itemsFieldRC != null){
                            for (final Object fieldRC : itemsFieldRC){
                                final JSONObject fRC = (JSONObject) fieldRC;
                                String itemFieldName = fRC.get((Object) "itemFieldNameRC") == null ? "-1" : fRC.get((Object) "itemFieldNameRC").toString();
                                filedRC.add(CellReference.convertNumToColString(isNo ? (Integer.parseInt(itemFieldName) + 1) : Integer.parseInt(itemFieldName)) + (indexRow.get() + 1));
                            }
                            String formula;
                            if (itemsFieldRC.size() >= 2){
                                switch (itemCalculate){
                                    case "SUM":
                                        formula = "IFERROR(SUM(" + StringUtils.join(filedRC, ",") + "), \"\")";
                                        break;
                                    case "COUNT":
                                        formula = "COUNT(" + StringUtils.join(filedRC, ",") + ")";
                                        break;
                                    case "AVG":
                                        formula = "IFERROR(SUM(" + StringUtils.join(filedRC, ",") + ")/" + filedRC.size() + ", \"\")";
                                        break;
                                    case "MAX":
                                        formula = "MAX(" + StringUtils.join(filedRC, ",") + ")";
                                        break;
                                    case "MIN":
                                        formula = "MIN(" + StringUtils.join(filedRC, ",") + ")";
                                        break;
                                    case "SUB":
                                        formula = "IFERROR(" + StringUtils.join(filedRC, "-") + ", \"\")";
                                        break;
                                    case "RAT":
                                        formula = "IFERROR(" + StringUtils.join(filedRC, "/") + "*100" + ", \"\")";
                                        break;
                                    default:
                                        formula = "";
                                        break;
                                }
                            } else {
                                formula = StringUtils.join(filedRC, "");
                            }
                            row.createCell(indexCol).setCellFormula(formula);
                            row.getCell(indexCol).setCellStyle(createStyle(12, false, true));
                        }
                    }
                }
            }
        }
        if (isCheck.get()){
            XSSFRow row = sheet.createRow(indexRow.get() + 1);
            Integer length;
            if (isNo){
                length = listItems.size() + 1;
            } else {
                length = listItems.size();
            }
            for (int i=0; i < length; i++){
                if (i == 0){
                    createCell(row, i, "Tổng toàn học viện", true);
                    sheet.autoSizeColumn(i);
                } else {
                    String a = CellReference.convertNumToColString(i);
                    JSONObject item;
                    if (isNo){
                        item = (JSONObject) listItems.get((i-1));
                    } else {
                        item = (JSONObject) listItems.get(i);
                    }
                    String itemQuantity = item.get((Object) "itemQuantity") == null ? "" : item.get((Object) "itemQuantity").toString();
                    String formula = "";
                    switch (itemQuantity){
                        case "SUM":
                            formula = "IFERROR(SUM(" + a + 7 + ":" + a + (indexRow.get() + 1) + ")" + ", \"\")";
                            break;
                        case "COUNT":
                            formula = "COUNT(" + a + 7 + ":" + a + (indexRow.get() + 1) + ")";
                            break;
                        case "MAX":
                            formula = "MAX(" + a + 7 + ":" + a + (indexRow.get() + 1) + ")";
                            break;
                        case "MIN":
                            formula = "MIN(" + a + 7 + ":" + a + (indexRow.get() + 1) + ")";
                            break;
                        default:
                            formula = "\"\"";
                            break;
                    }
//                    row.createCell(i).setCellFormula("SUM(" + a + 7 + ":" + a + (indexRow.get() + 1) + ")");
                    row.createCell(i).setCellFormula(formula);
                    row.getCell(i).setCellStyle(createStyle(12, true, true));
                }
            }
        }
    }

    public void export(String name, String items, boolean isNo, List<Unit> units, List<Integer> key, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "application/octet-stream", ".xlsx");
        writeHeaderLine(name, items, isNo);
        writeRow(units, items, isNo, key);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
