package com.connectly.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.connectly.entities.User;
import com.connectly.helpers.AppConstants;
import com.connectly.helpers.Helper;
import com.connectly.helpers.ResourceNotFoundException;
import com.connectly.repositories.UserRepo;
import com.connectly.services.EmailService;
import com.connectly.services.UserService;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private Helper helper;

    private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());


    @Override
    public User saveUser(User user) {
        //Generate dynamic userId
        String userId = UUID.randomUUID().toString();
        user.setUserId(userId);

        //encoding password and saving in db  
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        //setting user role 
        user.setRoleList(List.of(AppConstants.ROLE_USER));    //HARDCODING

        logger.info(user.getProviderId().toString());        

        String emailToken = UUID.randomUUID().toString();
        user.setEmailToken(emailToken);
        User savedUser =  userRepo.save(user);
        String emailLink = helper.getLinkForEmailVerification(emailToken);
        emailService.sendEmail(savedUser.getEmail(), "Verify Account: Connectify", emailLink);
        return savedUser;
    }

    @Override
    public Optional<User> getUserById(String id) {
       return userRepo.findById(id);
    }

    @Override
    public Optional<User> updateUser(User user) {

       User user2 = userRepo.findById(user.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

       user2.setName(user.getName());
       user2.setEmail(user.getEmail());
       user2.setPassword(user.getPassword());
       user2.setPhoneNumber(user.getPhoneNumber());
       user2.setAbout(user.getAbout());
       user2.setProfilePic(user.getProfilePic());
       user2.setEnabled(user.isEnabled());
       user2.setEmailVerified(user.isEmailVerified());
       user2.setPhoneVerified(user.isPhoneVerified());
       user2.setProviderId(user.getProviderId());
       user2.setProviderUserId(user.getProviderUserId());
       
       User updatedUser = userRepo.save(user2);
       return Optional.ofNullable(updatedUser);
    }

    @Override
    public void deleteUser(String id) {
       
        User user = userRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not Found"));
        userRepo.delete(user);
    }

    @Override
    public boolean isUserExist(String userId) {
        User user = userRepo.findById(userId).orElse(null);       
        return (user != null) ? true : false;
    }

    @Override
    public boolean isUserExistByEmail(String email) {
       User user = userRepo.findByEmail(email).orElse(null);
       return (user != null) ? true : false;
    }

    @Override
    public List<User> getAllUsers() {
       return userRepo.findAll();
    }

    @Override
    public User getUserByEmail(String email) {
      return userRepo.findByEmail(email).orElse(null);    
    }

    @Override
    public User getUserByToken(String token) {
        return userRepo.findByEmailToken(token).orElse(null);
    }


}
