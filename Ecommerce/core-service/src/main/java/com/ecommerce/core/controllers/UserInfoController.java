package com.ecommerce.core.controllers;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.*;
import com.ecommerce.core.entities.PasswordPolicies;
import com.ecommerce.core.entities.Roles;
import com.ecommerce.core.entities.UndoLog;
import com.ecommerce.core.entities.UserInfo;
import com.ecommerce.core.security.SecurityCredentialsConfig;
import com.ecommerce.core.service.PasswordPoliciesService;
import com.ecommerce.core.service.RolesService;
import com.ecommerce.core.service.UndoLogService;
import com.ecommerce.core.service.UserInfoService;
import com.ecommerce.core.util.CommonUtil;
import com.ecommerce.core.util.MailService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("user-info")
@Slf4j
public class UserInfoController extends BaseController {
    private final String START_LOG = "UserInfo {}";
    private final String END_LOG = "UserInfo {} finished in: {}";
    private final String TABLE_NAME = "UserInfo";

//	private final Gson g = createGson();

    Gson g = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    @Autowired
    UserInfoService service;

    @Autowired
    SecurityCredentialsConfig config;

    @Autowired
    RolesService rolesService;

    @Autowired
    PasswordPoliciesService passwordPoliciesService;

    @Autowired
    MailService mailService;

    @Autowired
    UndoLogService undoLogService;

    @GetMapping()
    public ResponseModel doSearch(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "roleId", required = false) Integer roleId,
                                  @RequestParam(value = "unitId", required = false) Integer unitId,
                                  @RequestParam(value = "sort", required = false) boolean sortDesc,
                                  @RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int perPage,
                                  HttpServletRequest request) {
        final String action = "doSearch";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            Pageable paging = PageRequest.of(currentPage, perPage);
            Page<UserInfo> pageResult = null;
            if (keyword.equals("")) {
                keyword = null;
            } else {
                keyword = keyword.trim();
            }
            pageResult = service.doSearch(keyword, roleId, unitId, paging);
            if ((pageResult == null || pageResult.isEmpty())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy người dùng.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }
            PagingResponse<UserInfo> result = new PagingResponse<>();
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

    @GetMapping("get-list-by-role")
    public ResponseModel getListUserByRole(@RequestParam(value = "roleId", required = true) Integer roleId,
                                           HttpServletRequest request) {
        final String action = "getListUserByRole";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {

            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(service.getListUserByRole(roleId));
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

    @GetMapping("{id}")
    public ResponseModel doRetrieve(@PathVariable Integer id) {
        final String action = "doRetrieve";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            UserInfo entity = service.retrieve(id);
            if (entity == null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy người dùng.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }
            // Todo hardcode
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(entity);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("find-by-username")
    public ResponseModel doRetrieve1(@RequestParam("username") String username) {
        final String action = "doRetrieve";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            UserInfo entity = service.findByUsername(username);
            if (entity == null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy người dùng.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }
            // Todo hardcode
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(entity);
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
    @Transactional
    public ResponseModel doCreate(@RequestBody UserInfo entity, HttpServletRequest request) {

        final String action = "doCreate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            String customUserName = entity.getUsername().trim();
            String customPassword = entity.getPassword().trim();
            String customFullName = entity.getFullname().trim();
            String customEmail = entity.getEmail().trim();
            String customPhone = entity.getPhoneNumber().trim();
            List<Roles> listRoles = entity.getRole();
            //check tên đăng nhập đã tồn tại
            UserInfo checkExisted = service.findByUsername(customUserName);
            if (checkExisted != null && checkExisted.getStatus() != ConstantDefine.STATUS.DELETED && checkExisted.getDeleted() != ConstantDefine.IS_DELETED.TRUE) {
                // username đã tồn tại
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_ALREADY_EXIST);
                responseModel.setErrorMessages("Người dùng đã tồn tại.");
                return responseModel;
            }
            //check email đã tồn tại
            UserInfo checkEmailExisted = service.findByEmail(customEmail);
            if (checkEmailExisted != null && checkEmailExisted.getStatus() != ConstantDefine.STATUS.DELETED && checkEmailExisted.getDeleted() != ConstantDefine.IS_DELETED.TRUE) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_GMAIL_EXIST);
                responseModel.setErrorMessages("Email đã tồn tại trên hệ thống.");
                return responseModel;
            }
            String createdBy = getUserFromToken(request);
            entity.setUsername(customUserName);
            String password = config.passwordEncoder().encode(customPassword);
            entity.setPassword(password);
            entity.setFullname(customFullName);
            entity.setEmail(customEmail);
            entity.setPhoneNumber(customPhone);
            entity.setCreatedBy(createdBy);
            entity.setStatus(ConstantDefine.STATUS.ACTIVE);
            entity.setRole(listRoles);
            entity.setUndoStatus(ConstantDefine.STATUS.CAN_BE_UNDO);
            entity.setDeleted(ConstantDefine.IS_DELETED.FALSE);
            entity = service.create(entity);

            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .requestObject(g.toJson(entity))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Thêm mới người dùng "+entity.getUsername()+" bởi " + createdBy)
                    .createdDate(LocalDateTime.now())
                    .tableName(TABLE_NAME)
                    .idRecord(entity.getId())
                    .createdBy(createdBy)
                    .build();
            undoLogService.create(undoLog);
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

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    @PutMapping()
    @Transactional
    public ResponseModel doUpdate(@RequestBody UserInfo dto, HttpServletRequest request) {
        final String action = "doUpdate";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            System.out.println("dto = " + dto);
            String customEmail = dto.getEmail().trim();
            UserInfo entityEmail = service.retrieve(dto.getId());

            //check email đã tồn tại
            UserInfo checkEmailExisted = service.findByEmail(customEmail);
            if (checkEmailExisted != null && checkEmailExisted.getStatus() != ConstantDefine.STATUS.DELETED && checkEmailExisted.getDeleted() != ConstantDefine.IS_DELETED.TRUE && !checkEmailExisted.getEmail().equalsIgnoreCase(entityEmail.getEmail())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_GMAIL_EXIST);
                responseModel.setErrorMessages("Email đã tồn tại trên hệ thống.");
                return responseModel;
            }

            UserInfo entity = service.findByUsername(dto.getUsername().trim());
            String createdBy = getUserFromToken(request);
            UndoLog undoLog = UndoLog.undoLogBuilder()
                    .action(request.getMethod())
                    .revertObject(g.toJson(entity, UserInfo.class))
                    .status(ConstantDefine.STATUS.UNDO_NEW)
                    .url(request.getRequestURL().toString())
                    .description("Cập nhập người dùng "+entity.getUsername()+" bởi " + createdBy)
                    .updatedDate(LocalDateTime.now())
                    .createdBy(createdBy)
                    .tableName(TABLE_NAME)
                    .idRecord(entity.getId())
                    .build();
            entity.setUsername(dto.getUsername().trim());
            entity.setFullname(dto.getFullname().trim());
            entity.setEmail(dto.getEmail().trim());
            entity.setPhoneNumber(dto.getPhoneNumber().trim());
            entity.setRole(dto.getRole());
            entity.setUpdatedBy(createdBy);
            entity.setUpdatedDate(LocalDateTime.now());
            entity.setUnit(dto.getUnit());
            service.update(entity, entity.getId());
            // save undo
            undoLog.setRequestObject(g.toJson(entity, UserInfo.class));
            undoLogService.create(undoLog);
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
            String createdBy = getUserFromToken(request);
            UserInfo entity = service.deleteUser(id, createdBy);
          if(entity != null) {
              UndoLog undoLog = UndoLog.undoLogBuilder()
                      .action(request.getMethod())
                      .requestObject(g.toJson(entity, UserInfo.class))
                      .status(ConstantDefine.STATUS.UNDO_NEW)
                      .url(request.getRequestURL().toString())
                      .description("Xóa người dùng "+ entity.getUsername()+" bởi " + createdBy)
                      .createdDate(LocalDateTime.now())
                      .createdBy(createdBy)
                      .tableName(TABLE_NAME)
                      .idRecord(entity.getId())
                      .build();
              undoLogService.create(undoLog);
              ResponseModel responseModel = new ResponseModel();
              responseModel.setStatusCode(HttpStatus.SC_OK);
              responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
              return responseModel;
          }
            ResponseModel responseModel = new ResponseModel();
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS1);
            return responseModel;
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PutMapping("delete/{id}")
    public ResponseModel doDeleteLogic(@PathVariable Integer id, HttpServletRequest request)
            throws Exception{
        final String action = "doDelete";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        ResponseModel responseModel = new ResponseModel();

        try {
            UserInfo entity = service.retrieve(id);
            entity.setStatus(ConstantDefine.STATUS.DELETED);
            service.update(entity, id);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;

        } catch (Exception e) {
            throw e;
        } finally {
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PutMapping("lock/{id}")
    public ResponseModel doLock(@PathVariable Integer id, HttpServletRequest request) {
        final String action = "doDelete";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            UserInfo entity = service.retrieve(id);
            entity.setStatus(ConstantDefine.STATUS.LOCKED);
            entity.setUpdatedBy(getUserFromToken(request));
            service.update(entity, id);
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

    @PutMapping("unlock/{id}")
    public ResponseModel doUnlock(@PathVariable Integer id, HttpServletRequest request) {
        final String action = "doDelete";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            UserInfo entity = service.retrieve(id);
            entity.setStatus(ConstantDefine.STATUS.ACTIVE);
            entity.setUpdatedBy(getUserFromToken(request));
            service.update(entity, id);
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

    @PostMapping("/change-password")
    public ResponseModel changePassword(@RequestBody PasswordDTO dto, HttpServletRequest request) {
        final String action = "changePassword";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            if (checkNull(dto.getOldPassword()) || checkNull(dto.getNewPassword())
                    || checkNull(dto.getConfirmPassword())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_VALIDATION);
                responseModel.setErrorMessages("Thông tin nhập vào chưa đầy đủ.");
                return responseModel;
            }
            UserInfo user = service.findByUsername(getUserFromToken(request));
            if (user == null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_VALIDATION);
                responseModel.setErrorMessages("Người dùng không tồn tại.");
                return responseModel;
            }
            if (!user.getStatus().equals(ConstantDefine.STATUS.ACTIVE)) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_VALIDATION);
                responseModel.setErrorMessages("Người dùng không ở trạng thái hoạt động.");
                return responseModel;
            }

            if (!config.passwordEncoder().matches(dto.getOldPassword(), user.getPassword())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_PASS1);
                responseModel.setErrorMessages("Mật khẩu không chính xác.");
                return responseModel;
            }

            if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_PASS2);
                responseModel.setErrorMessages("Mật khẩu mới nhập vào không chính xác.");
                return responseModel;
            }

            // Check passwordPolicy
            PasswordPolicies policy = passwordPoliciesService.getPasswordPolicy();
            if (policy != null) {
                if (!CommonUtil.checkPasswordPolicy(policy, dto.getNewPassword())) {
                    ResponseModel responseModel = new ResponseModel();
                    responseModel.setStatusCode(HttpStatus.SC_OK);
                    responseModel.setCode(ResponseFontendDefine.CODE_PASS2);
                    responseModel.setErrorMessages("Mật khẩu mới nhập vào không chính xác.");
                    return responseModel;
                }
            }
            String password = config.passwordEncoder().encode(dto.getNewPassword());
            user.setPassword(password);
            user.setUpdatedBy(user.getUsername());
            user.setUpdatedDate(LocalDateTime.now());
            service.update(user, user.getId());

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

    @PostMapping("/create-password")
    public ResponseModel createPassword(@RequestBody PasswordDTO dto, HttpServletRequest request) {
        final String action = "createPassword";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            if (checkNull(dto.getNewPassword()) || checkNull(dto.getConfirmPassword())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_VALIDATION);
                responseModel.setErrorMessages("Thông tin nhập vào chưa đầy đủ.");
                return responseModel;
            }

            UserInfo user = service.findByUsername(getUserFromToken(request));
            if (user == null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_VALIDATION);
                responseModel.setErrorMessages("Người dùng không tồn tại.");
                return responseModel;
            }
            if (!user.getStatus().equals(ConstantDefine.STATUS.ACTIVE)) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_VALIDATION);
                responseModel.setErrorMessages("Người dùng không ở trạng thái hoạt động.");
                return responseModel;
            }
            if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_PASS2);
                responseModel.setErrorMessages("Mật khẩu mới nhập vào không chính xác.");
                return responseModel;
            }
            // Check passwordPolicy
            PasswordPolicies policy = passwordPoliciesService.getPasswordPolicy();
            if (policy != null) {
                if (!CommonUtil.checkPasswordPolicy(policy, dto.getNewPassword())) {
                    ResponseModel responseModel = new ResponseModel();
                    responseModel.setStatusCode(HttpStatus.SC_OK);
                    responseModel.setCode(ResponseFontendDefine.CODE_PASS2);
                    responseModel.setErrorMessages("Mật khẩu mới nhập vào không chính xác.");
                    return responseModel;
                }
            }
            String password = config.passwordEncoder().encode(dto.getNewPassword());
            user.setPassword(password);
            user.setUpdatedBy(user.getUsername());
            user.setUpdatedDate(LocalDateTime.now());
            service.update(user, user.getId());
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

    @PostMapping("/reset-password")
    public ResponseModel resetPassword(@RequestBody UserDTO dto, HttpServletRequest request) {
        final String action = "changePassword";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);

        try {
            UserInfo user = service.retrieve(dto.getUserId());
            if (user == null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_VALIDATION);
                responseModel.setErrorMessages("Người dùng không tồn tại.");
                return responseModel;
            }

            // Sinh mật khẩu mới
            PasswordPolicies policy = passwordPoliciesService.getPasswordPolicy();
            String newPassword = CommonUtil.generatePassword(policy);
            String encodePassword = config.passwordEncoder().encode(newPassword);
            user.setPassword(encodePassword);
            user.setUpdatedBy(getUserFromToken(request));
            user.setUpdatedDate(LocalDateTime.now());

            service.update(user, user.getId());
            String[] to = {user.getEmail()};
            // Gửi mail báo mật khẩu mới cho người dùng
            mailService.sendResetPasswordMail(to, user.getFullname(), newPassword);

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

    @PostMapping("/forgot-password")
    public ResponseModel forgotPassword(@RequestBody ConfirmPasswordDTO dto, HttpServletRequest request) {
        final String action = "forgotPassword";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);

        try {
            // check emai tồn tại
            UserInfo user = service.findByEmail(dto.getEmail());
            if (user == null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_VALIDATION);
                responseModel.setErrorMessages("User không tồn tại.");
                return responseModel;
            }

            // Sinh token
            UUID uuid = UUID.randomUUID();
            String resetPasswordToken = uuid.toString() + user.getId();
            System.out.println(resetPasswordToken);
            user.setResetPasswordToken(resetPasswordToken);
            service.update(user, user.getId());
            // Gửi email confirm
            String[] to = {user.getEmail()};
            mailService.sendConfirmForgotPasswordMail(to, user.getFullname(), resetPasswordToken);
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

    @PostMapping("/forgot-password-confirm")
    public ResponseModel confirmResetPassword(@RequestBody ConfirmPasswordDTO dto, HttpServletRequest request) {
        final String action = "forgotPassword";
        StopWatch sw = new StopWatch();
        log.info(START_LOG, action);
        try {
            // check user theo token
            UserInfo user = service.findByForgotPasswordToken(dto.getToken());
            if (user == null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_VALIDATION);
                responseModel.setErrorMessages("User không tồn tại.");
                return responseModel;
            }
            // Sinh mật khẩu mới
            PasswordPolicies policy = passwordPoliciesService.getPasswordPolicy();
            String newPassword = CommonUtil.generatePassword(policy);
            String encodePassword = config.passwordEncoder().encode(newPassword);
            user.setPassword(encodePassword);
            user.setResetPasswordToken(null);
            service.update(user, user.getId());
            String[] to = {user.getEmail()};

            // Gửi mail báo mật khẩu mới cho người dùng
            mailService.sendResetPasswordMail(to, user.getFullname(), newPassword);
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

    @GetMapping("get-all")
    public ResponseModel getAllUser(){
        final String action = "getAllUser";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(service.getAllUser());
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

    @GetMapping("get-all-without-admin")
    public ResponseModel getAllUserWithoutAdmin(){
        final String action = "getAllUser";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(service.getAllUserWithoutAdmin());
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

    @DeleteMapping("delete-multi")
    public ResponseModel deleteMulti(@RequestBody Integer[] ids, HttpServletRequest request) {
        final String action = "doDeleteMulti";
        ResponseModel responseModel = new ResponseModel();
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
//            String userName = null;
//            if (!isAdminFromToken(request)){
//                userName = getUserFromToken(request);
//            }

            if(service.deleteUsere(ids, g, getUserFromToken(request), request)){
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
                return responseModel;
            }else{
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.BEING_USED);
                return responseModel;
            }

        } catch (Exception e) {
            throw handleException(e);
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }


    @GetMapping("getUserIdByRolesid")
    public ResponseModel getListActiveUnits(@RequestParam("username") String username,HttpServletRequest request) {
        final String action = "getUserIdByRolesid";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            List<Integer> entity=service.getUserIdByRolesid(username);
            if (entity == null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setErrorMessages("Không tìm thấy vai trof id naof.");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }
            ResponseModel responseModel = new ResponseModel();
            responseModel.setContent(entity);
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
