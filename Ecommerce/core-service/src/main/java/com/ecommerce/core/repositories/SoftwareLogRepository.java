package com.ecommerce.core.repositories;

import com.ecommerce.core.entities.SoftwareLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SoftwareLogRepository extends JpaRepository<SoftwareLog, Integer> {
    @Query(value = "SELECT u FROM SoftwareLog u WHERE (?1 IS NULL OR lower(u.error) like %?1% OR lower(u.amendingcontent) like %?1%) AND (?2 IS NULL OR u.status = ?2) AND (?3 is null or u.errorlogtime >= ?3) AND (?4 is null or u.errorlogtime < ?4) and u.deleted != 1 ORDER BY u.errorlogtime desc , u.successfulrevisiontime desc")
    Page<SoftwareLog> doSearch(String keyword, Integer status, LocalDateTime startDate, LocalDateTime endDate, Pageable paging);

    @Query(value = "Select f from SoftwareLog f where f.error like %?1% AND f.deleted != 1")
    public Optional<SoftwareLog> findByError(String error, Integer aTrue);

    @Query(value = "Select f from SoftwareLog f where f.version = ?1 AND f.deleted != 1")
    public List<SoftwareLog> findByVersion(String version);

    Boolean existsByAmendingcontentAndErrorAndDeletedIsNot (String error, String amedingcontent, Integer deleted);


}
