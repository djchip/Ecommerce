package com.ecommerce.core.dto;

import com.ecommerce.core.entities.Proof;
import com.ecommerce.core.entities.UserInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EvaluatedDTO {
    Integer id;
    Integer evaluated;
    String comment;
    List<UserInfo> user;
    List<Proof> proofs;
}
