package com.ecommerce.core.service.impl;

import com.ecommerce.core.dto.DataProductDTO;
import com.ecommerce.core.dto.ProductDTO;
import com.ecommerce.core.entities.Product;
import com.ecommerce.core.entities.ProductCategory;
import com.ecommerce.core.repositories.ProductRepository;
import com.ecommerce.core.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository repository;

    @Override
    public Product create(Product entity) {
        String inputName = entity.getName().trim();
        Product checkExisted = repository.findByName(inputName);
        if (checkExisted != null){
            return null;
        } else {
            String normalized = Normalizer.normalize(inputName, Normalizer.Form.NFD);
            // Loại bỏ các ký tự không phải ASCII và chuyển thành chữ thường
            String slug = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                    .replaceAll("[^\\p{ASCII}]", "").toLowerCase();
            entity.setSlug(slug.replaceAll(" ", "-"));
            entity.setSold(0);
            entity.setActive(true);
            return repository.save(entity);
        }
    }

    @Override
    public Product retrieve(Integer id) {
        Optional<Product> product = repository.findById(id);
        return product.orElse(null);
    }

    @Override
    public void update(Product entity, Integer id) {
    }

    @Override
    public void delete(Integer id) throws Exception {

    }

    @Override
    public Page<ProductDTO> doSearch(String keyword, Integer categoryId, Pageable paging) {
        return repository.doSearch(keyword, categoryId, paging);
    }

    @Override
    public Integer deleteProduct(Integer id) {
        Optional<Product> product = repository.findById(id);
        if(product.isPresent()){
            if (product.get().isActive()){
                product.get().setActive(false);
                repository.save(product.get());
                return 1;
            } else {
                product.get().setActive(true);
                repository.save(product.get());
                return 2;
            }
        }
        return null;
    }

    @Override
    public Product doUpdate(Product entity, Integer id, String updateBy) {
        Optional<Product> optional = repository.findById(id);
        Product product = optional.orElse(null);
        if(product != null){
            String inputName = entity.getName();
            Product checkExistedByName = null;
            if (!inputName.equalsIgnoreCase(product.getName())) {
                checkExistedByName = repository.findByName(inputName);
                if (checkExistedByName != null && !checkExistedByName.getName().equalsIgnoreCase(product.getName())){
                    return null;
                }
            }
            entity.setCreatedBy(product.getCreatedBy());
            entity.setActive(product.isActive());
            entity.setSold(product.getSold());
            entity.setUpdatedBy(updateBy);
            entity.setUpdatedDate(LocalDateTime.now());
            return repository.save(entity);
        }
        return null;
    }

    @Override
    public List<DataProductDTO> getListProduct() {
        return repository.getListProduct();
    }
}
