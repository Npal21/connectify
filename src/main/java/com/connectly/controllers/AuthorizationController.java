package com.connectly.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.connectly.entities.User;
import com.connectly.helpers.Message;
import com.connectly.helpers.MessageType;
import com.connectly.services.UserService;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/auth")
public class AuthorizationController {

    @Autowired
    private UserService userService;

    //Verify email
    @GetMapping("/verify-email")
    public String verifyEmail(
        @RequestParam("token") String token,
        HttpSession session){

        User user = userService.getUserByToken(token);

        if(user != null){

            if(user.getEmailToken().equals(token)){
                user.setEmailVerified(true);
                user.setEnabled(true);
                userService.updateUser(user);
                //userRepo.save(user);
                session.setAttribute("message", Message.builder()
                .type(MessageType.green)
                .content("Your Email is verified ! You can Login now.")
                .build());
                return "success_page";
            }

            session.setAttribute("message", Message.builder()
            .type(MessageType.red)
            .content("Email not verified ! Token is not associated with user.")
            .build());
            return "error_page";
            
        }

        session.setAttribute("message", Message.builder()
            .type(MessageType.red)
            .content("Email not verified! Token is not associated with user.")
            .build());
        return "error_page";    
    }

}


