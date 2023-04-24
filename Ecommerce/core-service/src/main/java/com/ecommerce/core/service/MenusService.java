package com.ecommerce.core.service;

import com.ecommerce.core.dto.MenuObjDTO;
import com.ecommerce.core.dto.MenusDTO;
import com.ecommerce.core.dto.TreeNodeDTO;
import com.ecommerce.core.entities.Menus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MenusService extends IRootService<Menus>{
	
	List<Menus> findAll();
	
	List<Menus> findAllChilds();
	
	List<MenusDTO> getMenus(List<Integer> roleIds);
	
	Page<MenuObjDTO> doSearch(String keyword, Pageable paging);
	
	void deleteMenu(Integer id);

	List<Menus> getMenuParrent();


	List<Menus> getMenuParrentUrl();

	 Menus findByMenuCode(String menuCode);

	Menus findByMenuName(String menuName);
	List<TreeNodeDTO> setupTreeMenu();
}
