package ru.movie.event.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.movie.event.kafka.EventsProducer;
import ru.movie.event.model.MovieEvent;
import ru.movie.event.model.PaymentEvent;
import ru.movie.event.model.UserEvent;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventsProducer producer;

    public EventController(EventsProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/user")
    public ResponseEntity<Map<String, Object>> createUserEvent(@RequestBody UserEvent event) {
        String key = event.userId() != null ? event.userId() : UUID.randomUUID().toString();
        producer.sendUserEvent(key, event);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", "success", "type", "User", "key", key));
    }

    @PostMapping("/payment")
    public ResponseEntity<Map<String, Object>> createPaymentEvent(@RequestBody PaymentEvent event) {
        String key = event.paymentId() != null ? event.paymentId() : UUID.randomUUID().toString();
        producer.sendPaymentEvent(key, event);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", "success", "type", "Payment", "key", key));
    }

    @PostMapping("/movie")
    public ResponseEntity<Map<String, Object>> createMovieEvent(@RequestBody MovieEvent event) {
        String key = event.movieId() != null ? event.movieId() : UUID.randomUUID().toString();
        producer.sendMovieEvent(key, event);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", "success", "type", "Movie", "key", key));
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("status", true);
    }
}
