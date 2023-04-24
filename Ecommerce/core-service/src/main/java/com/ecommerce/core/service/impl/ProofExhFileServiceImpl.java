package com.ecommerce.core.service.impl;

import com.ecommerce.core.entities.ProofExhFile;
import com.ecommerce.core.repositories.ProofExhFileRepository;
import com.ecommerce.core.service.ProofExhFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProofExhFileServiceImpl implements ProofExhFileService {
    @Autowired
    ProofExhFileRepository proofExhFileRepository;

    @Override
    public ProofExhFile create(ProofExhFile entity) {
        return proofExhFileRepository.save(entity);
    }

    @Override
    public ProofExhFile retrieve(Integer id) {
        return null;
    }

    @Override
    public void update(ProofExhFile entity, Integer id) {
        proofExhFileRepository.save(entity);
    }

    @Override
    public void delete(Integer id) throws Exception {
        proofExhFileRepository.deleteById(id);
    }

    @Override
    public List<ProofExhFile> getListProofExhFileByProofId(Integer id) {
        return proofExhFileRepository.getListProofExhFileByProofId(id);
    }

    @Override
    public ProofExhFile findById(Integer id) {
        return proofExhFileRepository.findById(id).get();
    }
}
