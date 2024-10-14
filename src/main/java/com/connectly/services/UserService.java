package com.connectly.services;

import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

import com.connectly.entities.User;

@Service
public interface UserService {

    User saveUser(User user);
    Optional<User> getUserById(String id);
    Optional<User> updateUser(User user);
    void deleteUser(String id);
    boolean isUserExist(String userId);
    boolean isUserExistByEmail(String email);
    List<User> getAllUsers();
    User getUserByEmail(String email);
    User getUserByToken(String token);

}
