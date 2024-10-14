package com.connectly.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.connectly.entities.User;
import com.connectly.helpers.Helper;
import com.connectly.services.UserService;

@ControllerAdvice
public class RootController {

    
    private Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    public RootController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public void addLoggedInUserInformation(Model model, Authentication authentication){

        if(authentication == null){
            return;
        }
        
        System.out.println("Adding logged in user info to the model");
        String userName = Helper.getEmailOfLoggedInUser(authentication); 
        logger.info("User logged in: {}", userName);
        //Fetching data from db 
        User user = userService.getUserByEmail(userName);        
        System.out.println(user);
        System.out.println(user.getName());
        System.out.println(user.getEmail());
        model.addAttribute("loggedInUser", user);
        
       
    }


}
