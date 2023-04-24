package com.ecommerce.core.service.impl;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.exceptions.CategoryBeingUseException;
import com.ecommerce.core.exceptions.ExistsCategoryException;
import com.ecommerce.core.repositories.CategoriesRepository;
import com.ecommerce.core.service.CategoriesService;
import com.ecommerce.core.service.UndoLogService;
import com.google.gson.Gson;
import com.ecommerce.core.entities.Categories;
import com.ecommerce.core.entities.Organization;
import com.ecommerce.core.entities.UndoLog;
import com.ecommerce.core.repositories.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriesServiceImpl implements CategoriesService {
    private static final Integer DELETED = 1;
    private static final Integer UNDO_DELETE = 0;
    private static final String TABLE_NAME = "categories";
    private static final int FIRST_INDEX = 0;
    private static final Integer UN_ACTIVE_UNDO_LOG = 1;
    @Autowired
    UndoLogService undoLogService;
    @Autowired
    CategoriesRepository categoriesRepository;
    @Autowired
    OrganizationRepository organizationRepository;

    @Override
    public Categories deleteCar(Integer id) throws Exception {
        Optional<Categories> optional = categoriesRepository.findById(id);
        if (optional.isPresent()) {
            List<Organization> organizations = organizationRepository.findByCategoryIdAndDeletedNot(id, 1);
            if (organizations.size() > 0) {
                return null;
            }
            Categories userInfo = optional.get();
            userInfo.setDelete(DELETED);
            userInfo.setUndoStatus(ConstantDefine.STATUS.CAN_BE_UNDO);
            return categoriesRepository.save(userInfo);
        } else {
            throw new Exception();
        }
    }

    private Categories undoPut(UndoLog undoLog) {
        Gson g = new Gson();
//        Directory userInfo = g.fromJson(undoLog.getRevertObject(), Directory.class);
//        userInfo.setUndoStatus(ConstantDefine.STATUS.CAN_NOT_UNDO);
//        directoryRepository.save(userInfo);

        return g.fromJson(undoLog.getRevertObject(), Categories.class);
    }

    @Transactional
    @Override
    public void undo(UndoLog undoLog) throws Exception {
        List<UndoLog> undoLogs = undoLogService.findByTableNameAndIdRecordAndStatusNotOrderByCreatedDateDesc(TABLE_NAME, undoLog.getIdRecord(), 1);
        for (UndoLog log : undoLogs) {
            if (log.getId().equals(undoLog.getId())) {
                Optional<Categories> optional = categoriesRepository.findById(log.getIdRecord());
                Categories categories;
                if (optional.isPresent()) {
                    categories = optional.get();
                } else {
                    throw new Exception();
                }
                switch (log.getAction()) {
                    case "POST":
                        if (existsByCategoryIdAndDeleted(categories.getId(), 0)) {
                            throw new CategoryBeingUseException("Category being use");
                        }
                        categoriesRepository.deleteById(categories.getId());
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                    case "DELETE":
                        if (!categoriesRepository.findByNameAndDelete(categories.getName(), 0).isEmpty()) {
                            throw new ExistsCategoryException("Tên chuyên mục đã tồn tại");
                        } else {
                            categories.setDelete(UNDO_DELETE);
                            log.setStatus(UN_ACTIVE_UNDO_LOG);
                            undoLogService.update(log, log.getId());
                            break;
                        }
                    case "PUT":
                        categories = undoPut(log);
                        log.setStatus(UN_ACTIVE_UNDO_LOG);
                        undoLogService.update(log, log.getId());
                        break;
                }
                if (!undoLogService.existsByTableNameAndIdRecord(TABLE_NAME, categories.getId()) && !log.getAction().equals("POST")) {
                    categories.setUndoStatus(ConstantDefine.STATUS.CAN_NOT_UNDO);
                }
                if (!log.getAction().equals("POST")) {
                    categoriesRepository.save(categories);
                }
                break;
            } else {
                log.setStatus(UN_ACTIVE_UNDO_LOG);
                undoLogService.update(log, log.getId());
            }
        }
    }

    @Override
    public Categories findByDirectoryName(String name) {
        Optional<Categories> entity = categoriesRepository.findByNameAndDeleteNot(name, ConstantDefine.IS_DELETED.TRUE);
        return entity.orElse(null);
    }

    @Override
    public boolean deleteCate(Integer[] ids, Gson g, String createdBy, HttpServletRequest request) throws Exception {
     for (Integer id:ids){
         Categories entity=deleteCar(id);
         if(entity == null){
             return  false;
         }
         UndoLog undoLog = UndoLog.undoLogBuilder()
                 .action(request.getMethod())
                 .requestObject(g.toJson(entity, Categories.class))
                 .status(ConstantDefine.STATUS.UNDO_NEW)
                 .url(request.getRequestURL().toString())
                 .description("Xóa chuyên mục "+entity.getName()+" bởi " + createdBy)
                 .createdBy(createdBy)
                 .createdDate(LocalDateTime.now())
                 .tableName(TABLE_NAME)
                 .idRecord(entity.getId())
                 .build();
         undoLogService.create(undoLog);
     }
        return true;
    }

    @Override
    public Optional<Categories> finbyID(Integer id) {
        return categoriesRepository.finbyId(id);
    }

    @Override
    public List<Categories> getCategoriesByOrganizationId(Integer id) {
        return categoriesRepository.getCategoriesByOrganizationId(id);
    }


    @Override
    public Categories create(Categories entity) {
        return categoriesRepository.save(entity);
    }

    @Override
    public Categories retrieve(Integer id) {
        Optional<Categories> entity = categoriesRepository.findById(id);
        return entity.orElse(null);
    }

    @Override
    public void update(Categories entity, Integer id) {
        categoriesRepository.save(entity);
    }

    @Override
    public void delete(Integer id) throws Exception {

    }

    private boolean existsByCategoryIdAndDeleted(Integer categoryId, Integer deleted) {
        return organizationRepository.existsByCategoryIdAndDeleted(categoryId, deleted);
    }
}
