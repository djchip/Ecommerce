package com.ecommerce.core.service;

import com.ecommerce.core.dto.ProofDTO;
import com.ecommerce.core.entities.Proof;
import com.ecommerce.core.entities.UndoLog;
import com.ecommerce.core.entities.Unit;
import com.ecommerce.core.exceptions.*;
import com.google.gson.Gson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;


public interface ProofService extends IRootService<Proof> {
    Page<ProofDTO> doSearch(Integer programId, Integer standardId, Integer criteriaId, String username, Pageable pageable );

    List<Proof> getProof();

    Page<ProofDTO> doSearchContent(Integer programId, Integer standardId, Integer criteriaId, String keyword, String username, Pageable pageable);

    List<Proof> findByProofName(String proofName);

    Proof findByProofCode(String proofCode);

    void importAutomatic(MultipartFile file, String createdBy, Integer organizationId, Integer programId, String forWhat, HttpServletRequest request) throws IOException, DetectExcelException, ExistsCriteria, ExistsDirectory, ExistsProofException, NotSameCodeException, InvalidProofCode, WrongFormat;

    List<Proof> getAllProofNeedFile(String proofCode, Integer programId);

    void updateExhFile(Integer id, Integer proofId);

    Integer getMaxOrderOfStandard(Integer stanId);

    Integer getMaxOrderOfCriteria(Integer criId);

    Proof findById(int id);

    List<Proof> findAll();

    List<Proof> findByStandardId(Integer id);

    List<Proof> findByCriteriaId(Integer id);

    Unit getUnitByUsername(String creater);



    Proof deleteProof(Integer id);

    ProofDTO getDetailProof(Integer id);

    List<Proof> findByUnit(Integer unitId);

    Proof deleteProoff(Integer id) throws Exception;

    void undo(UndoLog undoLog) throws Exception;

    ProofDTO formatObj(Proof entity);

    List<Proof> findByProId(Integer proId);

    Page<Proof> getProofHaveNotFile(Integer programId, Pageable paging);

    void saveAll(List<Proof> proofs);

    boolean deleteMultiProof(Integer[] ids, Gson g, String userFromToken, HttpServletRequest request) throws Exception;

    void updateExhCode(String oldExhCode, String newExhCode, int orgId);

    List<ProofDTO> formatObj(List<Proof> proofs);

    List<Proof> getListProofByProgramId(int id);

    List<Proof> findByOrganizationId(Integer id);

}
