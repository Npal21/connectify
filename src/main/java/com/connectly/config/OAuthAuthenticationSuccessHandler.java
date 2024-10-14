package com.connectly.config;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.connectly.entities.Providers;
import com.connectly.entities.User;
import com.connectly.helpers.AppConstants;
import com.connectly.repositories.UserRepo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler{
    
    Logger logger = LoggerFactory.getLogger(OAuthAuthenticationSuccessHandler.class);

    @Autowired
    private UserRepo userRepo;
    
    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request, 
        HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        
                logger.info("***********OAuthAuthenticationSuccessHandler***********");

                //Identifying the provider
                var oAuth2AuthenticationToken = (OAuth2AuthenticationToken)authentication;
                String authorizedClientRegistrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
                logger.info(authorizedClientRegistrationId);

                var oauthUser = (DefaultOAuth2User)authentication.getPrincipal();

                User user = new User();
                user.setUserId(UUID.randomUUID().toString());
                user.setEmailVerified(true);
                user.setEnabled(true);
                user.setRoleList(List.of(AppConstants.ROLE_USER));
                user.setPassword("dummy");


                if(authorizedClientRegistrationId.equalsIgnoreCase("google")){
                    //google
                    user.setEmail(oauthUser.getAttribute("email").toString());
                    user.setProfilePic(oauthUser.getAttribute("picture").toString());
                    user.setName(oauthUser.getAttribute("name").toString());
                    user.setProviderUserId(oAuth2AuthenticationToken.getName());
                    user.setProviderId(Providers.GOOGLE);
                    user.setAbout("This account is created using Google");
                }
                else if(authorizedClientRegistrationId.equalsIgnoreCase("github")){ 
                    //github 
                    String email = oauthUser.getAttribute("email") != null ? 
                                   oauthUser.getAttribute("email").toString() : 
                                   oauthUser.getAttribute("login").toString() + "@gmail.com";
                    String picture = oauthUser.getAttribute("avatar_url").toString();
                    String name = oauthUser.getAttribute("login").toString();
                    String providerUserId = oauthUser.getName();

                    user.setEmail(email);
                    user.setProfilePic(picture);
                    user.setName(name);
                    user.setProviderUserId(providerUserId);
                    user.setProviderId(Providers.GITHUB);
                    user.setAbout("This account is created using Github");
                    
                }
                else if(authorizedClientRegistrationId.equalsIgnoreCase("linkedin")){
                    //linkedin
                }
                else{
                    logger.info("OAuthAuthenticationSuccessHandler : Unknown provider");
                }

/* 
                //Getting user details from authentication object
                DefaultOAuth2User user = (DefaultOAuth2User)authentication.getPrincipal();

                
                        //logger.info(user.getName());
                        //user.getAttributes().forEach((key, value)->{
                            //logger.info("{} -> {}", key, value );
                        //});
                        //logger.info(user.getAuthorities().toString());
               

                //Extracting data from provider object
                String email = user.getAttribute("email").toString();
                String name = user.getAttribute("name").toString();
                String picture = user.getAttribute("picture").toString();
                
                //Create user and save in database
                User user1 = new User();
                user1.setEmail(email);
                user1.setName(name);
                user1.setProfilePic(picture);
                user1.setPassword("password");
                user1.setUserId(UUID.randomUUID().toString());
                user1.setAbout("This account is created using google.");
                user1.setEmailVerified(true);
                user1.setEnabled(true);
                user1.setProviderId(Providers.GOOGLE);
                user1.setProviderUserId(user.getName());
                user1.setRoleList(List.of(AppConstants.ROLE_USER));

                User user2 = userRepo.findByEmail(email).orElse(null);
                //insert into db only when user doesn't exist in it
                if(user2 == null){  
                    userRepo.save(user1);
                    logger.info("**************************User saved in database: "+ email);
                }
  */

                //Save the user in database
                User user2 = userRepo.findByEmail(user.getEmail()).orElse(null);
                //insert into db only when user doesn't exist in it
                if(user2 == null){  
                    userRepo.save(user);
                    logger.info("User saved in database: "+ user.getEmail());
                }
                
                //response.sendRedirect("/home");
                new DefaultRedirectStrategy().sendRedirect(request, response, "/user/profile");
                
    }

}
