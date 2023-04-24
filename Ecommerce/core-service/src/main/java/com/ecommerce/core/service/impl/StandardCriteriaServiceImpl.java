package com.ecommerce.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.ecommerce.core.dto.*;
import com.ecommerce.core.service.StandardCriteriaService;
import com.ecommerce.core.repositories.ProgramsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.core.repositories.CriteriaRepository;
import com.ecommerce.core.repositories.DirectoryRepository;

@Service
public class StandardCriteriaServiceImpl implements StandardCriteriaService {

    @Autowired
    CriteriaRepository criteriaRepository;

    @Autowired
    DirectoryRepository standardRepository;

    @Autowired
    ProgramsRepository programsRepository;

    @Override
    public List<TreeNodeDTO> setupTreeStandardCriteria(Integer programId, String username) {
        List<TreeNodeDTO> listStandardTree = standardRepository.getListStandardTreeNodeByProgramId(programId, username);
        List<PreTreeNodeByIdDTO> listCriteriaTree = criteriaRepository.getListCriteriaTreeNode(programId, username);
//        Integer currentCriteriaIndex = 0;

        for (int i = 0; i < listStandardTree.size(); i++) {
//			if(listStandardTree.get(i).getId() == listCriteriaTree.get(currentCriteriaIndex).getParentId()) {
            for (int j = 0; j < listCriteriaTree.size(); j++) {
                System.out.println(listStandardTree.get(i).getId());
                System.out.println(listCriteriaTree.get(j).getParentId());
                if (listStandardTree.get(i).getId().equals(listCriteriaTree.get(j).getParentId())) {
                    if (listStandardTree.get(i).getChildren() == null) {
                        listStandardTree.get(i).setChildren(new ArrayList<TreeNodeDTO>());
                    }
                    listStandardTree.get(i).getChildren().add(new TreeNodeDTO(listCriteriaTree.get(j).getId(), listCriteriaTree.get(j).getName()));
                }
            }
//			}
        }
        List<TreeNodeDTO> listResult = new ArrayList<TreeNodeDTO>();
        for (int i = 0; i < listStandardTree.size(); i++) {
            if (listStandardTree.get(i).getChildren() != null && !listStandardTree.get(i).getChildren().isEmpty()) {
                listResult.add(listStandardTree.get(i));
            }
        }
        return listResult;
    }

    @Override
    public List<TreeNodeDTO> setupTreeStandardCriteriaEn(Integer programId, String username) {
        List<TreeNodeDTO> listStandardTree = standardRepository.getListStandardTreeNodeByProgramIdEn(programId, username);
        List<PreTreeNodeByIdDTO> listCriteriaTree = criteriaRepository.getListCriteriaTreeNodeEn(programId, username);
//        Integer currentCriteriaIndex = 0;

        for (int i = 0; i < listStandardTree.size(); i++) {
//			if(listStandardTree.get(i).getId() == listCriteriaTree.get(currentCriteriaIndex).getParentId()) {
            for (int j = 0; j < listCriteriaTree.size(); j++) {
                System.out.println(listStandardTree.get(i).getId());
                System.out.println(listCriteriaTree.get(j).getParentId());
                if (listStandardTree.get(i).getId().equals(listCriteriaTree.get(j).getParentId())) {
                    if (listStandardTree.get(i).getChildren() == null) {
                        listStandardTree.get(i).setChildren(new ArrayList<TreeNodeDTO>());
                    }
                    listStandardTree.get(i).getChildren().add(new TreeNodeDTO(listCriteriaTree.get(j).getId(), listCriteriaTree.get(j).getName()));
                }
            }
//			}
        }
        List<TreeNodeDTO> listResult = new ArrayList<TreeNodeDTO>();
        for (int i = 0; i < listStandardTree.size(); i++) {
            if (listStandardTree.get(i).getChildren() != null && !listStandardTree.get(i).getChildren().isEmpty()) {
                listResult.add(listStandardTree.get(i));
            }
        }
        return listResult;
    }

    @Override
    public List<TreeNodeDTO> setupTreeSta(Integer programId, String username) {
        return standardRepository.getListStandardTreeNodeByProgramId(programId, username);
    }

    public List<TreeNodeDTO> setupTreeStaEn(Integer programId, String username) {
        return standardRepository.getListStandardTreeNodeByProgramIdEn(programId, username);
    }

    @Override
    public List<TreeNodeProgramDTO> setupTreeProgramStandardCriteria(Integer orgId, String username) {
        List<TreeNodeProgramDTO> listProgram = programsRepository.getListTreeNodeProgramDTOByOrgId(orgId, username);
        List<TreeNodeStandardDTO> listStandard = standardRepository.getListTreeNodeStandardDTOByOrgId(orgId, username);
        List<TreeNodeCriteriaDTO> listCriteria = criteriaRepository.getListTreeNodeCriteriaDTOByOrgId(orgId, username);
        System.out.println("PRO" + listProgram);
        System.out.println("STA" + listStandard);
        System.out.println("CRI" + listCriteria);

        for (int i = 0; i < listProgram.size(); i++) {
            for (int j = 0; j < listStandard.size(); j++) {
                if (Objects.equals(listStandard.get(j).getParentId(), listProgram.get(i).getId())) {
                    if (listProgram.get(i).getChildren() == null) {
                        listProgram.get(i).setChildren(new ArrayList<TreeNodeStandardDTO>());
                    }
                    listProgram.get(i).getChildren().add(listStandard.get(j));
//				listProgram.get(i).getListStandards().add(new TreeNodeStandardDTO(listStandard.get(j).getId(), listStandard.get(j).getName()));
                }
            }
        }
        for (int i = 0; i < listStandard.size(); i++) {
            String[] staId = listStandard.get(i).getId().split("\\.");
            for (int j = 0; j < listCriteria.size(); j++) {
                String[] criId = listCriteria.get(j).getId().split("\\.");

                if (criId[0].equals(staId[0]) && Integer.parseInt(staId[1]) == listCriteria.get(j).getParentId()) {
                    if (listStandard.get(i).getChildren() == null) {
                        listStandard.get(i).setChildren(new ArrayList<>());
                    }
                    listStandard.get(i).getChildren().add(listCriteria.get(j));
                }
            }
        }
        return listProgram;
    }

    @Override
    public List<TreeNodeProgramDTO> setupTreeProgramStandardCriteriaEn(Integer orgId, String username) {
        List<TreeNodeProgramDTO> listProgram = programsRepository.getListTreeNodeProgramDTOByOrgIdEn(orgId, username);
        List<TreeNodeStandardDTO> listStandard = standardRepository.getListTreeNodeStandardDTOByOrgIdEn(orgId, username);
        List<TreeNodeCriteriaDTO> listCriteria = criteriaRepository.getListTreeNodeCriteriaDTOByOrgIdEn(orgId, username);
        for (int i = 0; i < listProgram.size(); i++) {
            for (int j = 0; j < listStandard.size(); j++) {
                if (Objects.equals(listStandard.get(j).getParentId(), listProgram.get(i).getId())) {
                    if (listProgram.get(i).getChildren() == null) {
                        listProgram.get(i).setChildren(new ArrayList<TreeNodeStandardDTO>());
                    }
                    listProgram.get(i).getChildren().add(listStandard.get(j));
//				listProgram.get(i).getListStandards().add(new TreeNodeStandardDTO(listStandard.get(j).getId(), listStandard.get(j).getName()));
                }
            }
        }
        for (int i = 0; i < listStandard.size(); i++) {
            String[] staId = listStandard.get(i).getId().split("\\.");
            for (int j = 0; j < listCriteria.size(); j++) {
                String[] criId = listCriteria.get(j).getId().split("\\.");

                if (criId[0].equals(staId[0]) && Integer.parseInt(staId[1]) == listCriteria.get(j).getParentId()) {
                    if (listStandard.get(i).getChildren() == null) {
                        listStandard.get(i).setChildren(new ArrayList<>());
                    }
                    listStandard.get(i).getChildren().add(listCriteria.get(j));
                }
            }
        }
        return listProgram;
    }

    @Override
    public List<TreeNodeDTO> setupTreeStandard(Integer programId, String username) {
        // TODO Auto-generated method stub
        return standardRepository.getListStandardTreeNodeByProgramId(programId, username);
    }

    @Override
    public String generateExhibitionCodeForStandardId(Integer standardId) {
        // TODO Auto-generated method stub
        return standardRepository.getExhibitionCode(standardId);
    }

    @Override
    public ExhCodeAndStandIdDTO generateExhibitionCodeForCriteriaId(Integer criteriaId) {
        // TODO Auto-generated method stub
        List<ExhCodeAndStandIdDTO> lstResult = criteriaRepository.getExhibitionCode(criteriaId);
        if (lstResult != null && !lstResult.isEmpty()) {
            return lstResult.get(0);
        }
        return null;
    }

    @Override
    public ExhCodeAndIdDTO generateExhibitionCodeForCriteriaIdWithStandardId(Integer criteriaId) {
        // TODO Auto-generated method stub
        List<ExhCodeAndIdDTO> lstResult = criteriaRepository.getExhibitionCodeWithId(criteriaId);
        if (lstResult != null && !lstResult.isEmpty()) {
            return lstResult.get(0);
        }
        return null;
    }

    @Override
    public ExhCodeAndIdDTO generateExhibitionCodeForStandardIdV2(Integer standardId) {
        // TODO Auto-generated method stub
        List<ExhCodeAndIdDTO> lstResult = standardRepository.getExhibitionCodeWithId(standardId);
        if (lstResult != null && !lstResult.isEmpty()) {
            return lstResult.get(0);
        }
        return null;
    }
}
