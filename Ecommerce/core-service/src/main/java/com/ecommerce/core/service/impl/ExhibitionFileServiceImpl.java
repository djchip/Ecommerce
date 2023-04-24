package com.ecommerce.core.service.impl;

import com.ecommerce.core.entities.ExhibitionFile;
import com.ecommerce.core.repositories.ExhibitionFileRepository;
import com.ecommerce.core.service.ExhibitionFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExhibitionFileServiceImpl implements ExhibitionFileService {

	@Autowired
    ExhibitionFileRepository repo;
	
	@Override
	public ExhibitionFile create(ExhibitionFile entity) {
		// TODO Auto-generated method stub
		return repo.save(entity);
	}

	@Override
	public ExhibitionFile retrieve(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(ExhibitionFile entity, Integer id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Integer getLatestId() {
		return repo.getLatestId() != null ? repo.getLatestId() : 0;
	}

	@Override
	public ExhibitionFile getExhFileByProofId(Integer id) {
		return repo.getExhFileByProofId(id);
	}

	@Override
	public List<ExhibitionFile> getListExhFileByProofId(Integer id) {
		return repo.getListExhFileByProofId(id);
	}

	@Override
	public ExhibitionFile findById(Integer id) {
		return repo.findById(id).get();
	}

	@Override
	public Optional<ExhibitionFile> finbyID(Integer id) {
		return repo.findById(id);

	}
}
