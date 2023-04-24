package com.ecommerce.core.service;

import com.ecommerce.core.entities.Programs;
import com.ecommerce.core.entities.UndoLog;
import com.google.gson.Gson;
import com.ecommerce.core.dto.ProgramsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface ProgramsService extends IRootService<Programs> {
    Page<ProgramsDTO> doSearch(String keyword, String username, Pageable pageable);
    Page<ProgramsDTO> doSearchEn(String keyword, String username, Pageable pageable);
    Programs findByProgramsName(String programsName);
    Page<Programs> findByName(String name,Pageable pageable);
    void delete(Integer id) throws Exception;
    Programs save(Programs programs);
    Page<Programs> findAll(Pageable pageable);
    Programs findById(int id);
    ProgramsDTO findByIdd(int id);
    Programs getByIdEn(Integer id);
    Programs getById(Integer id);
    Programs deletePro(Integer id) throws Exception;
    void undo(UndoLog undoLog) throws Exception;
    List<Programs> getSelectbox();

    List<Programs> findProgramsByUsername(String username);

    List<Programs> getAllProgramsByYear(int byYear);
    ProgramsDTO formatObj(Programs entity);
    Optional<ProgramsDTO> finbyID(Integer id);
    Programs findByDirectoryName(String name);
    boolean deletePrograms(Integer[] ids, Gson g, String createdBy, HttpServletRequest request) throws Exception;
    Programs findProgramByProofId(Integer id);
    List<Programs> findByOrgId(Integer orgId);
    boolean existsByOrganizationIdAndDelete(Integer orgId, Integer delete);

    List<Integer> getListYearInDataBase();

    List<Integer> getProgramQuantityByYear();

    List<Programs> findByOrgIdAndCategoryId(Integer orgId, Integer categoryId);
}
