package com.assignment.notificationservice.dao.redis;

import com.assignment.notificationservice.entity.redis.BlacklistedNumbers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BlacklistedNumbersRepository extends JpaRepository<BlacklistedNumbers, Integer> {

    @Query("SELECT bn.phoneNumber FROM BlacklistedNumbers bn")
    List<String> getAllBlacklistedPhoneNumbers();

    @Transactional
    @Modifying
    @Query("delete from BlacklistedNumbers where phoneNumber=:phoneNumber")
    void deleteSpecificPhoneNumber(@Param("phoneNumber") String phoneNumber);
}
