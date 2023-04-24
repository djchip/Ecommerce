package com.ecommerce.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenusDTO {

	Integer id;
	String code;
	String title;
	String type;
	String icon;
	String url;
	String translate;
	List<MenusDTO> children;
	Integer sortBy;



}
