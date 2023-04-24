package com.ecommerce.core.repositories;

import com.ecommerce.core.entities.StandardUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StandardUserRepository extends JpaRepository<StandardUser, Integer> {
    @Query(value = "SELECT s FROM StandardUser s WHERE s.standardId = ?1 AND s.userId = ?2")
    StandardUser checkExisted(int staId, int userId);

    @Query(value = "SELECT s.standardId FROM StandardUser s JOIN UserInfo u ON s.userId = u.id WHERE u.username = ?1")
    List<Integer> getListStandardIdByUsername(String username);
}
