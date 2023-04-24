package com.ecommerce.core.repositories;

import com.ecommerce.core.entities.ObjectDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjectDetailRepository extends JpaRepository<ObjectDetail, Integer> {

    List<ObjectDetail> findByObjAndStatusNot(Integer obj, Integer status);

    @Query(value = "SELECT ifnull(max(col), 'col_0') FROM object_detail where obj = ?1", nativeQuery = true)
    String getMaxColByObj(Integer obj);
}
