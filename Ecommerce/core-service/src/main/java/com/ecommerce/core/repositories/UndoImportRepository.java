package com.ecommerce.core.repositories;

import com.ecommerce.core.entities.UndoImport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UndoImportRepository extends JpaRepository<UndoImport, Integer> {
    List<UndoImport> findByUndoIdAndStatus(Integer idRecord, Integer status);
    List<UndoImport> findByUndoIdAndStatusAndTableName(Integer idRecord, Integer status, String tableName);
}
