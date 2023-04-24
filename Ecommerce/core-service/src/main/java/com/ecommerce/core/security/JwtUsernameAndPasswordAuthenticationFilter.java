package com.ecommerce.core.security;

import com.ecommerce.core.config.JwtConfig;
import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.service.PrivilegesSevice;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecommerce.core.dto.PrivilegesDTO;
import com.ecommerce.core.dto.ResponseModel;
import com.ecommerce.core.dto.UserCredentialsDTO;
import com.ecommerce.core.entities.Roles;
import com.ecommerce.core.entities.UserInfo;
import com.ecommerce.core.repositories.UserInfoRepository;
import com.ecommerce.core.service.RolesService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	// We use auth manager to validate the user credentials
	private AuthenticationManager authManager;

	private final JwtConfig jwtConfig;

	private UserInfoRepository userRepository;
	
	private RolesService roleService;

	private PrivilegesSevice privilegesSevice;

//    private String token = null;

	public JwtUsernameAndPasswordAuthenticationFilter(UserInfoRepository userRepository,RolesService rolesService, PrivilegesSevice privilegesSevice, AuthenticationManager authManager,
            JwtConfig jwtConfig) {
		this.userRepository = userRepository;
		this.authManager = authManager;
		this.jwtConfig = jwtConfig;
		this.roleService = rolesService;
		this.privilegesSevice = privilegesSevice;
		// POST: auth/login
		this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(jwtConfig.getUri(), "POST"));
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {

		try {
			// 1. Get credentials from request
			UserCredentialsDTO creds = new ObjectMapper().readValue(request.getInputStream(), UserCredentialsDTO.class);
			// log.info("First request user login: " + creds.toString());

			// 2. Create auth object (contains credentials)
			// which will be used by auth manager
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(creds.getUsername(),
					creds.getPassword(), Collections.emptyList());

			// 3. Authentication manager authenticate the user, and use
			// UserDetialsServiceImpl::loadUserByUsername() method to load the user.
            Authentication auth = authManager.authenticate(authToken);

//            try {
//                ResObject rs = wso2.login(creds.getUsername(), creds.getPassword());
//                if (rs.getStatus() == 200) {
//                    JSONObject j = new JSONObject(rs.getBody());
//                    token = j.getString("access_token");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            return auth;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// Upon successful authentication, generate a token.
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		// Get user login
		UserInfo user = userRepository.findByUsername(auth.getName()).get();
		if (user.getStatus() != ConstantDefine.STATUS.ACTIVE) {
			// Response to body
			Map<String, String> json = new HashMap<String, String>();
			// DatND 16-09
			if (user.getStatus() == ConstantDefine.STATUS.DELETED)
				json.put("message", "Người dùng đã bị xóa.");
			else if (user.getStatus() == ConstantDefine.STATUS.LOCKED)
				json.put("message", "Người dùng đã bị khóa.");
			ResponseModel res = new ResponseModel();
			res.setStatusCode(HttpServletResponse.SC_OK);
			res.setCode(ResponseFontendDefine.CODE_PERMISSION);
			res.setContent(json);

			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding("UTF-8");
			byte[] body = new ObjectMapper().writeValueAsBytes(res);
			response.getOutputStream().write(body);
			return;
		}
		
		// Create JWT from user
//        if (token == null) {

//		Roles role = roleService.retrieve(user.getRoleId());
		List<Integer> listRoleId = new ArrayList<>();
		StringBuilder role = new StringBuilder();
		boolean roleAdmin = false;
		for (Roles a : user.getRole()) {
			listRoleId.add(a.getId());
			role.append(a.getId()).append(",");
			if (a.getRoleCode().equalsIgnoreCase("admin") || a.getRoleCode().equalsIgnoreCase("Super Admin")){
				roleAdmin = true;
			}
		}
		List<PrivilegesDTO> privilegesAction = privilegesSevice.getListPrivilegesAction(listRoleId);
		Long now = System.currentTimeMillis();
        String token = Jwts.builder().setSubject(
        		auth.getName())
        			.claim("name", user.getFullname())
                    .claim("type", role)
					.claim("adm", roleAdmin)
					.claim("action", privilegesAction)
                    // Convert to list of strings.
                    // This is important because it affects the way we get them back in the Gateway.
                    .claim("id", user.getId()).setIssuedAt(new Date(now)).setIssuedAt(new Date(now))
                    .setExpiration(new Date(now + jwtConfig.getExpiration() * 1000))
                    // in milliseconds
                    .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret().getBytes()).compact();

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            Date date = new Date(now + jwtConfig.getExpiration() * 1000);
            log.info("Infor Token: email: " + auth.getName() + " id: " + user.getId() + " time remaining : "
                    + formatter.format(date));
//        }

		// Add token to header
		response.addHeader(jwtConfig.getHeader(), jwtConfig.getPrefix() + token);

		// Response to body
		Map<String, String> json = new HashMap<String, String>();
		json.put("LOGIN", "SUCCESS");
		ResponseModel res = new ResponseModel();
		res.setStatusCode(HttpServletResponse.SC_OK);
		res.setCode(ResponseFontendDefine.CODE_SUCCESS);
		json.put("Bearer", jwtConfig.getPrefix() + token);
		res.setContent(json);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		byte[] body = new ObjectMapper().writeValueAsBytes(res);
		response.getOutputStream().write(body);
	}

}
