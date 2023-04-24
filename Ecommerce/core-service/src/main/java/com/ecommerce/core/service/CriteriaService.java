package com.ecommerce.core.service;

import com.ecommerce.core.entities.Criteria;
import com.ecommerce.core.entities.UndoLog;
import com.google.gson.Gson;
import com.ecommerce.core.dto.CriteriaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface CriteriaService extends IRootService<Criteria> {
	List<Criteria> getDirectory();
    List<Criteria> getCriteria();

    Page<CriteriaDTO> doSearch(String keyword, Integer proId, Integer stanId, Integer orgId, String userName, Pageable paging);
    Page<CriteriaDTO> doSearchExcelEn(String keyword, Integer proId, Integer stanId, Integer orgId, String userName, Pageable paging, String listId);

    Page<CriteriaDTO> doSearchExcel(String keyword, Integer proId, Integer stanId, Integer orgId, String userName, Pageable paging, String listId);
    Page<CriteriaDTO> doSearchEn(String keyword, Integer proId, Integer stanId, Integer orgId,String userName, Pageable paging);
    Criteria findByDirectoryName(String name);
    List<Criteria> save(List<Criteria> criteria);
    Criteria save(Criteria directory);
    CriteriaDTO getById(Integer id);
    CriteriaDTO getByIdEn(Integer id);
    Criteria findById(int id);
    String findNameById(Integer id);
    Integer getMaxOrder(Integer stanId);
    Criteria deleteDir(Integer id) throws Exception;
    void undo(UndoLog undoLog) throws Exception;
    void importCriteria(MultipartFile file, String userFromToken, Integer organizationId,Integer categoryId, HttpServletRequest request) throws Exception;
    List<Criteria> findByStandardId(Integer id, Integer deleted);
    List<Criteria> getListCriteriaBySta(Integer id);
    CriteriaDTO formatObj(Criteria entity);
    List<CriteriaDTO> formatObj(List<Criteria> entity);
    Optional<CriteriaDTO> finbyID(Integer id);
    boolean checkCodeExists(String code, Integer orgId, Integer staId);
    int deleteCriteria(Integer[] ids, Gson g, String deleteBy, HttpServletRequest request) throws Exception;
    void updateExhCode(String oldExhCode, String newExhCode, int orgId);
    List<Criteria> findByProgramIdAndDeleteNot(Integer programId, Integer delete);
    List<Criteria> findAllByDelete();

    List<Integer> findAllCriIdByUsername(String username);

    List<Criteria> findByOrganizationId(Integer id);

    List<Criteria> getCriteriaByDirectoryId(Integer id, String userFromToken);


    List<Criteria> getListCriteriaByOrgIdAndCategoryId(Integer orgId, Integer categoryId);

    List<Criteria> getListCriteriaByOrgIdAndCategoryIdAndStandardId(Integer orgId, Integer categoryId, Integer standardId);

}
