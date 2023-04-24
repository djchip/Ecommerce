package com.ecommerce.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ecommerce.core.entities.SoftwareVersion;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface SoftwareVersionRepository extends JpaRepository<SoftwareVersion, Integer> {
    @Query(value = "Select f from SoftwareVersion f where f.version like %?1%")
    public Optional<SoftwareVersion> findBySVersion(String version);
    @Modifying
    @Transactional
    @Query(value = "Update SoftwareVersion s SET s.lastestVersion = 0 where s.id < ?1")
    void updateLastestVersion(Integer id);
    @Query(value = "SELECT v FROM SoftwareVersion v where v.lastestVersion = 1")
    SoftwareVersion getLastestVersion ();

}
