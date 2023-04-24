package com.ecommerce.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

 
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseModel {

	protected String requestIdentifier;
	protected Integer statusCode;
	protected Integer code;
	protected String errorMessages;
	protected Integer currentPage;
	protected Integer totalRecord;
	protected Integer totalPage;
//	@JsonProperty("data")
	protected Object content;
	protected String ErrorCode;
	protected String BotChat;
	protected Integer Timereminder;
	protected Boolean is_action;
}
