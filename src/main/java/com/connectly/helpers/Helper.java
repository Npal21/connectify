package com.connectly.helpers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class Helper {

    @Value("${server.baseUrl}")
    private String baseUrl;

    public static String getEmailOfLoggedInUser(Authentication authentication){

        if(authentication instanceof OAuth2AuthenticationToken){

            var oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
            var clientId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();

            var oauth2User = (OAuth2User)authentication.getPrincipal();
            String username = "";

            if(clientId.equalsIgnoreCase("google")){

                //google
                System.out.println("Getting email from client Id Google..");
                username = oauth2User.getAttribute("email").toString();
            }
            else if(clientId.equalsIgnoreCase("github")){

                //github
                System.out.println("Getting email from client Id github..");
                username = oauth2User.getAttribute("email") != null ? 
                               oauth2User.getAttribute("email").toString() : 
                               oauth2User.getAttribute("login").toString() + "@gmail.com";
            }

            return username;
        }

        else{
            System.out.println("Getting data from local database.");
            return authentication.getName();
        }

    }


    public String getLinkForEmailVerification(String emailToken){

        return this.baseUrl+"/auth/verify-email?token="+emailToken;
    }


}
