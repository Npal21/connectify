package com.connectly.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.connectly.entities.User;


@Repository
public interface UserRepo extends JpaRepository<User, String>{

    Optional<User> findByEmail(String email); //Custom finder method-> JPA implements them automatically

    Optional<User> findByEmailAndPassword(String email, String password); // Syntax 'findBy+yourFieldName'

    Optional<User> findByEmailToken(String emailToken);

}
