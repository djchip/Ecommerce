package com.ecommerce.core.entities;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "role_privileges")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePrivileges { 
	
	@EmbeddedId
    private RolePrivilegesId id;

}
