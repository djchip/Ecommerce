package com.ecommerce.core.service;

import org.springframework.stereotype.Service;

import com.ecommerce.core.entities.PasswordPolicies;


@Service
public interface PasswordPoliciesService extends IRootService<PasswordPolicies>{

	PasswordPolicies getPasswordPolicy();
}
