package com.ecommerce.core.repositories;


import com.ecommerce.core.entities.ExhibitionFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExhibitionFileRepository extends JpaRepository<ExhibitionFile,Integer> {

    @Query(value = "SELECT MAX(e.id) FROM ExhibitionFile e")
    Integer getLatestId();

    @Query(value = "SELECT e FROM ExhibitionFile e JOIN ProofExhFile pe ON e.id = pe.exhFileId JOIN Proof p ON pe.proofId = p.id WHERE p.id = ?1")
    ExhibitionFile getExhFileByProofId(Integer id);

    @Query(value = "SELECT e FROM ExhibitionFile e JOIN ProofExhFile pe ON e.id = pe.exhFileId JOIN Proof p ON p.id = pe.proofId WHERE p.id = ?1")
    List<ExhibitionFile> getListExhFileByProofId(Integer id);

    Optional<ExhibitionFile> findById(Integer id);


}
