package com.ecommerce.core.util;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import com.ecommerce.core.dto.PagingRequest;

public class AppUtils {
    public static PageRequest getPaging(PagingRequest request) {
        PageRequest pageable = null;
        if (StringUtils.isEmpty(request.getSortBy())) {
            pageable = PageRequest.of(
                    request.getCurrentPage() == null ? 0 : request.getCurrentPage(),
                    request.getPerPage() == null ? 10 : request.getPerPage());
        } else {
            pageable = PageRequest.of(
                    request.getCurrentPage() == null ? 0 : request.getCurrentPage(),
                    request.getPerPage() == null ? 10 : request.getPerPage(),
                    Sort.by(request.isSortDesc() ? Sort.Direction.DESC : Sort.Direction.ASC, request.getSortBy()));
        }
        return pageable;
    }
    
    /**
     * Check data is null or empty.
     *
     * @param <T>  the type of the data
     * @param data the input data
     * @return {@code true} if data is null or empty, otherwise {@code false}
     */
    public static <T> boolean isDataNullOrEmpty(T data) {
        if (data == null) {
            return true;
        }

        if (data instanceof String) {
            return ((String) data).isEmpty();
        }

        if (data instanceof List) {
            return ((List<?>) data).isEmpty();
        }

        if (data instanceof Map) {
            return ((Map<?, ?>) data).isEmpty();
        }

        return false;
    }
}
