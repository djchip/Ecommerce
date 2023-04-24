package com.ecommerce.core.service;

import com.google.gson.Gson;
import com.ecommerce.core.entities.UndoImport;

import java.util.List;
import java.util.Map;

public interface UndoImportService extends IRootService<UndoImport> {
    void insertImportUndo(Map.Entry<?, ?> entry, Integer undoId, Integer idRecord, String createBy, Gson g, String tableName);

    List<UndoImport> findByUndoIdAndStatus(Integer idRecord, Integer status);

    List<UndoImport> findByUndoIdAndStatusAndTableName(Integer idRecord, Integer status, String tableName);
}
