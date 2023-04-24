package com.ecommerce.core.service.impl;


import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.entities.Roles;
import com.ecommerce.core.entities.UndoLog;
import com.ecommerce.core.entities.UserInfo;
import com.ecommerce.core.exceptions.ExistsCriteria;
import com.ecommerce.core.repositories.RolesRepository;
import com.ecommerce.core.repositories.UserInfoRepository;
import com.ecommerce.core.service.UndoLogService;
import com.google.gson.Gson;
import com.ecommerce.core.service.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RolesServiceImpl implements RolesService{
	private static final Integer DELETED =1 ;
	private static final Integer UNDO_DELETE = 0;
	private static final String TABLE_NAME = "roles";
	private static final int FIRST_INDEX = 0;
	private static final Integer UN_ACTIVE_UNDO_LOG = 1;
	@Autowired
    RolesRepository repo;

	@Autowired
    UndoLogService undoLogService;

	@Autowired
    UserInfoRepository userInfoRepository;

	@Override
	public Roles create(Roles entity) {
		// TODO Auto-generated method stub
		return repo.save(entity);
	}

	@Override
	public Roles retrieve(Integer id) {
		// TODO Auto-generated method stub
		Optional<Roles> entity = repo.findById(id);
		if (!entity.isPresent()) {
			return null;
		}
		return entity.get();
	}

	@Override
	public void update(Roles entity, Integer id) {
		// TODO Auto-generated method stub
		repo.save(entity);
	}

	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub
		repo.deleteById(id);
	}

	@Override
	public Page<Roles> doSearch(String keyword, Pageable paging) {
		// TODO Auto-generated method stub
		return repo.doSearch(keyword, paging);
	}

	@Override
	public Roles findByRoleCode(String roleCode) {
		// TODO Auto-generated method stub
		Optional<Roles> entity = repo.findByRoleCode(roleCode);
		if (!entity.isPresent()) {
			return null;
		}
		return entity.get();
	}

	@Override
	public List<Roles> getListRoles() {
		// TODO Auto-generated method stub
		return repo.getListRoles();
	}

	@Override
	public Roles deleteRole(Integer id) throws Exception {
		Optional<Roles> optional = repo.findById(id);
		if (optional.isPresent()) {
			List<UserInfo> userInfos= userInfoRepository.findByRoleIdAAndAndDeletedNot(id);
			if(userInfos != null && userInfos.size()>0){
				return null;
			}
			Roles roles = optional.get();
			roles.setDelete(DELETED);
			roles.setUndoStatus(ConstantDefine.STATUS.CAN_BE_UNDO);
			return repo.save(roles);
		} else {
			throw new Exception();
		}
	}
	private Roles undoPut(UndoLog undoLog)  {
		Gson g = new Gson();
		return  g.fromJson(undoLog.getRevertObject(), Roles.class);

	}

	@Override
	public void undo(UndoLog undoLog) throws Exception {
		List<UndoLog> undoLogs = undoLogService.findByTableNameAndIdRecordAndStatusNotOrderByCreatedDateDesc(TABLE_NAME, undoLog.getIdRecord(), 1);
		for (UndoLog log: undoLogs) {
			if (log.getId().equals(undoLog.getId())){
				Optional<Roles> optional = repo.findById(log.getIdRecord());
				Roles roles;
				if (optional.isPresent()) {
					roles = optional.get();
				} else {
					throw new Exception();
				}
				switch (log.getAction()) {
					case "POST":
						repo.deleteById(roles.getId());
						log.setStatus(UN_ACTIVE_UNDO_LOG);
						undoLogService.update(log, log.getId());
						break;
					case "DELETE":
						if(!repo.findByRoleCodeAndDelete(roles.getRoleCode(),0).isEmpty()){
							throw new ExistsCriteria("Mã nhóm quyền đã tồn tại với mã: "
									+ roles.getRoleName());

						}else {
						roles.setDelete(UNDO_DELETE);
						log.setStatus(UN_ACTIVE_UNDO_LOG);
						undoLogService.update(log, log.getId());
						break;}
					case "PUT":
						roles = undoPut(log);
						log.setStatus(UN_ACTIVE_UNDO_LOG);
						undoLogService.update(log, log.getId());
						break;
				}
				if(!undoLogService.existsByTableNameAndIdRecord(TABLE_NAME, roles.getId()) && !log.getAction().equals("POST")){
					roles.setUndoStatus(ConstantDefine.STATUS.CAN_NOT_UNDO);
				}
				if(!log.getAction().equals("POST")){
					repo.save(roles);
				}
				break;
			}else {
				log.setStatus(UN_ACTIVE_UNDO_LOG);
				undoLogService.update(log, log.getId());
			}
		}
	}

	@Override
	public boolean deleteRoles(Integer[] ids, Gson g, String createdBy, HttpServletRequest request) throws Exception {
		for (Integer id : ids) {
			Roles entity = deleteRole(id);
			if (entity == null) {
				return false;
			}
			UndoLog undoLog = UndoLog.undoLogBuilder()
					.action(request.getMethod())
					.requestObject(g.toJson(entity, Roles.class))
					.status(ConstantDefine.STATUS.UNDO_NEW)
					.url(request.getRequestURL().toString())
					.description("Xóa vai trò "+entity.getRoleName()+" bởi " + createdBy)
					.createdBy(createdBy)
					.createdDate(LocalDateTime.now())
					.tableName(TABLE_NAME)
					.idRecord(entity.getId())
					.build();
			undoLogService.create(undoLog);
		}
		return true;
	}

	@Override
	public List<String> getListRolesCodeByUsername(String username) {
		return repo.getListRolesCodeByUsername(username);
	}


}
