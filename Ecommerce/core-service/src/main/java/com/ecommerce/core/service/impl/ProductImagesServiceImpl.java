package com.ecommerce.core.service.impl;

import com.ecommerce.core.entities.ProductImages;
import com.ecommerce.core.repositories.ProductImagesRepository;
import com.ecommerce.core.service.ProductImagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductImagesServiceImpl implements ProductImagesService {

    @Autowired
    private ProductImagesRepository repository;

    @Override
    public ProductImages create(ProductImages entity) {
        return repository.save(entity);
    }
}
