package ru.movie.event.model;

public record PaymentEvent(String paymentId, String userId, String status, Double amount) {
}
