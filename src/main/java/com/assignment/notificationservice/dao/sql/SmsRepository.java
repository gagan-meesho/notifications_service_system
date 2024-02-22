package com.assignment.notificationservice.dao.sql;


import com.assignment.notificationservice.entity.sql.Sms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsRepository extends JpaRepository<Sms, Integer> {

}
