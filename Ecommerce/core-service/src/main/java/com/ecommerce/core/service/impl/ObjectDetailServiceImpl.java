package com.ecommerce.core.service.impl;

import com.ecommerce.core.repositories.ObjectDetailRepository;
import com.ecommerce.core.service.ObjectDetailService;
import com.ecommerce.core.entities.ObjectDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ObjectDetailServiceImpl implements ObjectDetailService {

    @Autowired
    ObjectDetailRepository repo;

    @Override
    public ObjectDetail create(ObjectDetail entity) {
        return repo.save(entity);
    }

    @Override
    public ObjectDetail retrieve(Integer id) {
        Optional<ObjectDetail> entity = repo.findById(id);
        if (!entity.isPresent()){
            return null;
        }
        return entity.get();
    }

    @Override
    public void update(ObjectDetail entity, Integer id) {
        repo.save(entity);
    }

    @Override
    public void delete(Integer id) throws Exception {
        Optional<ObjectDetail> entity = repo.findById(id);
        if (entity.isPresent()){
            ObjectDetail objectDetail = entity.get();
            objectDetail.setStatus(0);
            repo.save(objectDetail);
        } else {
            throw new Exception();
        }
    }

    @Override
    public List<ObjectDetail> getListByObj(Integer obj) {
        return repo.findByObjAndStatusNot(obj, 0);
    }

    @Override
    public String getMaxColByObj(Integer obj) {
        return repo.getMaxColByObj(obj);
    }
}
