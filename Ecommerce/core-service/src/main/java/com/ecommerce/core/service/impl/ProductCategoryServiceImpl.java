package com.ecommerce.core.service.impl;

import com.ecommerce.core.dto.ProductCategoryDTO;
import com.ecommerce.core.entities.Product;
import com.ecommerce.core.entities.ProductCategory;
import com.ecommerce.core.repositories.ProductCategoryRepository;
import com.ecommerce.core.repositories.ProductRepository;
import com.ecommerce.core.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    @Autowired
    private ProductCategoryRepository repository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Page<ProductCategoryDTO> doSearch(String keyword, Pageable paging) {
        return repository.doSearch(keyword, paging);
    }

    @Override
    public List<ProductCategory> findAllByActiveIsTrue() {
        return repository.findAllByActiveIsTrue();
    }

    @Override
    public ProductCategory findByCategoryName(String categoryName) {
        return repository.findByCategoryNameAndActiveIsTrue(categoryName);
    }

    @Override
    public ProductCategoryDTO findProductCategoryDTOById(Integer id) {
        return repository.findProductCategoryDTOById(id);
    }

    @Override
    public ProductCategory findByIdAndActiveTrue(Integer id) {
        return repository.findByIdAndActiveTrue(id);
    }

    @Override
    public boolean deleteMultiple(Integer[] ids) {
        boolean canDelete = true;
        for(Integer id : ids){
            List<ProductCategory> categories = repository.findByParentId(id);
            List<Product> products = productRepository.findByCategoryId(id);
            if(categories.size() > 0 || products.size() > 0){
                canDelete = false;
                break;
            }
        }
        if(canDelete){
            for(Integer id : ids){
                ProductCategory productCategory = repository.findByIdAndActiveTrue(id);
                if(productCategory != null){
                    productCategory.setActive(false);
                    repository.save(productCategory);
                }

            }
            return true;
        }
        return false;
    }

    @Override
    public List<ProductCategory> findByParentId(Integer parentId) {
        return repository.findByParentId(parentId);
    }

    @Override
    public boolean deleteCategory(Integer id) {
        ProductCategory productCategory = repository.findByIdAndActiveTrue(id);
        List<ProductCategory> categories = repository.findByParentId(id);
        List<Product> products = productRepository.findByCategoryId(id);
        System.out.println("CATE " +  categories);
        if(categories.isEmpty() && products.isEmpty()){
            productCategory.setActive(false);
            repository.save(productCategory);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ProductCategory create(ProductCategory entity) {
        return repository.save(entity);
    }

    @Override
    public ProductCategory retrieve(Integer id) {
        return null;
    }

    @Override
    public void update(ProductCategory entity, Integer id) {
        repository.save(entity);
    }

    @Override
    public void delete(Integer id) throws Exception {
    }


}
