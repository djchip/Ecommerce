package com.ecommerce.core.repositories;

import com.ecommerce.core.entities.ProofExhFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProofExhFileRepository extends JpaRepository<ProofExhFile, Integer> {

//    @Query(value = "SELECT pe FROM ProofExhFile pe JOIN ExhibitionFile e ON e.id = pe.exhFileId JOIN Proof p ON p.id = pe.proofId WHERE p.id = ?1")
//    List<ProofExhFile> getListProofExhFileByProofId(Integer id);

    @Query(value = "SELECT pe FROM ProofExhFile pe WHERE pe.proofId = ?1")
    List<ProofExhFile> getListProofExhFileByProofId(Integer id);
}
