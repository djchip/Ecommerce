package com.ecommerce.core.service;

import com.google.gson.Gson;
import com.ecommerce.core.entities.SoftwareLog;
import com.ecommerce.core.entities.SoftwareVersion;
import com.ecommerce.core.entities.UndoLog;
import com.ecommerce.core.exceptions.DetectExcelException;
import com.ecommerce.core.exceptions.ExistsSoftwareLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface SoftwareLogService extends IRootService<SoftwareLog> {
    SoftwareLog deleteError(Integer id) throws Exception;

    SoftwareLog findByError(String error);
    List<SoftwareLog> findByVersion(String version);
    SoftwareVersion findBySVersion (String version);

    List<SoftwareLog> save(List<SoftwareLog> entity);
    SoftwareLog save(SoftwareLog softwareLog) ;


    SoftwareLog findById(Integer id);
    void undo(UndoLog undoLog) throws Exception;
    void importLog (MultipartFile file, String userFromToken) throws IOException, DetectExcelException, ExistsSoftwareLog;



    SoftwareLog formatObj(SoftwareLog entity);

    Page<SoftwareLog> doSearch(String keyword, Integer status, LocalDateTime startDate, LocalDateTime endDate, Pageable paging);

    boolean deleteUser(Integer[] ids, Gson g, String createdBy, HttpServletRequest request) throws Exception;

}
