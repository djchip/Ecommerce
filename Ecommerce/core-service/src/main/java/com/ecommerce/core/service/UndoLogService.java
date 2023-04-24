package com.ecommerce.core.service;

import com.ecommerce.core.dto.UnitDTO;
import com.ecommerce.core.entities.UndoLog;
import com.ecommerce.core.entities.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface UndoLogService extends IRootService<UndoLog> {
    List<UndoLog> findByRequestObjectStartsWithOrderByCreatedDateDesc(String requestObject);

    List<UndoLog> findByTableNameAndIdRecordAndStatusNotOrderByCreatedDateDesc(String tableName, Integer idRecord, Integer status);

    Boolean existsByTableNameAndIdRecord(String tableName, Integer idRecord);

    Page<UndoLog> doSearch(String tableName, String action, LocalDateTime startDate, LocalDateTime endDate, String createdBy, Pageable paging);

    void filterAndRedirect(UndoLog undoLog) throws Exception;

    UndoLog findById(Integer id);

    List<UnitDTO> formatObj(List<Unit> entity);
}
