package com.ecommerce.core.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PagingRequest {
	Integer currentPage;
    Integer perPage;
    String filter;
    String sortBy;
    boolean sortDesc;   // true: DESC, false ASC
}
