package com.ecommerce.core.service;

import com.ecommerce.core.entities.Document;
import com.ecommerce.core.entities.UndoLog;
import com.google.gson.Gson;
import com.ecommerce.core.dto.DocumentReportDTO;
import com.ecommerce.core.dto.IdFileDTO;
import com.ecommerce.core.dto.ListDocumentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface DocumentService extends IRootService<Document>{
    List<Document> getDocument();

    List<ListDocumentDTO> getListDocumentDTO();

    Page<ListDocumentDTO> doSearch(String keyword, Pageable paging);

    Page<ListDocumentDTO> doSearchByUnit(Integer unitId, Pageable paging);

    List<DocumentReportDTO> getListByUnit(Integer unitId);

    ListDocumentDTO getDetailDocument(Integer id);
    Document findByDocumentNumber(String documentNumber);

    String getFilenameById(Integer id);

    List<IdFileDTO> getListIdAndFilename(String[] ids);

    Document findByDocumentName(String documentName);
    Document deleteDocumt (Integer id) throws Exception;
     void undo (UndoLog undoLog) throws Exception;

    boolean deleteDocumt(Integer[] ids, Gson g, String deleteBy, HttpServletRequest request) throws Exception;
}
