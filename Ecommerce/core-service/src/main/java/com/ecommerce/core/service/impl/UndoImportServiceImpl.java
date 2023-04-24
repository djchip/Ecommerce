package com.ecommerce.core.service.impl;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.entities.UndoImport;
import com.ecommerce.core.repositories.UndoImportRepository;
import com.ecommerce.core.service.UndoImportService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class UndoImportServiceImpl implements UndoImportService {

    @Autowired
    private UndoImportRepository repository;

    @Override
    public void insertImportUndo(Map.Entry<?, ?> entry, Integer undoId, Integer idRecord, String createBy, Gson g, String tableName) {
        UndoImport undoImport = UndoImport.undoImportBuilder()
                .requestObject(g.toJson(entry.getKey()))
                .revertObject(g.toJson(entry.getValue()))
                .description("Create by " + createBy)
                .idRecord(idRecord)
                .undoId(undoId)
                .status(ConstantDefine.STATUS.UNDO_NEW)
                .tableName(tableName)
                .createdBy(createBy)
                .createdDate(LocalDateTime.now())
                .build();
        repository.save(undoImport);
    }

    @Override
    public List<UndoImport> findByUndoIdAndStatus(Integer idRecord, Integer status) {
        return repository.findByUndoIdAndStatus(idRecord, status);
    }

    @Override
    public List<UndoImport> findByUndoIdAndStatusAndTableName(Integer idRecord, Integer status, String tableName) {
        return repository.findByUndoIdAndStatusAndTableName(idRecord, status, tableName);
    }

    @Override
    public UndoImport create(UndoImport entity) {
        return null;
    }

    @Override
    public UndoImport retrieve(Integer id) {
        return null;
    }

    @Override
    public void update(UndoImport entity, Integer id) {
        repository.save(entity);
    }

    @Override
    public void delete(Integer id) throws Exception {

    }
}
