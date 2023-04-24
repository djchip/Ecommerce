package com.ecommerce.core.service;

import java.util.List;

import com.ecommerce.core.dto.TreeNodeProgramDTO;

import com.ecommerce.core.dto.ExhCodeAndIdDTO;
import com.ecommerce.core.dto.ExhCodeAndStandIdDTO;
import com.ecommerce.core.dto.TreeNodeDTO;

public interface StandardCriteriaService{

    List<TreeNodeDTO> setupTreeStandardCriteria(Integer programId, String username);

    List<TreeNodeDTO> setupTreeStandardCriteriaEn(Integer programId, String username);

    List<TreeNodeDTO> setupTreeSta(Integer programId, String username);

    List<TreeNodeDTO> setupTreeStaEn(Integer programId, String username);

    List<TreeNodeProgramDTO> setupTreeProgramStandardCriteria(Integer orgId, String username);

    List<TreeNodeProgramDTO> setupTreeProgramStandardCriteriaEn(Integer orgId, String username);
    
    List<TreeNodeDTO> setupTreeStandard(Integer programId, String username);
    
    String generateExhibitionCodeForStandardId(Integer standardId);
    
    ExhCodeAndStandIdDTO generateExhibitionCodeForCriteriaId(Integer criteriaId);
    
    ExhCodeAndIdDTO generateExhibitionCodeForCriteriaIdWithStandardId(Integer criteriaId);
    ExhCodeAndIdDTO generateExhibitionCodeForStandardIdV2(Integer criteriaId);

}
