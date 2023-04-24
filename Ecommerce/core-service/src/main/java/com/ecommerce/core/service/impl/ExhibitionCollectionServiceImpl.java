package com.ecommerce.core.service.impl;

import java.util.List;
import java.util.Optional;

import com.ecommerce.core.entities.ExhibitionCollection;
import com.ecommerce.core.repositories.ExhibitionCollectionRepository;
import com.ecommerce.core.service.ExhibitionCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.core.dto.TreeNodeDTO;

@Service
public class ExhibitionCollectionServiceImpl implements ExhibitionCollectionService {

	@Autowired
    ExhibitionCollectionRepository repo;

	@Override
	public ExhibitionCollection create(ExhibitionCollection entity) {
		// TODO Auto-generated method stub
		return repo.save(entity);
	}

	@Override
	public ExhibitionCollection retrieve(Integer id) {
		// TODO Auto-generated method stub
		Optional<ExhibitionCollection> entity = repo.findById(id);
		return entity.orElse(null);
	}

	@Override
	public void update(ExhibitionCollection entity, Integer id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Integer id) throws Exception {
		// TODO Auto-generated method stub
		repo.deleteById(id);
	}

	@Override
	public List<TreeNodeDTO> getListTreeNode(Integer programId) {
		// TODO Auto-generated method stub
		return repo.getListTreeNode(programId);
	}

	@Override
	public Boolean checkExistedFile(Integer programId, String name) {
		// TODO Auto-generated method stub
		List<ExhibitionCollection> lstResult = repo.getFileByName(programId, name);
		return lstResult != null && !lstResult.isEmpty();
	}

	@Override
	public List<ExhibitionCollection> getListCollectionsByProgramId(Integer programId) {
		// TODO Auto-generated method stub
		return repo.getListCollectionByPrograms(programId);
	}

	@Override
	public List<ExhibitionCollection> findByIsDirectoryAndProgramIdAndPath(Integer isDirectory, Integer programId, String path) {
		return repo.findByIsDirectoryAndProgramIdAndPath(isDirectory, programId, path);
	}

	@Override
	public Boolean existsByIsDirectoryAndProgramIdAndPath(Integer isDirectory, Integer programId, String path) {
		return repo.existsByIsDirectoryAndProgramIdAndPath(isDirectory, programId, path);
	}

}
