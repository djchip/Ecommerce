package com.ecommerce.core.service.impl;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.entities.Document;
import com.ecommerce.core.entities.UndoLog;
import com.ecommerce.core.exceptions.ExistsCriteria;
import com.ecommerce.core.repositories.DocumentRepository;
import com.ecommerce.core.service.UndoLogService;
import com.google.gson.Gson;
import com.ecommerce.core.dto.DocumentReportDTO;
import com.ecommerce.core.dto.IdFileDTO;
import com.ecommerce.core.dto.ListDocumentDTO;
import com.ecommerce.core.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentServiceImpl implements DocumentService {
    private static final Integer DELETE = 1;
    private static final Integer UNDO_DELETE = 0;
    private static final String TABLE_NAME = "document";
    private static final Integer UN_ACTIVE_UNDO_LOG = 1;

    @Autowired
    DocumentRepository repo;

    @Autowired
    UndoLogService undoLogService;

    @Override
    public List<Document> getDocument() {
        return repo.findAll();
    }

    @Override
    public List<ListDocumentDTO> getListDocumentDTO() {
        return repo.getListDocumentDTO();
    }


    @Override
    public Page<ListDocumentDTO> doSearch(String keyword, Pageable paging) {
        return repo.doSearch(keyword, paging);
    }

    @Override
    public Page<ListDocumentDTO> doSearchByUnit(Integer unitId, Pageable paging) {
        return repo.doSearchByUnit(unitId, paging);
    }

    @Override
    public List<DocumentReportDTO> getListByUnit(Integer unitId) {
        List<Object[]> listByUnit = repo.getListByUnit(unitId);
        List<DocumentReportDTO> dto = new ArrayList<>();
        for (Object[] item: listByUnit){
            DocumentReportDTO reportDTO = new DocumentReportDTO();
            reportDTO.setDocumentNumber((String) item[0]);
            reportDTO.setDocumentName((String) item[1]);
            reportDTO.setDocumentNameEn((String) item[2]);
            reportDTO.setDocumentDes((String) item[3]);
            reportDTO.setDocumentDesEn((String) item[4]);
            reportDTO.setFileName((String) item[5]);
            reportDTO.setDocumentType((String) item[6]);
            reportDTO.setDocumentField((String) item[7]);
            reportDTO.setReleaseDate((Date) item[8]);
            dto.add(reportDTO);
        }
        return dto;
    }

    @Override
    public ListDocumentDTO getDetailDocument(Integer id) {
        return repo.getDetailDocument(id);
    }

    @Override
    public Document findByDocumentNumber(String documentNumber) {
        Optional<Document> entity = repo.findByDocumentNumber(documentNumber);
        if (!entity.isPresent()) {
            return null;
        }
        return entity.get();
    }

    @Override
    public String getFilenameById(Integer id) {
        return repo.getFilenameById(id);
    }

    @Override
    public List<IdFileDTO> getListIdAndFilename(String[] ids) {
        List<IdFileDTO> rs = new ArrayList<>();
        for(String id : ids) {
            IdFileDTO dto = new IdFileDTO();
            dto = repo.getListIdAndFilename(Integer.parseInt(id));
            rs.add(dto);
        }
        return rs;
    }

    @Override
    public Document findByDocumentName(String documentName) {
        return repo.findByDocumentName(documentName);
    }

    @Override
    public Document deleteDocumt(Integer id) throws Exception {
        Optional<Document> optional = repo.findById(id);
        if (optional.isPresent()) {
            Document document = optional.get();
            document.setDelete(DELETE);
//            document.setUndoStatus(ConstantDefine.STATUS.CAN_NOT_UNDO);
            return repo.save(document);
        } else {
            throw new Exception();
        }
    }

    private Document undoPut(UndoLog undoLog) {
        Gson g = new Gson();
        return g.fromJson(undoLog.getRevertObject(), Document.class);
    }

    @Override
    public void undo(UndoLog undoLog) throws Exception {
        List<UndoLog> undoLogs = undoLogService.findByTableNameAndIdRecordAndStatusNotOrderByCreatedDateDesc(TABLE_NAME, undoLog.getIdRecord(), 1);
        for (UndoLog log : undoLogs) {
            if (log.getId().equals(undoLog.getId())) {
                Optional<Document> optional = repo.findById(log.getIdRecord());
                Document document;
                if (optional.isPresent()) {
                    document = optional.get();
                } else {
                    throw new Exception();
                }
                switch (log.getAction()) {
                    case "POST":
                        repo.deleteById(document.getId());
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                    case "DELETE":
                        if(repo.findByDocumentNameAndDelete(document.getDocumentName(), 0).isEmpty()){
                        document.setDelete(UNDO_DELETE);
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;}
                        else {
                            throw new ExistsCriteria("văn bản đã tồn tại : ");
                        }
                    case "PUT":
                        document = undoPut(log);
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                }
                if (!undoLogService.existsByTableNameAndIdRecord(TABLE_NAME, document.getId()) && !log.getAction().equals("POST")) {
                    document.setUndoStatus(ConstantDefine.STATUS.CAN_NOT_UNDO);
                }
                if (!log.getAction().equals("POST")) {
                    repo.save(document);
                }
                break;
            } else {
                log.setStatus(UN_ACTIVE_UNDO_LOG);
                undoLogService.update(log, log.getId());
            }
        }
    }


    @Override
    public Document create(Document entity) {
        return repo.save(entity);
    }

    @Override
    public Document retrieve(Integer id) {
        Optional<Document> entity = repo.findById(id);
        if (!entity.isPresent()) {
            return null;
        }
        return entity.get();
    }

    @Override
    public void update(Document entity, Integer id) {
        repo.save(entity);
    }

    @Override
    public void delete(Integer id) {
        repo.deleteById(id);
    }

    @Override
    public boolean deleteDocumt(Integer[] ids, Gson g, String deleteBy, HttpServletRequest request) throws Exception {
        for (Integer id : ids) {
            Document document = deleteDocumt(id);
            if (document == null) {
                return false;
            }
            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .requestObject(g.toJson(document, Document.class))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Xóa văn bản bởi " + deleteBy)
                    .createdDate(LocalDateTime.now())
                    .createdBy(deleteBy)
                    .tableName(TABLE_NAME)
                    .idRecord(document.getId())
                    .build();
            undoLogService.create(undoLog);
        }
        return true;
    }
}
