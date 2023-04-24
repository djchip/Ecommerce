package com.ecommerce.core.service.impl;

import com.ecommerce.core.entities.DocumentFile;
import com.ecommerce.core.repositories.DocumentFileRepository;
import com.ecommerce.core.service.DocumentFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class DocumentFileServiceImpl implements DocumentFileService {
    @Autowired
    DocumentFileRepository repo;

    @Override
    public DocumentFile create(DocumentFile entity) {
        return repo.save(entity);
    }

    @Override
    public DocumentFile retrieve(Integer id) {
        return null;
    }

    @Override
    public void update(DocumentFile entity, Integer id) {

    }

    @Override
    public void delete(Integer id) throws Exception {
        repo.deleteById(id);
    }

    @Override
    public List<DocumentFile> getListFileByDocumentId(Integer id) {
        return repo.getListFileByDocumentId(id);
    }

    @Override
    public DocumentFile findById(Integer id) {
        return repo.findById(id).get();
    }

    @Override
    public List<DocumentFile> getListDocumentFileByDocumentId(Integer documentId) {
        return repo.getListDocumentFileByDocumentId(documentId);
    }
}
