package com.ecommerce.core.service;

import com.ecommerce.core.entities.UndoLog;
import com.ecommerce.core.entities.UserInfo;
import com.google.gson.Gson;
import com.ecommerce.core.dto.UserInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;


public interface UserInfoService extends IRootService<UserInfo>{
    Optional<UserInfo> findById(Integer id);

    Page<UserInfo> doSearch(String keyword, Integer roleId, Integer unitId, Pageable paging);
    UserInfo findByUsername(String username);
    UserInfo findByEmail(String email);
    UserInfo findByForgotPasswordToken(String token);
    List<UserInfoDTO> getListUserByRole(Integer roleId);

    UserInfo deleteUser(Integer id, String userName) throws Exception;
    void undo(UndoLog undoLog) throws Exception;

    List<UserInfo> findByUnit(Integer unitId);

    List<UserInfo> getAllUser();
    boolean deleteUsere(Integer[] ids, Gson g, String userFromToken, HttpServletRequest request) throws Exception;

    List<Integer> getUserIdByRolesid(String username);

    List<UserInfo> getAllUserWithoutAdmin();

}
