package com.assignment.notificationservice.controller.authentication.user;


import com.assignment.notificationservice.entity.authentication.User;
import com.assignment.notificationservice.service.authentication.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/v1/home")
public class HomeController {
    @Autowired
    private UserService userService;
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    @GetMapping("/current-user")
    public Principal getCurrentUser(Principal principal) {
        return principal;
    }
}