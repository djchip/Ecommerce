package com.ecommerce.core.service;

import com.google.gson.Gson;
import com.ecommerce.core.entities.Categories;
import com.ecommerce.core.entities.UndoLog;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface CategoriesService  extends IRootService<Categories> {
    Categories deleteCar(Integer id) throws Exception;
    void undo(UndoLog undoLog) throws Exception;
    Categories findByDirectoryName(String name);
    boolean deleteCate(Integer[] ids, Gson g, String createdBy, HttpServletRequest request) throws Exception;
    Optional<Categories> finbyID(Integer id);

    List<Categories> getCategoriesByOrganizationId(Integer id);


}
