package com.krish.AgariBackend;

import com.krish.AgariBackend.entity.Role;
import com.krish.AgariBackend.entity.User;
import com.krish.AgariBackend.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class AgariBackendApplication {

	public static void main(String[] args) {
		// Try loading .env from current directory or subdirectory
		String[] paths = { ".", "./AgariBackend" };
		boolean loaded = false;

		for (String path : paths) {
			java.io.File envFile = new java.io.File(path, ".env");
			if (envFile.exists()) {
				System.out.println("Found .env at: " + envFile.getAbsolutePath());
				io.github.cdimascio.dotenv.Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.configure()
						.directory(path)
						.ignoreIfMissing()
						.load();
				dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
				loaded = true;
				break;
			}
		}

		if (!loaded) {
			System.out.println("⚠️ WARNING: Could not find .env file. Environment variables might not be set.");
		}

		SpringApplication.run(AgariBackendApplication.class, args);
		System.out.println("Server Running Successfully..!");
	}

	@Bean
	CommandLineRunner init(UserRepository userRepo, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepo.findByEmail("admin@agari.com").isEmpty()) {
				User admin = new User();
				admin.setUsername("Super Admin");
				admin.setEmail("admin@agari.com");
				admin.setPassword(passwordEncoder.encode("admin123"));
				admin.setEnabled(true);

				Set<Role> roles = new HashSet<>();
				roles.add(Role.ROLE_ADMIN);
				roles.add(Role.ROLE_USER);
				admin.setRoles(roles);

				userRepo.save(admin);
				System.out.println("✅ Default Admin User Created: admin@agari.com / admin123");
			}
		};
	}

}
