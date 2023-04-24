package com.ecommerce.core.service;

import java.util.List;

import com.ecommerce.core.dto.TreeNodeDTO;
import com.ecommerce.core.entities.ExhibitionCollection;

public interface ExhibitionCollectionService extends IRootService<ExhibitionCollection> {

	List<TreeNodeDTO> getListTreeNode(Integer programId);
	
	Boolean checkExistedFile(Integer programId, String name);
	
	List<ExhibitionCollection> getListCollectionsByProgramId(Integer programId);

	List<ExhibitionCollection> findByIsDirectoryAndProgramIdAndPath(Integer isDirectory, Integer programId, String path);

	Boolean existsByIsDirectoryAndProgramIdAndPath(Integer isDirectory, Integer programId, String path);
}
