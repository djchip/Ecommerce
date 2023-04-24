package com.ecommerce.core.service.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ecommerce.core.entities.UserInfo;
import com.ecommerce.core.repositories.UserInfoRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Quach Hoang Anh
 * @Email: anhqh@vnpt.vn
 * @Version 1.0.0
 */

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserInfoRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		// check User exist with username
		Optional<UserInfo> user = userRepository.findByUsername(username);
		if (!user.isPresent()) {
			throw new UsernameNotFoundException("API xác thực không tìm thấy user: " + username);
		}

		Set<GrantedAuthority> listGrantedAuthorities = new HashSet<GrantedAuthority>();

		return new User(user.get().getUsername(), user.get().getPassword(), listGrantedAuthorities);

	}
}