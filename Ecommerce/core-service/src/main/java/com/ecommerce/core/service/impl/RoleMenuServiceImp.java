package com.ecommerce.core.service.impl;

import com.ecommerce.core.entities.RoleMenu;
import com.ecommerce.core.repositories.RoleMenuRepository;
import com.ecommerce.core.service.RoleMenuSerrvice;
import com.ecommerce.core.dto.RoleMenuDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class RoleMenuServiceImp implements RoleMenuSerrvice {
    @Autowired
    RoleMenuRepository repo;

    @Override
    public void updateRoleMenu(RoleMenuDTO dto) {
        Integer roleId= dto.getRoleId();
        List<RoleMenu> roleMenusList=repo.findAll();
        for (int i = 0; i < roleMenusList.size(); i++) {
            if(roleMenusList.get(i).getRoleId()== roleId){
                repo.deleteById(roleMenusList.get(i).getId());
            }
        }
        for (int i = 0; i <dto.getMenuId().size(); i++) {
            RoleMenu roleMenu=new RoleMenu(dto.getMenuId().get(i),roleId);
            repo.save(roleMenu);
        }
    }

    @Override
    public List<Integer> getMenuByRoleId(Integer roleId) {
        List<RoleMenu> roleMenusList=repo.findAll();
        List<Integer> listMenu = new ArrayList<Integer>();
        for (int i = 0; i < roleMenusList.size(); i++) {
            if(roleMenusList.get(i).getRoleId()== roleId){
                listMenu.add(roleMenusList.get(i).getMenuId());
            }
        }
        return listMenu;

    }
}
