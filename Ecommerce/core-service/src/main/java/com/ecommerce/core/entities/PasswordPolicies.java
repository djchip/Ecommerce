package com.ecommerce.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "PASSWORD_POLICIES")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor

public class PasswordPolicies {
	@Id
	@Column(name = "ID", columnDefinition = "INT")
    private Integer id;

    @Column(name = "PASSWORD_LIFETIME")
    private Integer passwordLifeTime;

    @Column(name = "PASSWORD_MIN_LENGTH")
    private Integer passwordMinLength;
    
    @Column(name = "MIN_LOWERCASE_CHARACTER")
    private Integer minLowercaseCharacter;
    
    @Column(name = "MIN_UPPERCASE_CHARACTER")
    private Integer minUppercaseCharacter;
    
    @Column(name = "MIN_NUMBER_CHARACTER")
    private Integer minNumberCharacter;
    
    @Column(name = "MIN_SPECIAL_CHARACTER")
    private Integer minSpecialCharacter;
    
    @Column(name = "CHANGE_PASS_FIRST_LOGIN")
    private Integer changePassFirstLogin;
}
