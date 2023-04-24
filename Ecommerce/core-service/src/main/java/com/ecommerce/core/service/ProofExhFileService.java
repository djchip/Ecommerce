package com.ecommerce.core.service;

import com.ecommerce.core.entities.ProofExhFile;

import java.util.List;

public interface ProofExhFileService extends IRootService<ProofExhFile>{
    List<ProofExhFile> getListProofExhFileByProofId(Integer id);

    ProofExhFile findById(Integer id);
}
