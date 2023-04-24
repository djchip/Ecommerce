package com.ecommerce.core.service;

import com.ecommerce.core.dto.PrivilegesDTO;
import com.ecommerce.core.entities.Privileges;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PrivilegesSevice extends IRootService<Privileges>{

    Page<Privileges> doSearch(String keyword, Pageable paging);

    Privileges findByCode(String roleCode);

    Privileges findByName(String roleName);

    Privileges findByNameAndId(Integer id, String roleName);

    List<PrivilegesDTO> getListPrivilegesAction(List<Integer> roleId);
}
