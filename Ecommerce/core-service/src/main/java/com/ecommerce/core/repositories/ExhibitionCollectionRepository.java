package com.ecommerce.core.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ecommerce.core.dto.TreeNodeDTO;
import com.ecommerce.core.entities.ExhibitionCollection;
@Repository
public interface ExhibitionCollectionRepository extends JpaRepository<ExhibitionCollection,Integer> {

	@Query(value = "select new com.ecommerce.core.dto.TreeNodeDTO(c.id, c.name) from  ExhibitionCollection c WHERE c.programId = ?1 ORDER BY c.name ")
	List<TreeNodeDTO> getListTreeNode(Integer programId);
	
	@Query(value = "select c from  ExhibitionCollection c WHERE c.programId = ?1 AND c.name = ?2")
	List<ExhibitionCollection> getFileByName(Integer programId, String name);
	
	@Query(value = "select c from  ExhibitionCollection c WHERE c.programId = ?1")
	List<ExhibitionCollection> getListCollectionByPrograms(Integer programId);

	List<ExhibitionCollection> findByIsDirectoryAndProgramIdAndPath(Integer isDirectory, Integer programId, String path);

	Boolean existsByIsDirectoryAndProgramIdAndPath(Integer isDirectory, Integer programId, String path);
}
