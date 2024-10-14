package com.connectly.config;

import com.connectly.services.impl.SecurityCustomUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {

    //user creation and login using in-memory service
    /*
    @Bean
    public UserDetailsService userDetailsService(){

        UserDetails user1 = User
                            .withDefaultPasswordEncoder()
                            .username("admin123")
                            .password("admin123")
                            .roles("ADMIN","USER")
                            .build();

        UserDetails user2 = User
                            .withDefaultPasswordEncoder()
                            .username("user123")
                            .password("password")
                            .build();

        var inMemoryUserDetailsManager = new InMemoryUserDetailsManager(user1, user2);
        return inMemoryUserDetailsManager;
    }
 */

    @Autowired
    private SecurityCustomUserDetailsService userDetailsService; 

    @Autowired
    private OAuthAuthenticationSuccessHandler handler;

    @Autowired
    private AuthFailureHandler authFailureHandler;

    //Configuration of authentication provider of spring security
    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        //need user details service's object
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);

        //need password encoder service's object
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());  
        
        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{

        //Url configurations specifying public and private accesses
        httpSecurity.authorizeHttpRequests(authorize -> {
            // authorize.requestMatchers("/home","/signup","/services").permitAll();
            authorize.requestMatchers("/user/**").authenticated();
            authorize.anyRequest().permitAll();
        });

        //Form default login
            //httpSecurity.formLogin(Customizer.withDefaults());
        httpSecurity.formLogin(formLogin -> {

            //designing and customizing own login page
            formLogin.loginPage("/login")
            .loginProcessingUrl("/authenticate")
            .successForwardUrl("/user/profile")
            .usernameParameter("email")
            .passwordParameter("password");
             // .failureForwardUrl("/login?error=true")
            /*
            .failureHandler(new AuthenticationFailureHandler() {

                @Override
                public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException exception) throws IOException, ServletException {
                   
                    throw new UnsupportedOperationException("Unimplemented method 'onAuthenticationFailure'");
                }
                
            })
            .successHandler(new AuthenticationSuccessHandler() {

                @Override
                public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                        Authentication authentication) throws IOException, ServletException {
                   
                    throw new UnsupportedOperationException("Unimplemented method 'onAuthenticationSuccess'");
                }
                
            })
             */    
            //;

            formLogin.failureHandler(authFailureHandler);
        });

        //Disabling CSRF so that get method can be fired for logout using '/logout'; else must fire POST method for logout
        httpSecurity.csrf(AbstractHttpConfigurer :: disable); 

        // Oauth configurations
            //httpSecurity.oauth2Login(Customizer.withDefaults());
        httpSecurity.oauth2Login(oauth -> {
            oauth.loginPage("/login");
            oauth.successHandler(handler);

        });

        httpSecurity.logout(formLogout -> {
            formLogout.logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout=true");
        });

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
