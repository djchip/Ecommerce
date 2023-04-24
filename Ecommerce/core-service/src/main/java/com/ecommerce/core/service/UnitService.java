package com.ecommerce.core.service;

import com.google.gson.Gson;
import com.ecommerce.core.entities.UndoLog;
import com.ecommerce.core.entities.Unit;
import com.ecommerce.core.exceptions.DetectExcelException;
import com.ecommerce.core.exceptions.ExistsUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


public interface UnitService extends IRootService<Unit>{

    Page<Unit> doSearch(String keyword, Integer classify, Pageable paging);

    Unit findByUnitName(String unitName);

    Unit findByUnitCode(String unitCode);

    List<Unit> findAll();

    List<Unit> save(List<Unit> units);

    List<Unit> getListUnits();
    
    List<Unit> getListActiveUnits();

    Unit findById(Integer id);

    Unit deleteUnit(Integer id) throws Exception;
    void undo(UndoLog undoLog) throws Exception;
    Optional<Unit> finbyID(Integer id);

    Unit getUnitByUsername(String userName);

    boolean deleteUnit(Integer[] ids, Gson g, String deleteBy, HttpServletRequest request) throws Exception;

    void importLog (MultipartFile file, String userFromToken, HttpServletRequest request) throws IOException, DetectExcelException, ExistsUnit;

}
