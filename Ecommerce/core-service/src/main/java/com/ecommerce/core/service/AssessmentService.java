package com.ecommerce.core.service;

import com.google.gson.Gson;
import com.ecommerce.core.dto.AssDTO;
import com.ecommerce.core.dto.AssessmentDTO;
import com.ecommerce.core.entities.Assessment;
import com.ecommerce.core.entities.UndoLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface AssessmentService extends IRootService<Assessment>{

    Page<AssessmentDTO> doSearch(Integer userId,String keyword, Pageable paging, Integer evaluated);

    Page<AssessmentDTO> doSearchEn(Integer userId,String keyword, Pageable paging,Integer evaluated);
    Page<AssDTO> doSearch(String keyword,Integer programId, Integer directoryId, Integer criteriaId,  Integer reporttype,String user, Pageable paging);

    Page<AssDTO> doSearchEn(String keyword,Integer programId, Integer directoryId, Integer criteriaId,Integer reporttype, String user, Pageable paging);

    AssessmentDTO getDetailVN(Integer id);

    AssessmentDTO getDetailEN(Integer id);

    Assessment findByName(String name);

    Integer getMaxOrder();

    List<Assessment> getAssessmentTemp();

    Assessment deleteAssessment(Integer id) throws Exception;
    void undo(UndoLog undoLog) throws Exception;

//    Assessment findById(Integer id);

    void replaceHyperLink(Assessment entity);

    boolean deleteAssessment(Integer[] ids, Gson g, String deleteBy, HttpServletRequest request) throws Exception;

    List<Integer> getAssessmentByCreatedBy(String name);


    AssessmentDTO formatObj(Assessment entity);


}
