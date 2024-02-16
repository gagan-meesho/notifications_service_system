package com.assignment.notificationservice.controller.authentication;


import com.assignment.notificationservice.entity.authentication.User;
import com.assignment.notificationservice.service.authentication.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private UserService userService;
    Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/current-user")
    public Principal getCurrentUser(Principal principal) {
        return principal;
    }

}