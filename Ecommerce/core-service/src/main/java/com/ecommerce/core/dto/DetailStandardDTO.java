package com.ecommerce.core.dto;

import com.ecommerce.core.entities.UserInfo;
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
public class DetailStandardDTO {
    DirectoryDTO entity;
    List<UserInfo> userInfos;
}
