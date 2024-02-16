package com.assignment.notificationservice.dao.sql;


import com.assignment.notificationservice.entity.sql.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsRepository extends JpaRepository<Request, Integer> {

}
