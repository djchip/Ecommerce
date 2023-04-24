package com.ecommerce.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExhibitionDTO {

	private Integer id;
	private String proofName;
	private String proofCode;
	private String standardName;
	private String criteriaName;
    private String documentType;
    private String numberSign;
    private LocalDateTime releaseDate;
    private String signer;
    private String releaseBy;
    private String description;
}
