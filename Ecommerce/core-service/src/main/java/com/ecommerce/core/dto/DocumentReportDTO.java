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
public class DocumentReportDTO {

    String documentNumber;
    String documentName;
    String documentNameEn;
    String documentDes;
    String documentDesEn;
    String fileName;
    String documentType;
    String documentField;
    Date releaseDate;
}
