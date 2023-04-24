package com.ecommerce.core.controllers;

import com.ecommerce.core.util.FileDownloadUtil;
import com.ecommerce.core.util.FileUploadUtil;
import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.ResponseModel;
import com.ecommerce.core.dto.TreeNodeDTOProof;
import com.ecommerce.core.entities.ExhibitionFile;
import com.ecommerce.core.entities.FileInfo;
import com.ecommerce.core.service.CriteriaProofSerice;
import com.ecommerce.core.service.ExhibitionFileService;
import com.ecommerce.core.service.RolesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("criteria-proof")
@Slf4j
public class CriteriaProofController extends  BaseController {
    private  final String START_LOG="CRITERIA_PROOF {}";
    private  final String END_LOG="CRITERIA_PROOF {} finished in: {}";
    @Autowired
    CriteriaProofSerice criteriaProofSerice;

    @Autowired
    ExhibitionFileService exhibitionFileService;

    @Autowired
    RolesService rolesService;


    @GetMapping({"setupTreeStardendCriteriaProofByProgramId/{id}"})
    public ResponseModel initTree(@PathVariable Integer id, HttpServletRequest request) {
        final String action = "initTree";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {

//            String userName = null;
//            if (!isAdminFromToken(request)){
//                userName = getUserFromToken(request);
//            }

            List<String> listRoleCodeByUsername = rolesService.getListRolesCodeByUsername(getUserFromToken(request));


            List<TreeNodeDTOProof> treeNodeDTOS = new ArrayList<>();

            if(listRoleCodeByUsername.contains("ADMIN") || listRoleCodeByUsername.contains("Super Admin")){
                treeNodeDTOS=criteriaProofSerice.setupStandTreeCriterProof(id, null);
            } else {
                treeNodeDTOS= criteriaProofSerice.setupStandTreeCriterProof(id, getUserFromToken(request));
            }

//                    criteriaProofSerice.setupStandTreeCriterProof(id, userName);
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(treeNodeDTOS);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("/proof/wopi/files/{id}/contents")
    public ResponseEntity<?> getFileProof(@PathVariable("id") Integer id) {
        Optional<ExhibitionFile> exhFileByProofId = exhibitionFileService.finbyID(id);
        FileDownloadUtil downloadUtil = new FileDownloadUtil();
        Resource resource = null;
        try {
            if (exhFileByProofId != null){
                String path = exhFileByProofId.get().getFilePath();
                resource = downloadUtil.getFileAsResource(path, exhFileByProofId.get().getFileName());
            }
        } catch (IOException e) {
            return null;
        }

        if (resource == null) {
            return new ResponseEntity<>("File not found", org.springframework.http.HttpStatus.NOT_FOUND);
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }

    @GetMapping("/proof/wopi/files/{id}")
    public ResponseEntity<?> getDataFileProof(@PathVariable("id") Integer id) throws IOException {
        Optional<ExhibitionFile> listExhFileByProofId = exhibitionFileService.finbyID(id);
        FileInfo fileInfo = new FileInfo();

        String path = listExhFileByProofId.get().getFilePath() + "/" + listExhFileByProofId.get().getFileName();
            fileInfo.setBaseFileName(listExhFileByProofId.get().getFileName());
            fileInfo.setUserId(1956);
            fileInfo.setSize(FileUploadUtil.getCharCount(path));
            fileInfo.setUserCanWrite(false);

        System.out.println("====================================" + fileInfo.isUserCanWrite() + "============================");
        String contentType = "application/json; charset=utf-8";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.ACCEPT_CHARSET)
                .body(fileInfo);
    }
}
