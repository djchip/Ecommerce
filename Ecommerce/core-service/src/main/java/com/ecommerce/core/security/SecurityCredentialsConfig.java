package com.ecommerce.core.security;

import javax.servlet.http.HttpServletResponse;

import com.ecommerce.core.config.JwtConfig;
import com.ecommerce.core.service.PrivilegesSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.ecommerce.core.repositories.UserInfoRepository;
import com.ecommerce.core.service.RolesService;
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityCredentialsConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtConfig jwtConfig;

	@Autowired
	private UserInfoRepository userRepository;
	
	@Autowired
	private RolesService rolesService;

	@Autowired
	private PrivilegesSevice privilegesSevice;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and().exceptionHandling()
				.authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED)).and()
				.addFilter(new JwtUsernameAndPasswordAuthenticationFilter(userRepository, rolesService, privilegesSevice, authenticationManager(),
                        jwtConfig))
				.authorizeRequests()
				//allow request active account
				.antMatchers(HttpMethod.GET, "/confirm-account/**").permitAll()
				// allow all POST requests
				.antMatchers(HttpMethod.POST, "/neo/login").permitAll()
				.antMatchers(HttpMethod.POST, "/neo/user-info/forgot-password").permitAll()
				.antMatchers(HttpMethod.POST, "/neo/user-info/forgot-password-confirm").permitAll()
				.antMatchers(HttpMethod.GET, "/neo/document-field/get-date-format-selected").permitAll()
				.antMatchers(HttpMethod.POST, "/neo/file/upload").permitAll()
				.antMatchers(HttpMethod.GET, "/neo/assessment/files/**").permitAll()
				.antMatchers(HttpMethod.GET, "/neo/assessment/wopi/files/**").permitAll()
				.antMatchers(HttpMethod.GET, "/neo/assessment/download/**").permitAll()
				.antMatchers(HttpMethod.GET, "/neo/ecommerce").permitAll()
				// gg search
				.antMatchers("/cse").permitAll()
				// any other requests must be authenticated
				.antMatchers("/**").permitAll().anyRequest().authenticated();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
