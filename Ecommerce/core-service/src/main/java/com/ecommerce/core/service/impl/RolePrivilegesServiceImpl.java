package com.ecommerce.core.service.impl;

import com.ecommerce.core.repositories.PrivilegesRepository;
import com.ecommerce.core.repositories.RolePrivilegesRepository;
import com.ecommerce.core.dto.PreTreeNodeDTO;
import com.ecommerce.core.dto.RolePrivilegesDTO;
import com.ecommerce.core.dto.TreeNodeDTO;
import com.ecommerce.core.entities.RolePrivileges;
import com.ecommerce.core.entities.RolePrivilegesId;
import com.ecommerce.core.service.RolePrivilegesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RolePrivilegesServiceImpl implements RolePrivilegesService {
    @Autowired
    RolePrivilegesRepository repository;
    
    @Autowired
    PrivilegesRepository privilegesRepository;

	@Override
	public List<TreeNodeDTO> setupTreePrivileges() {
		// TODO Auto-generated method stub
		List<PreTreeNodeDTO> listPreNode = privilegesRepository.getListPrivilegesMenu();
		List<TreeNodeDTO> listTreeNode = new ArrayList<TreeNodeDTO>();
		String currentMenu = "";
		for(int i = 0; i < listPreNode.size(); i++) {
			int check=0;
			if(!currentMenu.equals(listPreNode.get(i).getMenuName())) {
				currentMenu = listPreNode.get(i).getMenuName();
				for (int j = 0; j < listTreeNode.size(); j++) {
					if(currentMenu.equals(listTreeNode.get(j).getName()))
					{	check=1;
						listTreeNode.get(j).getChildren().add(new TreeNodeDTO(listPreNode.get(i).getId(), listPreNode.get(i).getPrivilegesName(), null, true));
					}
				}
				if(check==0)listTreeNode.add(new TreeNodeDTO(null, currentMenu, new ArrayList<TreeNodeDTO>(), true));
			}
			if(check==0)listTreeNode.get(listTreeNode.size() - 1).getChildren().add(new TreeNodeDTO(listPreNode.get(i).getId(), listPreNode.get(i).getPrivilegesName(), null, true));
			
		}
		return listTreeNode;
	}

	@Override
	public void updatePrivileges(RolePrivilegesDTO dto) {
		// TODO Auto-generated method stub
		List<RolePrivileges> listOldPrivileges = searchPrivilegesByRoleId(dto.getRoleId());
		
		List<Integer> listNewPrivileges = dto.getPrivilegesId();
		
		//Add new privileges
		for(int i= 0; i < listNewPrivileges.size(); i++) {
			RolePrivileges entity = new RolePrivileges(new RolePrivilegesId( dto.getRoleId(), listNewPrivileges.get(i)));
			if(!listOldPrivileges.contains(entity)) {
				repository.save(entity);
			}
		}
		
		//Remove privileges
		for(int i= 0; i < listOldPrivileges.size(); i++) {
			Integer oldPrivilegesId = listOldPrivileges.get(i).getId().getPrivilegesId();
			if(!listNewPrivileges.contains(oldPrivilegesId)) {
				repository.deleteById(new RolePrivilegesId( dto.getRoleId(), oldPrivilegesId));
			}
		}
	}

	@Override
	public List<RolePrivileges> searchPrivilegesByRoleId(Integer roleId) {
		// TODO Auto-generated method stub
		return repository.searchPrivilegesByRoleId(roleId);
	}

	@Override
	public RolePrivileges create(RolePrivileges entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RolePrivileges retrieve(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(RolePrivileges entity, Integer id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Integer> getPrivilegesByRoleId(Integer roleId) {
		// TODO Auto-generated method stub
		List<RolePrivileges> listRolePrivileges = repository.searchPrivilegesByRoleId(roleId);
		List<Integer> listPrivileges = new ArrayList<Integer>();
		if(listRolePrivileges != null && !listRolePrivileges.isEmpty()) {
			for(int i = 0; i < listRolePrivileges.size(); i++) {
				listPrivileges.add(listRolePrivileges.get(i).getId().getPrivilegesId());
			}
		}
		return listPrivileges;
	}
}
