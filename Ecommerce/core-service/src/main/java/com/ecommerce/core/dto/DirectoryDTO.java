package com.ecommerce.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DirectoryDTO {
    Integer id;
    String name;
    String nameEn;
    String code;
    String description;
    String descriptionEn;
    String create_by;
    String update_by;
//    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    Date createdDate;
//    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    Date updatedDate;
    String programName;
    Integer programId;
    Integer orderDir;
    Integer undoStatus;
    Integer delete;
    Integer OrganizaId;
    String OrganizaNam;
    String OrganizaNamEn;
     Integer categoryId;
    String categoryName;
    String categoryNameEn;
//    List<UserInfo> userInfos;

}
