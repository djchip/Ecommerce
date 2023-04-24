package com.ecommerce.core.service.impl;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.dto.*;
import com.ecommerce.core.entities.Assessment;
import com.ecommerce.core.entities.ExhibitionFile;
import com.ecommerce.core.entities.Proof;
import com.ecommerce.core.entities.UndoLog;
import com.ecommerce.core.repositories.AssessmentRepository;
import com.ecommerce.core.service.*;
import com.ecommerce.core.util.DocumentWordUtilV5;
import com.ecommerce.core.util.FileUploadUtil;
import com.google.gson.Gson;
import com.ecommerce.core.repositories.ProofRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@Slf4j
public class AssessmentServiceImpl implements AssessmentService {
    private static final Integer DELETED = 1;
    private static final Integer CAN_BE_DELETE = 0;
    private static final Integer UNDO_DELETE = 0;
    private static final String TABLE_NAME = "assessment";
    private static final Integer UN_ACTIVE_UNDO_LOG = 1;
    @Autowired
    AssessmentRepository repo;

    @Autowired
    ProofRepository proofRepository;
    @Autowired
    DirectoryService directoryService;
    @Autowired
    CriteriaService criteriaService;
    @Autowired
    ProgramsService programsService;
    @Autowired
    UndoLogService undoLogService;

    @Override
    public Page<AssessmentDTO> doSearch(Integer userId, String keyword, Pageable paging, Integer evaluated) {
        return repo.doSearch(userId,keyword,evaluated, paging);

    }
    @Override
    public Page<AssDTO> doSearch(String keyword, Integer programId, Integer directoryId, Integer criteriaId, Integer reporttype, String user, Pageable paging) {
        Page<Assessment> assessments = repo.doSearch1(keyword,programId,directoryId,criteriaId,reporttype,  user,paging);
        Page<AssDTO> dto = assessments.map(new Function<Assessment, AssDTO>() {
            @Override
            public AssDTO apply(Assessment assessment) {
                AssDTO assessmentDTO = new AssDTO();
                assessmentDTO.setId(assessment.getId());
                assessmentDTO.setName(assessment.getName());
                assessmentDTO.setNameEn(assessment.getNameEn());
                assessmentDTO.setDescription(assessment.getDescription());
                assessmentDTO.setDescriptionEn(assessment.getDescriptionEn());
                assessmentDTO.setCreatedBy(assessment.getCreatedBy());
                assessmentDTO.setCreatedDate(assessment.getCreatedDate());
                assessmentDTO.setUpdatedBy(assessment.getUpdatedBy());
                assessmentDTO.setUpdatedDate(assessment.getUpdatedDate());
                assessmentDTO.setEvaluated(assessment.getEvaluated());
                assessmentDTO.setFile(assessment.getFile());
                assessmentDTO.setComment(assessment.getComment());
                assessmentDTO.setUser(assessment.getUser());
                assessmentDTO.setViewers(assessment.getViewers());
                return assessmentDTO;
            }
        });
        return dto;

    }

    @Override
    public Page<AssessmentDTO> doSearchEn(Integer userId,String keyword, Pageable paging,Integer evaluated) {
        return repo.doSearchEN(userId,keyword,evaluated, paging);
    }
    public Page<AssDTO> doSearchEn(String keyword,Integer programId, Integer directoryId, Integer criteriaId,Integer reporttype, String user, Pageable paging) {
        Page<Assessment> assessments = repo.doSearchEN(keyword,programId,directoryId,criteriaId,reporttype, user, paging);
        Page<AssDTO> dto = assessments.map(new Function<Assessment, AssDTO>() {
            @Override
            public AssDTO apply(Assessment assessment) {
                AssDTO assessmentDTO = new AssDTO();
                assessmentDTO.setId(assessment.getId());
                assessmentDTO.setName(assessment.getName());
                assessmentDTO.setNameEn(assessment.getNameEn());
                assessmentDTO.setDescription(assessment.getDescription());
                assessmentDTO.setDescriptionEn(assessment.getDescriptionEn());
                assessmentDTO.setCreatedBy(assessment.getCreatedBy());
                assessmentDTO.setCreatedDate(assessment.getCreatedDate());
                assessmentDTO.setUpdatedBy(assessment.getUpdatedBy());
                assessmentDTO.setUpdatedDate(assessment.getUpdatedDate());
                assessmentDTO.setEvaluated(assessment.getEvaluated());
                assessmentDTO.setFile(assessment.getFile());
                assessmentDTO.setComment(assessment.getComment());
                assessmentDTO.setUser(assessment.getUser());
                assessmentDTO.setViewers(assessment.getViewers());
                return assessmentDTO;
            }
        });
        return dto;
    }

    @Override
    public AssessmentDTO getDetailVN(Integer id) {
        Optional<AssessmentDTO> entity = repo.getDetailVN(id);
        if(!entity.isPresent()){
            return null;
        }
        return entity.get();
    }

    @Override
    public AssessmentDTO getDetailEN(Integer id) {
        Optional<AssessmentDTO> entity = repo.getDetailEN(id);
        if(!entity.isPresent()){
            return null;
        }
        return entity.get();
    }

    @Override
    public Assessment findByName(String name) {
        Optional<Assessment> entity = repo.findByNameAndDelete(name, 0);
        if(!entity.isPresent()){
            return null;
        }
        return entity.get();
    }

    @Override
    public Integer getMaxOrder() {
        return repo.getMaxOrder();
    }

    @Override
    public List<Assessment> getAssessmentTemp() {
        return repo.getAssessmentTemp();
    }
    private Assessment undoPut(UndoLog undoLog)  {
        Gson g = new Gson();
        return  g.fromJson(undoLog.getRevertObject(), Assessment.class);

    }

    @Override
    public Assessment deleteAssessment(Integer id) throws Exception {
        Optional<Assessment> optional = repo.findById(id);
        if (optional.isPresent()) {
            Assessment assessment = optional.get();
            assessment.setDelete(DELETED);
            assessment.setUndoStatus(ConstantDefine.STATUS.CAN_BE_UNDO);
            return repo.save(assessment);
        } else {
            throw new Exception();
        }
    }

    @Override
    public void undo(UndoLog undoLog) throws Exception {
        List<UndoLog> undoLogs = undoLogService.findByTableNameAndIdRecordAndStatusNotOrderByCreatedDateDesc(TABLE_NAME, undoLog.getIdRecord(), 1);
        for (UndoLog log: undoLogs) {
            if (log.getId().equals(undoLog.getId())){
                Optional<Assessment> optional = repo.findById(log.getIdRecord());
                Assessment assessment;
                if (optional.isPresent()) {
                    assessment = optional.get();
                } else {
                    throw new Exception();
                }
                switch (log.getAction()) {
                    case "POST":
                        repo.deleteById(assessment.getId());
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                    case "DELETE":
                        assessment.setDelete(UNDO_DELETE);
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                    case "PUT":
                        assessment = undoPut(log);
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                }
                if(!undoLogService.existsByTableNameAndIdRecord(TABLE_NAME, assessment.getId()) && !log.getAction().equals("POST")){
                    assessment.setUndoStatus(ConstantDefine.STATUS.CAN_NOT_UNDO);
                }
                if(!log.getAction().equals("POST")){
                    repo.save(assessment);
                }
                break;
            }else {
                log.setStatus(UN_ACTIVE_UNDO_LOG);
                undoLogService.update(log, log.getId());
            }
        }
    }

    @Override
    public void replaceHyperLink(Assessment entity) {
        try {
            List<Proof> proof = proofRepository.findByProgramIdAndDeletedNot(entity.getProgramId(), DELETED);
            List<Proof> proofAssessment = new ArrayList<>();
            System.out.println("====================================" + proof.size());
            if (!proof.isEmpty()){
                for (Proof p:proof){
                    boolean isFound = entity.getContent().contains(p.getProofCode());
                    if (isFound){
                        proofAssessment.add(p);
                        String filePath = ConstantDefine.FILE_PATH.ASSESSMENT + "/" + entity.getOrderAss() + "/" + entity.getFile();
                        String tempPath = ConstantDefine.FILE_PATH.ASSESSMENT + "/" + entity.getOrderAss() + "/Temp.docx";
                        String url = ConstantDefine.FILE_PATH.DEFAULT_PATH;

                        if (!p.getListExhFile().isEmpty()){
                            ExhibitionFile e = p.getListExhFile().get(0);
                            if (e.getFileName().contains(".jpg") || e.getFileName().contains(".jpeg") || e.getFileName().contains(".png") || e.getFileName().contains(".mp4")){
                                url = ConstantDefine.FILE_PATH.IMAGE_PATH + p.getId() + "/" + e.getFileName() + "#" + p.getProofName().replace(" ", "-");
                                Path template = Paths.get(e.getFilePath() + "/" + e.getFileName());
                                Path dir = Paths.get("/var/www/html/vuexy/assets/vnua/" + p.getId());
                                Path file = Paths.get("/var/www/html/vuexy/assets/vnua/" + p.getId() + "/" + e.getFileName());
                                if (!Files.exists(dir)){
                                    Files.createDirectories(dir);
                                }
                                FileUploadUtil.cleanDir("/var/www/html/vuexy/assets/vnua/" + p.getId());
                                Files.copy(template, file, StandardCopyOption.REPLACE_EXISTING);
                            } else {
                                url = ConstantDefine.FILE_PATH.OPEN_PROOF + p.getId() + "#" + p.getProofName().replace(" ", "-");
                            }
                        }

                        String tooltip = p.getProofName();
//                        URI uri = new URI("https", "192.168.2.8:9980", "/browser/20c06f5/cool.html", "WOPISrc=http://192.168.2.8:8388/assessment/proof/wopi/files/" + p.getId() + "#" + p.getProofName().replace(" ", "-"), null);
                        DocumentWordUtilV5.processFileWord(filePath, tempPath, p.getProofCode(), url, tooltip);
                        Path file = Paths.get(filePath);
                        Path temp = Paths.get(tempPath);
                        Files.copy(temp, file, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
                if (!proofAssessment.isEmpty()){
                    File file = new File(ConstantDefine.FILE_PATH.ASSESSMENT + "/" + entity.getOrderAss() + "/Temp.docx");
                    if (file != null){
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            log.error("HyperLink - " + e);
        }
    }


    @Override
    public Assessment create(Assessment entity) {
        return repo.save(entity);
    }

    @Override
    public Assessment retrieve(Integer id) {
        Optional<Assessment> entity = repo.findById(id);
        if (!entity.isPresent()){
            return null;
        }
        return entity.get();
    }

    @Override
    public void update(Assessment entity, Integer id) {
        repo.save(entity);
    }

    @Override
    public void delete(Integer id) {
        repo.deleteById(id);
    }
    @Override
    public boolean deleteAssessment(Integer[] ids, Gson g, String deleteBy, HttpServletRequest request) throws Exception {
        for (Integer id : ids) {
            Assessment assessment = deleteAssessment(id);
            if (assessment == null) {
                return false;
            }
            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .requestObject(g.toJson(assessment, Assessment.class))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Xóa báo cáo bởi " + deleteBy)
                    .createdDate(LocalDateTime.now())
                    .createdBy(deleteBy)
                    .tableName(TABLE_NAME)
                    .idRecord(assessment.getId())
                    .build();
            undoLogService.create(undoLog);
        }
        return true;
    }

    @Override
    public  List<Integer> getAssessmentByCreatedBy(String name) {
        return repo.getAssessmentByCreatedBy(name);
    }

    @Override
    public AssessmentDTO formatObj(Assessment entity) {
        AssessmentDTO dto= new AssessmentDTO(entity.getId(), entity.getName(), entity.getNameEn(), entity.getDescription(), entity.getDescriptionEn(), entity.getCreatedBy(), entity.getCreatedDate(), entity.getUpdatedBy(), entity.getUpdatedDate(),entity.getEvaluated(), entity.getFile(), entity.getComment(), entity.getReportType(),entity.getProgramId(), null,null,entity.getDirectoryId(), null, null,entity.getCriteriaId(), null, null);
if(entity.getDirectoryId() != null){
        Optional<DirectoryDTO> directoryDTO = directoryService.finbyID(entity.getDirectoryId());
        directoryDTO.ifPresent(value -> dto.setDirectoryName(value.getName()));
        directoryDTO.ifPresent(value -> dto.setDescriptionEn(value.getNameEn()));

        Optional<ProgramsDTO> programsDTO = programsService.finbyID(entity.getProgramId());
        if (programsDTO.isPresent()) {
            dto.setProgramName(programsDTO.get().getName());
            dto.setProgramNameEn(programsDTO.get().getNameEn());
            System.out.println(programsDTO.toString()+ "oke");
        }}

        if(entity.getCriteriaId() != null){
            Optional<CriteriaDTO> criteriaDTO = criteriaService.finbyID(entity.getCriteriaId());
            if (criteriaDTO.isPresent()) {
                CriteriaDTO criteriaDTO1 = criteriaDTO.get();
                dto.setCriteriaName(criteriaDTO1.getName());
//                System.out.println("dto.  tieu chis= " + criteriaDTO1.toString());
            }}
        return dto;
    }
}
