package com.ecommerce.core.service;

import com.ecommerce.core.entities.Roles;
import com.ecommerce.core.entities.UndoLog;
import com.google.gson.Gson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


public interface RolesService extends IRootService<Roles> {
    Page<Roles> doSearch(String keyword, Pageable paging);

    Roles findByRoleCode(String roleCode);

    List<Roles> getListRoles();


    Roles deleteRole(Integer id) throws Exception;

    void undo(UndoLog undoLog) throws Exception;

    boolean deleteRoles(Integer[] ids, Gson g, String createdBy, HttpServletRequest request) throws Exception;

    List<String> getListRolesCodeByUsername(String username);
}
