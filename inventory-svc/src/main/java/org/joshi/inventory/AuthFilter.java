package org.joshi.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;

/**
 * Filters incoming requests to ensure certain actions cannot be performed by users that are not admins.
 */
@Component
@Order(1)
@Slf4j
public class AuthFilter implements Filter {

    private static final ObjectMapper mapper = new ObjectMapper();

    private record AuthReq(String username, String password) {

    }

    @Value("${user-svc-uri:http://user-svc:8080}")
    private String userSvcUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public AuthFilter(RestTemplateBuilder builder) {
        restTemplate = builder
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .errorHandler(new DefaultResponseErrorHandler() {
                    @Override
                    public void handleError(ClientHttpResponse response) {
                        // Don't throw
                    }
                })
                .build();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        var httpReq = (HttpServletRequest) request;

        // If it is not post or delete continue
        if (!httpReq.getMethod().equals("POST") && !httpReq.getMethod().equals("DELETE")) {
            chain.doFilter(request, response);
            return;
        }

        // Authenticate
        String username = httpReq.getHeader("username");
        String password = httpReq.getHeader("password");

        var httpResp = (HttpServletResponse) response;

        if (!StringUtils.hasText(username) && !StringUtils.hasText(password)) {
            httpResp.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        var authReq = mapper.writeValueAsString(new AuthReq(username, password));

        var authResp = restTemplate.postForEntity(userSvcUrl + "/api/auth", authReq, Void.class);

        if (authResp.getStatusCode() != HttpStatus.OK) {
            httpResp.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        chain.doFilter(request, response);
    }
}
