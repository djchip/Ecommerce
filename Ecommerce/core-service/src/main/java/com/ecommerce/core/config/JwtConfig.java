package com.ecommerce.core.config;

import org.springframework.beans.factory.annotation.Value;

import lombok.Data;

@Data
public class JwtConfig {

	// Link: https://stackoverflow.com/a/6897406
	@Value("${security.jwt.uri:/login}")
	private String Uri;

	@Value("${security.jwt.header:Authorization}")
	private String header;

	@Value("${security.jwt.prefix:Bearer }")
	private String prefix;

	@Value("${security.jwt.expiration:#{24*60*60}}")
	private int expiration;

	@Value("${security.jwt.secret:JwtSecretKey}")
	private String secret;
	

}
