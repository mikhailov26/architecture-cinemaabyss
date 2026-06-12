package ru.movie.proxy.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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
    public String getMovies(@RequestParam(required = false) String user) {
        if (gradualMigration.equals("true")) {
            // Переключаем трафик на новый сервис, используя Feature Flag
            if (Math.random() * 100 < moviesMigrationPercent) {
                return restTemplate.getForObject(moviesServiceUrl + "/api/movies", String.class);
            } else {
                return restTemplate.getForObject(monolithUrl + "/api/movies", String.class);
            }
        } else {
            return restTemplate.getForObject(monolithUrl + "/api/movies", String.class);
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


}