package com.connectly.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.connectly.entities.User;
import com.connectly.forms.UserForm;
import com.connectly.helpers.Message;
import com.connectly.helpers.MessageType;
import com.connectly.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@Controller
public class PageController {

    private UserService userService;
    public PageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index(){
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model){
        //System.out.println("Home page handler");
        model.addAttribute("githubRepo", "https://github.com/Npal21");
        return "home";
    }

    @GetMapping("/about")
    public String getAboutPage(Model model) {
        //System.out.println("Loading about page....");
        model.addAttribute("isLogin", true);
        return "about";
    }
    
    @GetMapping("/services")
    public String getMethodName() {
        //System.out.println("Loading services page....");
        return "services";
    }
    
    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        UserForm userForm = new UserForm();
        // userForm.setName("Nidhi");
        model.addAttribute("userForm", userForm);
        return "signup";
    }
    
    @PostMapping("/do-signup")
    public String processSignup(@Valid @ModelAttribute UserForm userForm,BindingResult rBindingResult, HttpSession session) {
        //Fetch form data 

        //UserForm 
        //System.out.println("Processing signup");
        //System.out.println(userForm);

        //Validate form data
        if(rBindingResult.hasErrors())
            return "signup";

        //save to db: userForm->user
        User user = new User();
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());
        user.setPhoneNumber(userForm.getPhoneNumber());
        user.setAbout(userForm.getAbout());  
        user.setEnabled(false);
        user.setProfilePic("https://static.vecteezy.com/system/resources/thumbnails/020/765/399/small/default-profile-account-unknown-icon-black-silhouette-free-vector.jpg");         
                  
        userService.saveUser(user);

        //message "signup successful"
        System.out.println("User saved");

        Message message = Message.builder().content("Registration successful").type(MessageType.green).build();
        session.setAttribute("message", message);

        //redirect to /signup        
        return "redirect:/signup";
    }
    




}
