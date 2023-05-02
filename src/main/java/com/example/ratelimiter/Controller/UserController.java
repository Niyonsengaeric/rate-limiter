package com.example.ratelimiter.Controller;

import com.example.ratelimiter.Exceptions.NotFoundException;
import com.example.ratelimiter.Model.User;
import com.example.ratelimiter.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> addUser(@Valid @RequestBody User user) {
        if (userService.existsByUserNameOrEmail(user.getUserName(), user.getEmail()))
            throw new NotFoundException("userName or email already used");
        return new ResponseEntity<>(userService.addUser(user), HttpStatus.CREATED);
    }
}
