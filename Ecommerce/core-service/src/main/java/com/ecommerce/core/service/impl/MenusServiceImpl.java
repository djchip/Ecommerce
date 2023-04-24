package com.ecommerce.core.service.impl;


import com.ecommerce.core.entities.Menus;
import com.ecommerce.core.repositories.MenusRepository;
import com.ecommerce.core.service.MenusService;
import com.ecommerce.core.dto.MenuObjDTO;
import com.ecommerce.core.dto.MenusDTO;
import com.ecommerce.core.dto.TreeNodeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MenusServiceImpl implements MenusService {
	
	@Autowired
    MenusRepository repo;

	@Override
	public Menus create(Menus entity) {
		// TODO Auto-generated method stub
		return repo.save(entity);
	}

	@Override
	public Menus retrieve(Integer id) {
		// TODO Auto-generated method stub
		Optional<Menus> entity = repo.findById(id);
		if (!entity.isPresent()) {
			return null;
		}
		return entity.get();
	}

	@Override
	public void update(Menus entity, Integer id) {
		// TODO Auto-generated method stub
		repo.save(entity);
	}

	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub
		repo.deleteById(id);
	}

	@Override
	public List<Menus> findAll() {
		// TODO Auto-generated method stub
		return repo.findAll();
	}

	@Override
	public List<MenusDTO> getMenus(List<Integer> roleIds) {
		// TODO Auto-generated method stub
		List<Menus> listMenu = new ArrayList<Menus>();
		if(roleIds != null) {
			listMenu = repo.findWithOrderAndRoleIds(roleIds);
			List<Menus> listParent = repo.getMenuParrent();
			listMenu.addAll(listParent);

			List<Menus> notDuplicated = new ArrayList<>();
			for(Menus m: listMenu){
				if(!notDuplicated.contains(m)){
					notDuplicated.add(m);
				}
			}

			listMenu.removeAll(listMenu);
			listMenu.addAll(notDuplicated);
		}else{
			listMenu = repo.findWithOrder();
		}
		Integer currParent = 0;
		Map<Integer, List<MenusDTO>> mapChildren = new HashMap();
		List<MenusDTO> result = new ArrayList<MenusDTO>();
		Integer currentMenuId = 0;
		for(int i = 0; i < listMenu.size(); i++) {
			Menus menu = listMenu.get(i);
			if(!currentMenuId.equals(menu.getId())){
				MenusDTO dto = new MenusDTO();

				dto.setId(menu.getId());
				dto.setCode(menu.getMenuCode());
				dto.setTitle(menu.getMenuName());
				dto.setUrl(menu.getUrl());
				dto.setType("item");
				dto.setIcon(menu.getIcon());
				dto.setTranslate(menu.getTranslate());
				dto.setChildren(mapChildren.get(menu.getId()));
				dto.setSortBy(menu.getSortBy());

				currParent = menu.getMenuParentId();

				if(mapChildren.get(currParent) != null ) {
					mapChildren.get(currParent).add(dto);
				}else {
					List<MenusDTO> listChildren = new ArrayList<MenusDTO>();
					listChildren.add(dto);
					mapChildren.put(currParent, listChildren);
				}

				if(mapChildren.get(menu.getId()) != null) {
					dto.setChildren(mapChildren.get(menu.getId()));
					dto.setType("collapsible");
				}

				if(menu.getMenuLevel() == 1) {
					result.add(dto);
				}


			}
			currentMenuId = menu.getId();

		}
//		result.forEach(item->{
//			Collections.sort(item.getChildren(), new Comparator<MenusDTO>() {
//				@Override
//				public int compare(MenusDTO o1, MenusDTO o2) {
//					return o1.getSortBy() - o2.getSortBy();
//				}
//			});
//		});
		return result;
	}

	@Override
	public Page<MenuObjDTO> doSearch(String keyword, Pageable paging) {
		// TODO Auto-generated method stub
		return repo.doSearch(keyword, paging);
	}

	@Override
	public void deleteMenu(Integer id) {
		// TODO Auto-generated method stub
		String pathToRoot = "-" + id + "-";
		repo.deleteMenu(id, pathToRoot);
	}

	@Override
	public List<Menus> getMenuParrent() {
		return repo.getMenuParrent();
	}

	@Override
	public List<Menus> getMenuParrentUrl() {
		return repo.getMenuParrentUrl();
	}

	@Override
	public Menus findByMenuCode(String menuCode) {
		Optional<Menus> entity = repo.findByMenuCode(menuCode);
		if (!entity.isPresent()) {
			return null;
		}
		return entity.get();
	}

	@Override
	public Menus findByMenuName(String menuName) {
		Optional<Menus> entity = repo.findByMenuName(menuName);
		if (!entity.isPresent()) {
			return null;
		}
		return entity.get();
	}

	@Override
	public List<TreeNodeDTO> setupTreeMenu() {
		List<Menus> menusList = repo.findAll();
//		List<TreeNodeDTO> listTreeNode = repo.getListTreeNode();
		List<TreeNodeDTO> listTreeNode = repo.getListTreeNode();
		String currentMenu = "";
		for (int i = 0; i < listTreeNode.size(); i++) {
			List<TreeNodeDTO> listChildren = new ArrayList<>();
			for (int j = 0; j < menusList.size(); j++) {
				if (menusList.get(j).getMenuParentId() != null && listTreeNode.get(i).getId() == menusList.get(j).getMenuParentId()) {
					listChildren.add(new TreeNodeDTO(menusList.get(j).getId(), menusList.get(j).getMenuName()));
				}
			}
			listTreeNode.get(i).setChildren(listChildren);
		}

		return listTreeNode;
	}

	@Override
	public List<Menus> findAllChilds() {
		// TODO Auto-generated method stub
		return repo.findAllChilds();
	}


}
