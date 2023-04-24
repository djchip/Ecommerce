package com.ecommerce.core.repositories;

import com.ecommerce.core.entities.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Integer> {

    @Query(value = "SELECT u FROM Unit u WHERE (?2 IS NULL OR u.classify = ?2) AND (?1 IS NULL OR lower(u.unitName) like %?1% OR lower(u.unitNameEn) like %?1% OR lower(u.unitCode) like %?1%) and u.deleted !=1 ORDER BY u.updatedDate desc")
    Page<Unit> doSearch(String keyword, Integer classify, Pageable paging);

    @Query(value = "SELECT u FROM Unit u WHERE 1=1 AND u.deleted <> 1")
    List<Unit> getListUnits();

//    @EntityGraph(attributePaths = {Unit.Fields.unitName})
    public Optional<Unit> findByUnitNameAndDeletedNot(String unitName, Integer deleted);

    @Query(value = "SELECT u FROM Unit u where u.deleted <> 1 AND u.email is not null")
    List<Unit> getListMailTo();

//    @EntityGraph(attributePaths = {Unit.Fields.unitCode})
    Optional<Unit> findByUnitCodeAndDeletedNot(String unitCode, Integer deleted);
    
    @Query(value = "SELECT u FROM Unit u WHERE u.deleted = 0")
    List<Unit> getListActiveUnits();

    @Query(value = "SELECT u FROM Unit u JOIN UserInfo ui ON u.id = ui.unit.id WHERE u.deleted <> 1 AND 1=1 AND ui.username = ?1")
    Unit getUnitByUsername(String userName);

    List<Unit> findByUnitNameAndDeleted(String name, Integer deleted);

    @Query(value = "SELECT u FROM Unit u  WHERE u.deleted != 1 and u.unitCode = ?1")
    List<Unit> findByUnitNamee(String name);

}
