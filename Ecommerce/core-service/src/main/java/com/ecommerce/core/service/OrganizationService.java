package com.ecommerce.core.service;


import com.ecommerce.core.entities.Organization;
import com.ecommerce.core.entities.UndoLog;
import com.google.gson.Gson;
import com.ecommerce.core.dto.OrganizationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface OrganizationService extends IRootService<Organization>{

    Page<Organization> doSearch(String keyword, Pageable pageable);
//    Page<OrganizationDTO> doSearchEn(String keyword, Pageable pageable);
    List<Organization> getSelectbox();

    List<Organization> findOrgForCriteria();

    List<Organization> getListOrg();
    Optional<Organization> finbyID(Integer id);

    List<Organization> findByName(String name);

    Organization findOrgByName(String name);

    Organization deleteOr(Integer id) throws Exception;

    void undo(UndoLog undoLog) throws Exception;

    OrganizationDTO formatObj(Organization entity);

    Organization findByProgramId(Integer id);


    Organization findByIdAndDeletedNot(Integer id, Integer deleted );

    boolean deleteOr(Integer[] ids, Gson g, String deleteBy, HttpServletRequest request) throws Exception;

    boolean findOrgbyEncode();
}
