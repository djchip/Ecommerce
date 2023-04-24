package com.ecommerce.core.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class RolePrivilegesId implements Serializable{

	@Column(name = "role_id")
    private Integer roleId;
	
    @Column(name = "privileges_id")
    private Integer privilegesId;

}
