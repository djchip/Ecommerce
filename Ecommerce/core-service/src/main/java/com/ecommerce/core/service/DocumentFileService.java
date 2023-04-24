package com.ecommerce.core.service;

import com.ecommerce.core.entities.DocumentFile;

import java.util.List;

public interface DocumentFileService extends IRootService<DocumentFile>{
    List<DocumentFile> getListFileByDocumentId(Integer id);

    DocumentFile findById(Integer id);

    List<DocumentFile> getListDocumentFileByDocumentId(Integer documentId);
}
