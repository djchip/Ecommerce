package com.ecommerce.core.dto;

import lombok.Data;

 
@Data
public class UserCredentialsDTO {
	private Integer id;
	private String username;
	private String password;
	private String role;

}
