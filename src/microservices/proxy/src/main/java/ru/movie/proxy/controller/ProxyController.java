package ru.movie.proxy.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@RestController
public class ProxyController {

    @Value("${GRADUAL_MIGRATION}")
    private String gradualMigration;

    @Value("${MOVIES_MIGRATION_PERCENT}")
    private int moviesMigrationPercent;

    @Value("${MONOLITH_URL}")
    private String monolithUrl;

    @Value("${MOVIES_SERVICE_URL}")
    private String moviesServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/api/movies")
    public String getMovies(@RequestParam(required = false) String user,
                            @RequestParam(required = false) String id) {
        if (gradualMigration.equals("true")) {
            if (Math.random() * 100 < moviesMigrationPercent) {
                String url = moviesServiceUrl + "/api/movies";
                if (id != null) {
                    url += "?id=" + id;
                }
                return restTemplate.getForObject(url, String.class);
            } else {
                String url = monolithUrl + "/api/movies";
                if (id != null) {
                    url += "?id=" + id;
                }
                return restTemplate.getForObject(url, String.class);
            }
        } else {
            String url = monolithUrl + "/api/movies";
            if (id != null) {
                url += "?id=" + id;
            }
            return restTemplate.getForObject(url, String.class);
        }
    }

    @PostMapping("/api/movies")
    public ResponseEntity<String> createMovie(@RequestBody String movieJson) {
        if (gradualMigration.equals("true")) {
            if (Math.random() * 100 < moviesMigrationPercent) {
                // Отправляем в movies-service
                String response = restTemplate.postForObject(moviesServiceUrl + "/api/movies", movieJson, String.class);
                return ResponseEntity.status(201).body(response);
            } else {
                // Отправляем в монолит
                String response = restTemplate.postForObject(monolithUrl + "/api/movies", movieJson, String.class);
                return ResponseEntity.status(201).body(response);
            }
        } else {
            String response = restTemplate.postForObject(monolithUrl + "/api/movies", movieJson, String.class);
            return ResponseEntity.status(201).body(response);
        }
    }

    @GetMapping("/health")
    public String health() {
        return restTemplate.getForObject(monolithUrl + "/health", String.class);
    }

    @GetMapping("/api/users")
    public String getUsers() {
        return restTemplate.getForObject(monolithUrl + "/api/users", String.class);
    }

    @PostMapping("/api/users")
    public ResponseEntity<String> createUser(@RequestBody String userJson) {
        ResponseEntity<String> response = restTemplate.postForEntity(monolithUrl + "/api/users", userJson, String.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
}