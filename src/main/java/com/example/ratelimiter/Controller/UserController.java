package com.example.ratelimiter.Controller;

import com.example.ratelimiter.Model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class UserController {


    @PostMapping("/register")
    public ResponseEntity<?> addUser(@Valid @RequestBody User user) {
        return new ResponseEntity("addNewUser", HttpStatus.CREATED);
    }
}
