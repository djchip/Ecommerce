package com.ecommerce.core.service;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.dto.DbStaffDTO;
import com.ecommerce.core.dto.DocumentReportDTO;
import com.ecommerce.core.dto.FormReportDTO;
import com.ecommerce.core.entities.Unit;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    FormService formService;

//    public String exportStaffReport(String fileName) throws IOException, JRException {
//        Path dir = Paths.get(ConstantDefine.FILE_PATH.REPORT);
//        if (!Files.exists(dir)){
//            Files.createDirectories(dir);
//        }
//        String path = ConstantDefine.FILE_PATH.REPORT + "/" + fileName + ".pdf";
//        System.out.println(path);
//        List<DbStaffDTO> listStaff = staffService.getListStaff();
//        File file = ResourceUtils.getFile(ConstantDefine.FILE_PATH.REPORT + "/TCCB.jrxml");
//        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
//        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(listStaff);
//        Map<String, Object> param = new HashMap<>();
//        param.put("author", "NEO");
//        JasperPrint print = JasperFillManager.fillReport(jasperReport, param, dataSource);
//        JasperExportManager.exportReportToPdfFile(print, path);
//        System.out.println("Export Form ===>");
//        return "Success";
//    }

    public String exportFormReport(String fileName, List<FormReportDTO> formList, Unit unit) throws IOException, JRException {
        Path dir = Paths.get(ConstantDefine.FILE_PATH.REPORT);
        if (!Files.exists(dir)){
            Files.createDirectories(dir);
        }
        String path = ConstantDefine.FILE_PATH.REPORT + "/" + fileName + ".pdf";
        System.out.println(path);
        System.out.println("Length:" + formList.size());
        File file = ResourceUtils.getFile( ConstantDefine.FILE_PATH.REPORT + "/StatisticalForm.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(formList);
        Map<String, Object> param = new HashMap<>();
        param.put("author", "NEO");
        param.put("unit", unit.getUnitName());
        param.put("quantity", formList.size());
        JasperPrint print = JasperFillManager.fillReport(jasperReport, param, dataSource);
        JasperExportManager.exportReportToPdfFile(print, path);
        System.out.println("Export Form ===>");
        return "Success";
    }

    public String exportDocumentReport(String fileName, List<DocumentReportDTO> documentList, Unit unit) throws IOException, JRException {
        Path dir = Paths.get(ConstantDefine.FILE_PATH.REPORT);
        if (!Files.exists(dir)){
            Files.createDirectories(dir);
        }
        String path = ConstantDefine.FILE_PATH.REPORT + "/" + fileName + ".pdf";
        System.out.println(path);
        System.out.println("Length:" + documentList.size());
        File file = ResourceUtils.getFile(ConstantDefine.FILE_PATH.REPORT + "/StatisticalDocument.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(documentList);
        Map<String, Object> param = new HashMap<>();
        param.put("author", "NEO");
        param.put("quantity", documentList.size());
        param.put("unit", unit.getUnitName());
        JasperPrint print = JasperFillManager.fillReport(jasperReport, param, dataSource);
        JasperExportManager.exportReportToPdfFile(print, path);
        System.out.println("Export Form ===>");
        return "Success";
    }
}
