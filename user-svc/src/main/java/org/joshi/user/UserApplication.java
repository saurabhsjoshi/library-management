package org.joshi.user;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@Slf4j
@SpringBootApplication
public class UserApplication {

    private final UserRepository repository;

    @Autowired
    public UserApplication(UserRepository repository) {
        this.repository = repository;
    }

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void setupAdmin() {
        // Create admin user
        var admin = new LibraryUser();
        admin.setUsername("admin");
        admin.setPassword("admin");
        repository.save(admin);

        log.info("Created admin user.");
    }
}
