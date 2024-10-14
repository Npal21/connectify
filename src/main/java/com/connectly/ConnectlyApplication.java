package com.connectly;

import com.connectly.entities.User;
import com.connectly.helpers.AppConstants;
import com.connectly.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@EnableJpaRepositories
@SpringBootApplication
public class ConnectlyApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ConnectlyApplication.class, args);
	}

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {

		User user = new User();
		user.setUserId(UUID.randomUUID().toString());
		user.setName("admin");
		user.setEmail("admin@gmail.com");
		user.setPassword(passwordEncoder.encode("admin"));
		user.setRoleList(List.of(AppConstants.ROLE_USER));
		user.setEmailVerified(true);
		user.setEnabled(true);
		user.setAbout("This is admin user.");
		user.setPhoneVerified(true);

		userRepo.findByEmail("admin@gmail.com").ifPresentOrElse(user1 -> {}, () -> {
			userRepo.save(user);
			System.out.println("User Created");
		});
	}
}
