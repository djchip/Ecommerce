package com.ecommerce.core.service;

import com.ecommerce.core.entities.ExhibitionFile;

import java.util.List;
import java.util.Optional;

public interface ExhibitionFileService extends IRootService<ExhibitionFile> {
	Integer getLatestId();

	ExhibitionFile getExhFileByProofId(Integer id);

	List<ExhibitionFile> getListExhFileByProofId(Integer id);

	ExhibitionFile findById(Integer id);

	Optional<ExhibitionFile> finbyID(Integer id);

}
