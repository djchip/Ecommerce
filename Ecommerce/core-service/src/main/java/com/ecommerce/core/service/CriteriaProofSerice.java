package com.ecommerce.core.service;

import com.ecommerce.core.dto.TreeNodeDTOProof;

import java.util.List;

public interface CriteriaProofSerice {
    List<TreeNodeDTOProof> setupStandTreeCriterProof(Integer id, String userFromToken);

//    List<TreeNodeDTO> setupTreeCriteria(Integer id);
}
