package com.example.ratelimiter.Controller;

import com.example.ratelimiter.Exceptions.NotFoundException;
import com.example.ratelimiter.Model.User;
import com.example.ratelimiter.Model.UserSubscription;
import com.example.ratelimiter.Service.UserService;
import com.example.ratelimiter.Service.UserSubscriptionService;
import com.example.ratelimiter.filter.CustomAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class NotificationController {
    @Autowired
    UserSubscriptionService userSubscriptionService;

    @Autowired
    UserService userService;
    @PostMapping("/notification")
    public String sendNotification(HttpServletRequest request) {
        String userName = request.getAttribute(CustomAuthorizationFilter.userName).toString();
        User getUser = userService.findUserName(userName);

        Integer timeCapacity;
        Integer monthCapacity = 100;

        if (getUser == null)
            throw new NotFoundException("user not found");

        UserSubscription subscription = userSubscriptionService.findUserSubscription(getUser);
        if(subscription != null){
            timeCapacity = subscription.getCapacity();
        }else{
            timeCapacity = 10;
        }

        System.out.printf("==userName==>"+subscription);

        return "Notification sent successfully.";
    }
}
