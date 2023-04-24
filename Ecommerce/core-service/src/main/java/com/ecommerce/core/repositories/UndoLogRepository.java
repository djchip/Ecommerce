package com.ecommerce.core.repositories;

import com.ecommerce.core.entities.UndoLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UndoLogRepository extends JpaRepository<UndoLog, Integer> {

    List<UndoLog> findByRequestObjectStartsWithOrderByCreatedDateDesc(String requestObject);

    List<UndoLog> findByTableNameAndIdRecordAndStatusNotOrderByCreatedDateDesc(String tableName, Integer idRecord, Integer status);

    Boolean existsByTableNameAndIdRecord(String tableName, Integer idRecord);

    @Query(value = "SELECT u FROM UndoLog u WHERE 1=1 AND (?1 IS NULL OR lower(u.tableName) like %?1%) and (?2 is null or u.action like ?2%) AND u.status <> 1 " +
            "AND (?3 is null or u.createdDate >= ?3) AND (?4 is null or u.createdDate < ?4) AND (?5 is null or lower(u.createdBy) like %?5%) ORDER BY u.createdDate desc,u.updatedDate desc")
    Page<UndoLog> doSearch(String tableName, String action, LocalDateTime startDate, LocalDateTime endDate, String createdBy, Pageable paging);
}
