package com.ecommerce.zuul.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecommerce.common.constants.ErrorMessageDefine;
import com.ecommerce.common.constants.ResponseFontendDefine;
import com.ecommerce.common.dto.ResponseModel;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest httpServletRequest, HttpServletResponse response, AuthenticationException e)
			throws IOException, ServletException {
		log.warn("JwtAuthenticationEntryPoint:UNAUTHORIZED");
		// Response to body
		Map<String, String> json = new HashMap<String, String>();
		json.put("TOKEN", "WRONG");
		ResponseModel responseModel = new ResponseModel();
		responseModel.setErrorMessages(ErrorMessageDefine.ACC_FORBIDDEN);
		responseModel.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
		responseModel.setCode(ResponseFontendDefine.CODE_PERMISSION);
		responseModel.setContent(json);

		byte[] body = new ObjectMapper().writeValueAsBytes(responseModel);
		response.getOutputStream().write(body);
	}
}