package com.ecommerce.core.repositories;

import com.ecommerce.core.entities.DocumentFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentFileRepository extends JpaRepository<DocumentFile, Integer> {

    @Query(value = "SELECT d FROM DocumentFile d WHERE 1=1 AND d.documentId = ?1")
    List<DocumentFile> getListFileByDocumentId(Integer id);

    @Query(value = "SELECT df FROM DocumentFile df WHERE 1=1 AND df.documentId = ?1")
    List<DocumentFile> getListDocumentFileByDocumentId(Integer documentId);
}
