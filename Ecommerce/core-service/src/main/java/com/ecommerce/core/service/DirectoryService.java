package com.ecommerce.core.service;

import com.ecommerce.core.entities.Directory;
import com.ecommerce.core.entities.UndoLog;
import com.ecommerce.core.exceptions.DetectExcelException;
import com.ecommerce.core.exceptions.ExistsDirectory;
import com.ecommerce.core.exceptions.NotSameCodeException;
import com.google.gson.Gson;
import com.ecommerce.core.dto.DirectoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface DirectoryService extends IRootService<Directory> {

    List<Directory> getDirectory();

    List<Directory> getDirectoryByPro(Integer id);
    List<Directory> getDirectoryByProgramId(Integer id, String userFromToken);

    Page<Directory> doSearch(String keyword, Integer orgId, Integer programId, String username, Pageable paging);

    Page<DirectoryDTO> doSearchExcel(String keyword, Integer orgId, Integer programId, String username, Pageable paging, String listId);

    Page<DirectoryDTO> doSearchEn(String keyword, Integer proId, Pageable paging);

    Directory findByDirectoryName(String name);

    List<DirectoryDTO> findAllDTO();

    List<Directory> save(List<Directory> directory);

    Directory save(Directory directory);

    Directory findById(int id);

//    DetailStandardDTO getDetailDirectory(int id);

    Optional<DirectoryDTO> finbyID(Integer id);

    Directory getById(int id);

    DirectoryDTO getByIdEn(int id);

    Directory findByCode(String code);

    Integer getMaxOrder();

    String findNameByProofId(Integer id);

    DirectoryDTO formatObj(Directory entity);

    Directory deleteDir(Integer id) throws Exception;

    void undo(UndoLog undoLog) throws Exception;

    void importDirectory(MultipartFile file, String userFromToken, Integer organizationId,Integer categoryId, HttpServletRequest request) throws IOException, DetectExcelException, ExistsDirectory, NotSameCodeException;

    List<Directory> finByOrgId(Integer orgId, String username);

    List<Directory> findByOrgId(Integer orgId, Integer categoryId, String username);

    List<Directory> findByOrganizationIdAndUsername(Integer orgId, String username);

    List<Directory> findByOrganizationIdAndUserId(Integer orgId, String userName);

    boolean checkCodeExists(String code, Integer orgId);

    List<Directory> findByOrgId(Integer id);


    List<Directory> findByOrganizationId(Integer id);

    int deleteDirectories(Integer[] standard, Gson g, String userFromToken, HttpServletRequest request) throws Exception;

    void updateExhCode(String oldExhCode, String newExhCode, int orgId);

    List<Directory> findAll();

    List<Directory> SelectboxSta();

    List<DirectoryDTO> formatObj(List<Directory> directories);

    List<Integer> findAllStaIdByUsername(String username);

    List<Directory> getListStandardByOrgIdAndCategoryId(Integer orgId,Integer categoryId);
    Directory findByIdAndAndDelete(Integer id);

}
