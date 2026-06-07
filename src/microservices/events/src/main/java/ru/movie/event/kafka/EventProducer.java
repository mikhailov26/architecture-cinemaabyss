package ru.movie.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {

    private static final Logger log = LoggerFactory.getLogger(EventProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.topics.user}")
    private String userTopic;
    @Value("${app.topics.payment}")
    private String paymentTopic;
    @Value("${app.topics.movie}")
    private String movieTopic;

    public EventProducer(KafkaTemplate<String, String> kafkaTemplate,
                          ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public <T> void sendUserEvent(String key, T payload) {
        send(userTopic, key, payload);
    }

    public <T> void sendPaymentEvent(String key, T payload) {
        send(paymentTopic, key, payload);
    }

    public <T> void sendMovieEvent(String key, T payload) {
        send(movieTopic, key, payload);
    }

    private <T> void send(String topic, String key, T payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send(topic, key, json)
                    .whenComplete((res, ex) -> {
                        if (ex != null) {
                            log.error("Failed to send to topic={} key={} payload={}", topic, key, json, ex);
                        } else {
                            log.info("Produced event to topic={} partition={} offset={} key={} payload={}",
                                    topic,
                                    res.getRecordMetadata().partition(),
                                    res.getRecordMetadata().offset(),
                                    key,
                                    json);
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Serialization error for topic={} key={}", topic, key, e);
        }
    }
}
