package org.joshi.user;

import com.fpts.api.filter.FilterBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;

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
        admin.setDisplayName("AdminUser");
        repository.save(admin);

        log.info("Created admin user.");
    }

    @Bean
    @Autowired
    public FilterRegistrationBean performanceTestFilter(ApplicationContext context,
                                                        @Qualifier("requestMappingHandlerMapping")
                                                        RequestMappingHandlerMapping requestMappingHandler) {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(FilterBuilder.buildPerformanceFilter(requestMappingHandler, context));
        List<String> urlPatterns = new ArrayList<>();
        urlPatterns.add("/*");
        filterRegistration.setUrlPatterns(urlPatterns);
        return filterRegistration;
    }
}
