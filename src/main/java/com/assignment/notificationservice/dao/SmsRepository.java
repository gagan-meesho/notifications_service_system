package com.assignment.notificationservice.dao;


import com.assignment.notificationservice.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsRepository extends JpaRepository<Request,Integer> {

}
