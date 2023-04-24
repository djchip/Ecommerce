package com.ecommerce.core.service.impl;

import com.ecommerce.core.service.CriteriaProofSerice;
import com.ecommerce.core.dto.AfterTreeNodeByIdDTO;
import com.ecommerce.core.dto.FileProofDTO;
import com.ecommerce.core.dto.PreTreeNodeByIdDTO;
import com.ecommerce.core.dto.TreeNodeDTOProof;
import com.ecommerce.core.repositories.CriteriaRepository;
import com.ecommerce.core.repositories.DirectoryRepository;
import com.ecommerce.core.repositories.ProofRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CriteriaProofSeviceImpl implements CriteriaProofSerice {

    @Autowired
    CriteriaRepository criteriaRepository;

    @Autowired
    ProofRepository proofRepository;

    @Autowired
    DirectoryRepository standardRepository;

    @Override
    public List<TreeNodeDTOProof> setupStandTreeCriterProof(Integer programId, String username) {
        List<TreeNodeDTOProof> listStandardTree = standardRepository.getListStandardTreeNodeDTOByProgramId(programId, username);
        List<PreTreeNodeByIdDTO> listCriteriaTree = criteriaRepository.getListCriteriaTreeNode(programId, username);
        List<AfterTreeNodeByIdDTO> listProofTree = proofRepository.getListProofTreeNode(programId);
        List<FileProofDTO> listEXH = proofRepository.getALLProff(programId);

        //        check Minh chứng-file
        for (int k = 0; k < listProofTree.size(); k++) {
            for (int e = 0; e < listEXH.size(); e++) {
                if (listProofTree.get(k).getId().equals(listEXH.get(e).getParentId())) {
                    if (listProofTree.get(k).getChildren() == null) {
                        listProofTree.get(k).setChildren(new ArrayList<>());
                    }
                    listProofTree.get(k).getChildren().add(listEXH.get(e));

                }
            }
        }

        //check Tiêu chuẩn-tiêu chí
        for (int i = 0; i < listStandardTree.size(); i++) {
            for (int j = 0; j < listCriteriaTree.size(); j++) {
                if (listCriteriaTree.get(j).getParentId().equals(listStandardTree.get(i).getId())) {
                    if (listStandardTree.get(i).getChildren() == null) {
                        List<PreTreeNodeByIdDTO> listDTO = new ArrayList<>();
                        listStandardTree.get(i).setChildren(listDTO);
                    }
                    listStandardTree.get(i).getChildren().add(listCriteriaTree.get(j));
                }
            }
        }
        //check Tiêu chí- minh chứng
        for (int i = 0; i < listCriteriaTree.size(); i++) {
            for (int j = 0; j < listProofTree.size(); j++) {
                if ( (listProofTree.get(j).getCriteriaId() != null && listProofTree.get(j).getCriteriaId().equals(listCriteriaTree.get(i).getId()))) {
                    if (listCriteriaTree.get(i).getChildren() == null) {
                        listCriteriaTree.get(i).setChildren(new ArrayList<>());
                    }
                    listCriteriaTree.get(i).getChildren().add(listProofTree.get(j));
                    listProofTree.get(j).setCheck(true);
                }
            }
        }


        //     check Tiêu chuẩn-minh chứng
        List<AfterTreeNodeByIdDTO> subList = listProofTree.stream().map(x -> getListChillNotInParent(x, listCriteriaTree)).collect(Collectors.toList())
                .stream().filter(y -> y != null).collect(Collectors.toList());

        for (int i = 0; i < listStandardTree.size(); i++) {
            for (int j = 0; j < subList.size(); j++) {

                if (listStandardTree.get(i).getChildren() == null) {
                    listStandardTree.get(i).setChildren(new ArrayList<>());
                }

                List<AfterTreeNodeByIdDTO> listChildConvert = new ArrayList<>();
                if (subList.get(j).getChildren() != null) {
                    subList.get(j).getChildren().forEach((data) -> {
                        AfterTreeNodeByIdDTO dto = new AfterTreeNodeByIdDTO();
                        dto.setId(data.getId());
                        dto.setName(data.getName());
                        dto.setIsFile(data.getIsFile());
                        listChildConvert.add(dto);
                    });
                }
                if (listStandardTree.get(i).getId().equals(subList.get(j).getStaId()) && !listProofTree.get(j).isCheck()) {
                    PreTreeNodeByIdDTO preTreeNode = new PreTreeNodeByIdDTO(subList.get(j).getId(), subList.get(j).getCriteriaId(), listChildConvert, subList.get(j).getName());
                    listStandardTree.get(i).getChildren().add(preTreeNode);
                }
            }
        }
        return listStandardTree;
    }

    public AfterTreeNodeByIdDTO getListChillNotInParent(AfterTreeNodeByIdDTO after, List<PreTreeNodeByIdDTO> pre) {
        if (!pre.stream().anyMatch(e -> e.getId() == after.getCriteriaId()))
            return after;
        return null;
    }
}
