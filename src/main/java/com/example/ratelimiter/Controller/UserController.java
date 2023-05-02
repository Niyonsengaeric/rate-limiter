package com.example.ratelimiter.Controller;

import com.example.ratelimiter.Exceptions.NotFoundException;
import com.example.ratelimiter.Model.User;
import com.example.ratelimiter.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

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

    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletRequest request) {
        String userName = request.getAttribute("userName").toString();
        ObjectMapper oMapper = new ObjectMapper();
        Map<String, Object> tokens = oMapper.convertValue(request.getAttribute("data"), Map.class);
        Map<String, Object> data = new HashMap<>();
        for (Map.Entry<String, Object> token : tokens.entrySet()) {
            data.put(token.getKey(), token.getValue().toString());
        }
        return ResponseEntity.ok(data);
    }
}
