package com.assignment.notificationservice.service.authentication;

import com.assignment.notificationservice.entity.authentication.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private List<User> store=new ArrayList<>();

    public UserService() {
        store.add(new User("1","gagan1","gagan1@gmail.com"));
        store.add(new User("2","gagan2","gagan2@gmail.com"));
        store.add(new User("3","gagan3","gagan3@gmail.com"));
    }
    public List<User> getAllUsers(){
        return store;
    }
}
