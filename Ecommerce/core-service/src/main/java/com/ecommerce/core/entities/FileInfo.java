package com.ecommerce.core.entities;

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
public class FileInfo {
    @JsonProperty("BaseFileName")
    private String BaseFileName;

    @JsonProperty("Size")
    private int Size;

    @JsonProperty("UserId")
    private Integer UserId;

    @JsonProperty("UserCanWrite")
    private boolean UserCanWrite;

    @JsonProperty("UserFriendlyName")
    private String UserFriendlyName;
}
