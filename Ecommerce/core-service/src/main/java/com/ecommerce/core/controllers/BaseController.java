package com.ecommerce.core.controllers;

import javax.servlet.http.HttpServletRequest;

import com.ecommerce.core.adapter.HibernateProxyTypeAdapter;
import com.ecommerce.core.constants.ErrorMessageDefine;
import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.exceptions.RalException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseController {

	@Value("${user.default.reset.password}")
	public String USER_DEFAULT_RESET_PASSWORD;
	
	@Value("${security.jwt.secret:JwtSecretKey}")
	private String secret;

	GsonBuilder b = new GsonBuilder();
	
	public String getUserFromToken(HttpServletRequest request) {
		try {
			String bearerToken = request.getHeader("Authorization");
			if (bearerToken.startsWith("Bearer ")) {
				bearerToken = bearerToken.replaceAll("Bearer ", "").trim();
			}
			Claims claims = Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(bearerToken).getBody();
			return claims.get("sub").toString();
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RalException(HttpStatus.SC_OK, ResponseFontendDefine.CODE_BUSINESS,
					ErrorMessageDefine.ACC_FORBIDDEN);
		}
	}

	public boolean isAdminFromToken(HttpServletRequest request) {
		try {
			String bearerToken = request.getHeader("Authorization");
			if (bearerToken.startsWith("Bearer ")) {
				bearerToken = bearerToken.replaceAll("Bearer ", "").trim();
			}
			Claims claims = Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(bearerToken).getBody();
			return Boolean.parseBoolean(claims.get("adm").toString());
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RalException(HttpStatus.SC_OK, ResponseFontendDefine.CODE_BUSINESS,
					ErrorMessageDefine.ACC_FORBIDDEN);
		}
	}

	protected RalException handleException(Exception exception) {

		log.error("Error [{}]", exception.getMessage(), exception);

		String cause = "";
		if (exception.getCause() != null) {
			cause = exception.getCause().getClass().getCanonicalName();
		}

		if (exception instanceof RalException) {
			throw new RalException(HttpStatus.SC_OK, ((RalException) exception).getCode(), exception.getMessage());
		} else if (exception instanceof IllegalArgumentException || cause.contains("IncorrectParameterException")
				|| cause.contains("SQLGrammarException")) {
			return new RalException(HttpStatus.SC_INTERNAL_SERVER_ERROR, ResponseFontendDefine.BAD_REQUEST_PARAMS,
					exception.getMessage());
		} else {
			return new RalException(ResponseFontendDefine.GENERAL, exception.getMessage());
		}

	}
	
	protected Boolean checkNull(Object obj) {
    	if(StringUtils.isEmpty(obj)) {
    		return true;
    	}
    	return false;
    }

	protected Gson createGson() {
		b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
		return b.create();
	}
}
