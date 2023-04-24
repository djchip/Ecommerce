package com.ecommerce.zuul.security;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.ecommerce.common.config.JwtConfig;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityTokenConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtConfig jwtConfig;

    @Autowired
    Environment env;

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*"); // TODO: lock down before deploying
		config.addAllowedHeader("*");
//		config.addAllowedOrigin("http://127.0.0.1:2222");
//		config.addAllowedOrigin("http://localhost:2222");
		config.addExposedHeader(HttpHeaders.AUTHORIZATION);
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.cors().and().csrf().disable()
				// make sure we use stateless session; session won't be used to store user's
				// state.
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				// handle an authorized attempts
				.exceptionHandling()
				.authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED)).and()
				// Add a filter to validate the tokens with every request
                .addFilterAfter(new JwtTokenAuthenticationFilter(jwtConfig), UsernamePasswordAuthenticationFilter.class)
				// authorization requests config
				.authorizeRequests()
				// allow all who are accessing "auth\login" service
				.antMatchers(HttpMethod.POST, "/neo/login").permitAll()
				.antMatchers(HttpMethod.POST, "/neo/file/upload").permitAll()
				.antMatchers(HttpMethod.GET, "/neo/menu/get-menu").permitAll()
				.antMatchers(HttpMethod.POST, "/neo/user-info/forgot-password").permitAll()
				.antMatchers(HttpMethod.POST, "/neo/user-info/forgot-password-confirm").permitAll()
				.antMatchers(HttpMethod.GET, "/neo/document-field/get-date-format-selected").permitAll()
				.antMatchers(HttpMethod.GET, "/neo/assessment/files/*").permitAll()
				.antMatchers(HttpMethod.GET, "/neo/assessment/wopi/files/*").permitAll()
				.antMatchers(HttpMethod.GET, "/neo/assessment/download/*").permitAll()
				// Any other request must be authenticated
				.anyRequest().authenticated();
	}

	@Bean
	public JwtConfig jwtConfig() {
		return new JwtConfig();
	}
}
