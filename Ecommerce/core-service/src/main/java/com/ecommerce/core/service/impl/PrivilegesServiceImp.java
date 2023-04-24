package com.ecommerce.core.service.impl;

import com.ecommerce.core.entities.Privileges;
import com.ecommerce.core.repositories.PrivilegesRepository;
import com.ecommerce.core.service.PrivilegesSevice;
import com.ecommerce.core.dto.ActionDTO;
import com.ecommerce.core.dto.PrivilegesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PrivilegesServiceImp implements PrivilegesSevice {

    private static final Integer ADD = 2;
    private static final Integer UPDATE = 3;
    private static final Integer DELETE = 4;
    private static final Integer DETAIL = 1;
    private static final Integer SEARCH = 5;
    private static final Integer LOCK = 6;

    @Autowired
    PrivilegesRepository repo;

    @Override
    public Privileges create(Privileges entity) {
        return repo.save(entity);
    }

    @Override
    public Privileges retrieve(Integer id) {
        Optional<Privileges> entity = repo.findById(id);
        if (!entity.isPresent()) {
            return null;
        }
        return entity.get();
    }

    @Override
    public void update(Privileges entity, Integer id) {
        repo.save(entity);
    }

    @Override
    public void delete(Integer id) {

    }

    @Override
    public Page<Privileges> doSearch(String keyword, Pageable paging) {
        return repo.doSearch(keyword, paging);
    }

    @Override
    public Privileges findByCode(String roleCode) {
        // TODO Auto-generated method stub
        Optional<Privileges> entity = repo.findByCode(roleCode);
        if (!entity.isPresent()) {
            return null;
        }
        return entity.get();
    }

    @Override
    public Privileges findByName(String roleName) {
        Optional<Privileges> entity = repo.findByName(roleName);
        if (!entity.isPresent()) {
            return null;
        }
        return entity.get();
    }

    @Override
    public Privileges findByNameAndId(Integer id, String roleName) {
        Optional<Privileges> entity = repo.findByNameAndId(id, roleName);
        if (!entity.isPresent()) {
            return null;
        }
        return entity.get();
    }

    @Override
    public List<PrivilegesDTO> getListPrivilegesAction(List<Integer> roleId) {
        List<Object[]> privilegesAction = repo.getListPrivilegesAction(roleId);
        List<PrivilegesDTO> list = new ArrayList<>();
        for (Object[] item : privilegesAction){
            PrivilegesDTO dto = new PrivilegesDTO();
            dto.setUrl((String) item[0]);
            String action = (String) item[1];
            List<Integer> listAction = new ArrayList<>();
            for (String field : action.split(",")){
                listAction.add(Integer.parseInt(field));
            }
            ActionDTO actionDTO = new ActionDTO();
            if (listAction.contains(ADD)) actionDTO.setAdd(true);
            if (listAction.contains(UPDATE)) actionDTO.setUpdate(true);
            if (listAction.contains(DELETE)) actionDTO.setDelete(true);
//            if (listAction.contains(DETAIL)) actionDTO.setDetail(true);
//            if (listAction.contains(SEARCH)) actionDTO.setSearch(true);
            if (listAction.contains(LOCK)) actionDTO.setLock(true);
            dto.setAction(actionDTO);
            list.add(dto);
        }
        return list;
    }
}
