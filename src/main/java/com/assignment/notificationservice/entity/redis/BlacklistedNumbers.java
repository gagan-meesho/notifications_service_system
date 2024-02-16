package com.assignment.notificationservice.entity.redis;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Value;

@Entity
@Table(name = "blacklisted_numbers")
public class BlacklistedNumbers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Size(min = 7, max = 10, message = "please enter a valid phone number")
    @Column(name = "phone_number")
    private String phoneNumber;

    public BlacklistedNumbers() {
    }

    public BlacklistedNumbers(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
