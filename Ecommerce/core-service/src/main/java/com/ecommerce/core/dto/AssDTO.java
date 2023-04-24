package com.ecommerce.core.dto;

import com.ecommerce.core.entities.UserInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssDTO {
    Integer id;
    String name;
    String nameEn;
    String description;
    String descriptionEn;
    String createdBy;
    Date createdDate;
    String updatedBy;
    Date updatedDate;
    Integer evaluated;
    String file;
    String comment;
    Integer programId;
    Integer directoryId;
    Integer criteriaId;
    List<UserInfo> user;
    List<UserInfo> viewers;
}
