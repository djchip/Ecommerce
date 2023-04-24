package com.ecommerce.core.service.impl;


import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.entities.Roles;
import com.ecommerce.core.entities.UndoLog;
import com.ecommerce.core.entities.UserInfo;
import com.ecommerce.core.exceptions.ExistsCriteria;
import com.ecommerce.core.exceptions.ExitsCheckEmail;
import com.ecommerce.core.repositories.UserInfoRepository;
import com.ecommerce.core.service.UndoLogService;
import com.google.gson.Gson;
import com.ecommerce.core.dto.UserInfoDTO;
import com.ecommerce.core.service.RolesService;
import com.ecommerce.core.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    private static final Integer FIRST_INDEX = 0;
    private static final Integer DELETED = 1;
    private static final Integer UNDO_DELETE = 0;
    private static final Integer STATUS_DELETE = 3;
    private static final String TABLE_NAME = "UserInfo";
    private static final Integer UN_ACTIVE_UNDO_LOG = 1;

    @Autowired
    UserInfoRepository repo;

    @Autowired
    UndoLogService undoLogService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    RolesService rolesService;

    @Override
    public UserInfo create(UserInfo entity) {
        // TODO Auto-generated method stub
        return repo.save(entity);
    }

    @Override
    public UserInfo retrieve(Integer id) {
        // TODO Auto-generated method stub
        Optional<UserInfo> entity = repo.findById(id);
        return entity.orElse(null);
    }

    @Override
    public void update(UserInfo entity, Integer id) {
        repo.save(entity);
    }

    @Transactional
    @Override
    public void delete(Integer id) throws Exception {
        Optional<UserInfo> optional = repo.findById(id);
        if (optional.isPresent()) {
            UserInfo userInfo = optional.get();
            userInfo.setDeleted(DELETED);
            userInfo.setStatus(STATUS_DELETE);
            repo.save(userInfo);
        } else {
            throw new Exception();
        }
    }

    @Override
    public Optional<UserInfo> findById(Integer id) {
        return repo.findById(id);
    }

    @Override
    public Page<UserInfo> doSearch(String keyword, Integer roleId, Integer unitId, Pageable paging) {
        return repo.doSearch(keyword, roleId, unitId, paging);
    }

    @Override
    public UserInfo findByUsername(String username) {
        Optional<UserInfo> entity = repo.findByName(username);
        return entity.orElse(null);
    }

    @Override
    public List<UserInfoDTO> getListUserByRole(Integer roleId) {
        List<Object[]> listUser = repo.getListUserByRole(roleId);
        List<UserInfoDTO> dtoList = new ArrayList<>();
        for (Object[] item : listUser){
            UserInfoDTO dto = new UserInfoDTO();
            dto.setId((Integer) item[0]);
            dto.setUsername((String) item[1]);
            dto.setEmail((String) item[2]);
            dto.setFullname((String) item[3]);
            dto.setPhoneNumber((String) item[4]);
            dtoList.add(dto);
        }
        return dtoList;
    }



    @Override
    public UserInfo deleteUser(Integer id, String userName) throws Exception {
        Optional<UserInfo> optional = repo.findById(id);
        Optional<UserInfo> byUsername = repo.findByUsername(userName);
        UserInfo userInfo = optional.get();
        boolean isCheck = false;
        if (optional.get().getRole().size() > 0 && byUsername.get().getRole().size() > 0){

            List<Integer> listRoleIdOfByUsername = new ArrayList<>();
            for(Roles roles : byUsername.get().getRole()){
                listRoleIdOfByUsername.add(roles.getId());
            }

            List<Integer> listRoleId = new ArrayList<>();
            for(Roles roles : optional.get().getRole()){
                listRoleId.add(roles.getId());
            }
//            TH: người được xóa là admin cấp cao không được xóa cái được xóa cấp cao
            if(listRoleIdOfByUsername.contains(121) && !listRoleId.contains(121)){
                isCheck = true;
            }
//            TH: người được xóa là admin không được xóa cái được xóa có admin và cái có admin cấp cao
            if(listRoleIdOfByUsername.contains(1) && !listRoleId.contains(121) && !listRoleId.contains(1)){
                isCheck = true;
            }
//            TH: cả 2 không phải admin, admin cấp cao
            if(!listRoleIdOfByUsername.contains(1) && !listRoleIdOfByUsername.contains(121)&& !listRoleId.contains(121) && !listRoleId.contains(1) ){
                isCheck=true;
            }


//            if(listRoleIdOfByUsername.contains(1) && listRoleId.contains(121)){
//                isCheck = false;
//            }

//            for (Roles role : byUsername.get().getRole()){
//                for (Roles r : optional.get().getRole()){
////                    TH1: cả 2 đều là admin
////                    TH2: cả 2 đều là admin cấp cao
////                    TH3: người được xóa là admin thì ko được xóa cái bị xóa admin cấp cao
////                    TH4: Người được xóa là admin và admin cấp cao xóa được cái bị xóa quyền admin
//
//                if ( (role.getId() == 1 && r.getId() == 1)
//                        || (role.getId() == 121 && r.getId() == 121)
//                        ||(role.getId() == 1 && r.getId()==121 )
//                )
//                {
//                    if (role.getId() == 121 && r.getId() == 1){
//                        isCheck = true;
//                    } else {
//                        isCheck = false;
//                    }
//                } else
//                {
//                    isCheck = true;
//                }
//            }}
            if (isCheck == true){
                userInfo.setDeleted(DELETED);
                userInfo.setUndoStatus(ConstantDefine.STATUS.CAN_BE_UNDO);
            } else {
                return null;
            }
        }
        return repo.save(optional.get());
    }

    @Transactional
    @Override
    public void undo(UndoLog undoLog) throws Exception {
        List<UndoLog> undoLogs = undoLogService.findByTableNameAndIdRecordAndStatusNotOrderByCreatedDateDesc(TABLE_NAME, undoLog.getIdRecord(), 1);
        for (UndoLog log: undoLogs) {
            if (log.getId().equals(undoLog.getId())){
                Optional<UserInfo> optional = repo.findById(log.getIdRecord());
                UserInfo userInfo;
                if (optional.isPresent()) {
                    userInfo = optional.get();
                } else {
                    throw new Exception();
                }
                switch (log.getAction()) {
                    case "POST":
                        repo.deleteById(userInfo.getId());
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                    case "DELETE":
                        if(!repo.findByUsernameAndDeleted(userInfo.getUsername(), 0).isEmpty()){
                            throw new ExistsCriteria("Name đã tồn tại với: "
                                    + userInfo.getUsername());

                        }else if(!repo.findByEmailAndAndDeleted(userInfo.getEmail(), 0).isEmpty()){
                        throw new ExitsCheckEmail("Emai đang tồn tại không thể xóa");
                    }else {
                        userInfo.setDeleted(UNDO_DELETE);
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;}
                    case "PUT":
                        if(!repo.findByEmailAndAndDeleted(userInfo.getEmail(), 0).isEmpty()){
                            throw new ExitsCheckEmail("Emai đang tồn tại không thể xóa");
                        } else {
                        userInfo = undoPut(log);
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;}
                }
                if(!undoLogService.existsByTableNameAndIdRecord(TABLE_NAME, userInfo.getId()) && !log.getAction().equals("POST")){
                    userInfo.setUndoStatus(ConstantDefine.STATUS.CAN_NOT_UNDO);
                }
                if(!log.getAction().equals("POST")){
                    repo.save(userInfo);
                }
                break;
            }else {
                log.setStatus(UN_ACTIVE_UNDO_LOG);
                undoLogService.update(log, log.getId());
            }
        }
    }

    @Override
    public List<UserInfo> findByUnit(Integer unitId) {
        return repo.findByUnit(unitId);
    }

    @Override
    public List<UserInfo> getAllUser() {
        return repo.getAllUser();
    }

    @Override
    public boolean deleteUsere(Integer[] ids, Gson g, String userFromToken, HttpServletRequest request) throws Exception {
        for (Integer id:ids){
            UserInfo entity=deleteUser(id, userFromToken);
            if(entity == null){
                return  false;
            }
//            System.out.println("roles =" + entity.getRole().toString());
//            System.out.println("ids =" + id.toString());
//            if(!id.equals(entity.getRole()))
//            {
//                return false;
//            }
            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .requestObject(g.toJson(entity, UserInfo.class))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Xóa người dùng "+entity.getUsername()+" bởi " + userFromToken)
                    .createdBy(userFromToken)
                    .createdDate(LocalDateTime.now())
                    .tableName(TABLE_NAME)
                    .idRecord(entity.getId())
                    .build();
            undoLogService.create(undoLog);
        }
        return true;
    }

    @Override
    public List<Integer> getUserIdByRolesid(String username) {
        return repo.getUserIdByRolesid(username);
    }

    @Override
    public List<UserInfo> getAllUserWithoutAdmin() {
        List<UserInfo> allUser = repo.getAllUser();
        List<UserInfo> listUserWithoutAmdin = new ArrayList<>();
        for (UserInfo userInfo : allUser) {
            if (!rolesService.getListRolesCodeByUsername(userInfo.getUsername()).contains("ADMIN") &&
                    !rolesService.getListRolesCodeByUsername(userInfo.getUsername()).contains("Super Admin")) {
                listUserWithoutAmdin.add(userInfo);
            }
        }
        return listUserWithoutAmdin;
    }

    private UserInfo undoPut(UndoLog undoLog) {
        Gson g = new Gson();
        return g.fromJson(undoLog.getRevertObject(), UserInfo.class);
    }

    @Override
    public UserInfo findByEmail(String email) {
        Optional<UserInfo> entity = repo.findByEmail(email);
        return entity.orElse(null);
    }

    @Override
    public UserInfo findByForgotPasswordToken(String token) {
        Optional<UserInfo> entity = repo.findByResetPasswordToken(token);
        return entity.orElse(null);
    }

}
