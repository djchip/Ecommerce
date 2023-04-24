package com.ecommerce.core.controllers;

import com.ecommerce.core.config.JwtConfig;
import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.MenuObjDTO;
import com.ecommerce.core.dto.PagingResponse;
import com.ecommerce.core.dto.ResponseModel;
import com.ecommerce.core.dto.TreeNodeDTO;
import com.ecommerce.core.entities.Menus;
import com.ecommerce.core.entities.Roles;
import com.ecommerce.core.entities.UserInfo;
import com.ecommerce.core.service.MenusService;
import com.ecommerce.core.service.RoleMenuSerrvice;
import com.ecommerce.core.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("menu")
@Slf4j
public class MenusController extends BaseController {
	private final String START_LOG = "Menus {}";
	private final String END_LOG = "Menus {} finished in: {}";
	@Autowired
	private JwtConfig jwtConfig;
	@Autowired
	RoleMenuSerrvice roleMenuSerrvice;

	@Autowired
	MenusService service;
	@Autowired
	UserInfoService userInfoService;
	@GetMapping("/tree-menu")
	public ResponseModel initTree(HttpServletRequest request) {
		final String action = "initTree";
		StopWatch sw = new StopWatch();
		sw.start();
		log.info(START_LOG, action);
		try {

			List<TreeNodeDTO> listResult = service.setupTreeMenu();

			ResponseModel responseModel = new ResponseModel();
			responseModel.setContent(listResult);
			responseModel.setStatusCode(HttpStatus.SC_OK);
			responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
			return responseModel;
		} catch (Exception e) {
			throw handleException(e);
		} finally {
			sw.stop();
			log.info(END_LOG, action, sw.getTotalTimeSeconds());
		}
	}

	@GetMapping()
	public ResponseModel doSearch(@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "sort", required = false) boolean sortDesc,
			@RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int perPage,
			HttpServletRequest request) {
		final String action = "doSearch";
		StopWatch sw = new StopWatch();
		sw.start();
        log.info(START_LOG, action);
		try {
			Pageable paging = PageRequest.of(currentPage, perPage);
			Page<MenuObjDTO> pageResult = null;
			if(keyword.equals("")) {
				keyword = null;
			} else {
				keyword = keyword.trim();
			}
			pageResult = service.doSearch(keyword, paging);

			if ((pageResult == null || pageResult.isEmpty())) {
				ResponseModel responseModel = new ResponseModel();
				responseModel.setErrorMessages("Không tìm thấy Menu.");
				responseModel.setStatusCode(HttpStatus.SC_OK);
				responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
				return responseModel;
			}

			PagingResponse<MenuObjDTO> result = new PagingResponse<>();
			result.setTotal(pageResult.getTotalElements());
			result.setItems(pageResult.getContent());

			ResponseModel responseModel = new ResponseModel();
			responseModel.setContent(result);
			responseModel.setStatusCode(HttpStatus.SC_OK);
			responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
			return responseModel;
		} catch (Exception e) {
			throw handleException(e);
		} finally {
			sw.stop();
	        log.info(END_LOG, action, sw.getTotalTimeSeconds());
		}
	}

	@GetMapping("list-menu")
	public ResponseModel getSelectBox() {
		final String action = "doRetrieve";
		StopWatch sw = new StopWatch();
		log.info(START_LOG, action);
		try {
			ResponseModel responseModel = new ResponseModel();
			responseModel.setContent(service.findAll());
			responseModel.setStatusCode(HttpStatus.SC_OK);
			responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
			return responseModel;
		} catch (Exception e) {
			throw handleException(e);
		} finally {
			log.info(END_LOG, action, sw.getTotalTimeSeconds());
		}
	}
	
	@GetMapping("get-menu")
	public ResponseModel getMenus(HttpServletRequest request) {
		final String action = "doRetrieve";
		StopWatch sw = new StopWatch();
		log.info(START_LOG, action);
		try {
//			String bearerToken = request.getHeader("Authorization").substring(7);
//			Claims claims = Jwts.parser().setSigningKey(jwtConfig.getSecret().getBytes()).parseClaimsJws(bearerToken)
//					.getBody();
//			String name = claims.getSubject();
//			UserInfo userInfo=userInfoService.findByUsername(name);
//			List<Roles> rolesList=userInfo.getRole();
//			List<Integer>menuIdList= new ArrayList<Integer>();
//			for (int i = 0; i <rolesList.size() ; i++) {
//				List<Integer>Menulist=roleMenuSerrvice.getMenuByRoleId(rolesList.get(i).getId());
//				menuIdList.addAll(Menulist);
//			}
//			List<MenusDTO>listMenuDTO=service.getMenus();
////			List<MenusDTO>listMenuDTOValue=service.getMenus();
//			for (int i = 0; i < listMenuDTO.size(); i++) {
//				List<MenusDTO> listMenuChildren=listMenuDTO.get(i).getChildren();
//				for (int j = 0; j <listMenuChildren.size() ; j++) {
//					int check=0;
//					for (int k = 0; k < menuIdList.size(); k++) {
//						if (listMenuChildren.get(j).getId() == menuIdList.get(k)) {
//							check = 1;
//						}
//					}
//					if (check == 0) {
//						listMenuChildren.remove(j);
//						j--;
//
//					}
//				}
//				listMenuDTO.get(i).setChildren(listMenuChildren);
//				if(listMenuDTO.get(i).getChildren().size()==0){
//					listMenuDTO.remove(i);
//					i--;
//				}
//			}
			UserInfo user = userInfoService.findByUsername(getUserFromToken(request));
			List<Roles> roles = user.getRole();
			List<Integer> roleIds = new ArrayList<Integer>();
			for(int i = 0; i < roles.size(); i++){
				roleIds.add(roles.get(i).getId());
			}
			ResponseModel responseModel = new ResponseModel();
			responseModel.setContent(service.getMenus(roleIds));
			responseModel.setStatusCode(HttpStatus.SC_OK);
			responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
			return responseModel;
		} catch (Exception e) {
			throw handleException(e);
		} finally {
			log.info(END_LOG, action, sw.getTotalTimeSeconds());
		}
	}

	@PostMapping()
	public ResponseModel doCreate(@RequestBody Menus entity) {
		final String action = "doCreate";
		StopWatch sw = new StopWatch();
		log.info(START_LOG, action);

		try {

			String menuCode  = entity.getMenuCode().trim();
			String menuName = entity.getMenuName().trim();
			Menus checkExisted = service.findByMenuCode(menuCode);
			Menus checkExistName = service.findByMenuName(menuName);

			if (checkExisted != null) {
				if (checkExisted.getMenuCode() != null) {
					ResponseModel responseModel = new ResponseModel();
					responseModel.setStatusCode(HttpStatus.SC_OK);
					responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
					responseModel.setErrorMessages("Mã menu đã tồn tại.");
					return responseModel;
				}
			}else if (checkExistName != null){
				if (checkExistName.getMenuName() != null) {
					ResponseModel responseModel = new ResponseModel();
					responseModel.setStatusCode(HttpStatus.SC_OK);
					responseModel.setCode(ResponseFontendDefine.NAME_ALREADY_EXIST);
					responseModel.setErrorMessages("Tên menu đã tồn tại.");
					return responseModel;
				}
			}else {
				String pathToRoot = "-";
				Integer level = 1;
				if(entity.getMenuParentId() != null) {
					boolean isRoot = false;
					boolean isParent = true;
					Integer menuId = entity.getMenuParentId();
					while(!isRoot) {
						Menus parent = service.retrieve(menuId);
						menuId = parent.getMenuParentId();
						if(isParent) {
							level = parent.getMenuLevel() + 1;
							isParent = false;
						}
						pathToRoot += parent.getId() + "-";
						if(parent.getMenuLevel() == 1) {
							isRoot = true;
						}
					}
				}

				entity.setMenuLevel(level);
				entity.setPathToRoot(pathToRoot);
				entity.setCreatedBy("admin");
				entity.setStatus(ConstantDefine.STATUS.ACTIVE);
				entity.setUpdatedDate(LocalDateTime.now());
				service.create(entity);
			}



			ResponseModel responseModel = new ResponseModel();
			responseModel.setStatusCode(HttpStatus.SC_OK);
			responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
			return responseModel;
		} catch (Exception e) {
			throw handleException(e);
		} finally {
			log.info(END_LOG, action, sw.getTotalTimeSeconds());
		}
	}
	
	@DeleteMapping("{id}")
	public ResponseModel doDelete(@PathVariable Integer id, HttpServletRequest request) {
		final String action = "doDelete";
		StopWatch sw = new StopWatch();
		log.info(START_LOG, action);
		try {
			service.deleteMenu(id);
			ResponseModel responseModel = new ResponseModel();
			responseModel.setStatusCode(HttpStatus.SC_OK);
			responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
			return responseModel;
		} catch (Exception e) {
			throw handleException(e);
		} finally {
			log.info(END_LOG, action, sw.getTotalTimeSeconds());
		}
	}


	//datbd
	@GetMapping("{id}")
	public ResponseModel doRetrieve(@PathVariable Integer id) {
		final  String action = "doRetrieve";
		StopWatch sw = new StopWatch();
		log.info(START_LOG,action);
		try {
			Menus entity = service.retrieve(id);
			if (entity == null){
				ResponseModel responseModel = new ResponseModel();
				responseModel.setErrorMessages("Không tìm thấy người dùng.");
				responseModel.setStatusCode(HttpStatus.SC_OK);
				responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
				return  responseModel;
			}

			ResponseModel responseModel = new ResponseModel();
			responseModel.setContent(entity);
			responseModel.setStatusCode(HttpStatus.SC_OK);
			responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
			return responseModel;
		}catch (Exception e){
			throw handleException(e);
		} finally {
			log.info(END_LOG, action, sw.getTotalTimeSeconds());
		}
	}

	@PutMapping()
	public ResponseModel doUpdate(@RequestBody Menus dto) {
		final String action = "Update";
		StopWatch sw = new StopWatch();
		log.info(START_LOG, action);
		try {
			Menus entity = service.retrieve(dto.getId());
			entity.setMenuCode(dto.getMenuCode());
			entity.setMenuName(dto.getMenuName());
			entity.setMenuParentId(dto.getMenuParentId());
			entity.setIcon(dto.getIcon());
			entity.setUrl(dto.getUrl());
			entity.setUpdatedDate(LocalDateTime.now());
			service.update(entity,entity.getId());
			ResponseModel responseModel= new ResponseModel();
			responseModel.setStatusCode(HttpStatus.SC_OK);
			responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
			return responseModel;
		} catch (
				Exception e){
			throw handleException(e);
		}finally {
			log.info(END_LOG,action, sw.getTotalTimeSeconds());
		}

	}

	@GetMapping("list-menu-parrent-url")
	public ResponseModel getMenuParrentUrl(){
		final String action = "doRetrieve Menu Parrenturl";
		StopWatch sw = new StopWatch();
		log.info(START_LOG, action);

		try {
			ResponseModel responseModel = new ResponseModel();
			responseModel.setContent(service.getMenuParrentUrl());
			responseModel.setStatusCode(HttpStatus.SC_OK);
			responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
			return responseModel;
		}catch (Exception e){
			throw handleException(e);
		}finally {
			log.info(END_LOG, action, sw.getTotalTimeSeconds());
		}
	}


	@GetMapping("list-menu-parrent")
	public ResponseModel getMenuParrent() {
		final String action = "doRetrieve Menu Parrent";
		StopWatch sw = new StopWatch();
		log.info(START_LOG, action);
		try {

			ResponseModel responseModel = new ResponseModel();
			responseModel.setContent(service.getMenuParrent());
			responseModel.setStatusCode(HttpStatus.SC_OK);
			responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
			return responseModel;
		} catch (Exception e) {
			throw handleException(e);
		} finally {
			log.info(END_LOG, action, sw.getTotalTimeSeconds());
		}
	}


}
