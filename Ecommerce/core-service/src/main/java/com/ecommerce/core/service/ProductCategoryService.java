package com.ecommerce.core.service;

import com.ecommerce.core.dto.ProductCategoryDTO;
import com.ecommerce.core.entities.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductCategoryService extends IRootService<ProductCategory>{

    Page<ProductCategoryDTO> doSearch(String keyword, Pageable paging);

    List<ProductCategory> findAllByActiveIsTrue();

    ProductCategory findByCategoryName(String categoryName);

    ProductCategoryDTO findProductCategoryDTOById(Integer id);

    ProductCategory findByIdAndActiveTrue(Integer id);

    boolean deleteMultiple(Integer[] ids) throws Exception;

    List<ProductCategory> findByParentId(Integer parentId);

    boolean deleteCategory(Integer id);
}
