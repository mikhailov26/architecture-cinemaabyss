package ru.movie.proxy;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Enumeration;

@RestController
public class ProxyController {

    private static final Logger log = LoggerFactory.getLogger(ProxyController.class);
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${monolith.url:http://localhost:8080}")
    private String monolithUrl;

    @Value("${movies.service.url:http://localhost:8081}")
    private String moviesServiceUrl;

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("{\"status\":\"OK\"}");
    }

    @RequestMapping("/api/movies/**")
    public ResponseEntity<byte[]> proxyMovies(HttpServletRequest request) throws IOException {
        return forward(request, moviesServiceUrl);
    }

    @RequestMapping("/api/users/**")
    public ResponseEntity<byte[]> proxyUsers(HttpServletRequest request) throws IOException {
        return forward(request, monolithUrl);
    }

    private ResponseEntity<byte[]> forward(HttpServletRequest request, String url) throws IOException {
        String query = request.getQueryString();
        String fullUrl = url + request.getRequestURI() + (query != null ? "?" + query : "");

        log.info("Forwarding {} to {}", request.getMethod(), fullUrl);

        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> names = request.getHeaderNames();
        if (names != null) {
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                headers.add(name, request.getHeader(name));
            }
        }

        byte[] body = request.getInputStream().readAllBytes();
        HttpEntity<byte[]> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    fullUrl,
                    HttpMethod.valueOf(request.getMethod()),
                    entity,
                    byte[].class
            );
            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        } catch (Exception e) {
            log.error("Proxy error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Proxy error\"}".getBytes());
        }
    }
}