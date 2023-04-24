package com.ecommerce.core.service;


import com.ecommerce.core.dto.DataProductDTO;
import com.ecommerce.core.dto.ProductDTO;
import com.ecommerce.core.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService extends IRootService<Product> {

    Page<ProductDTO> doSearch(String keyword, Integer categoryId, Pageable paging);

    Integer deleteProduct(Integer id);

    Product doUpdate(Product entity, Integer id, String updateBy);

    List<DataProductDTO> getListProduct();

}
