package com.ecommerce.core.service;

import com.ecommerce.core.entities.ObjectDetail;

import java.util.List;

public interface ObjectDetailService extends IRootService<ObjectDetail>{

    List<ObjectDetail> getListByObj(Integer obj);

    String getMaxColByObj(Integer obj);
}
