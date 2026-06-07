package ru.movie.event.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class EventConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventConsumer.class);


    @KafkaListener(topics = "${app.topics.user}", groupId = "${spring.kafka.consumer.group-id}")
    public void onUser(ConsumerRecord<String, String> record, @Payload String payload) {
        log.info("Consumed User event: topic={} partition={} offset={} key={} value={}",
                record.topic(), record.partition(), record.offset(), record.key(), payload);
    }

    @KafkaListener(topics = "${app.topics.payment}", groupId = "${spring.kafka.consumer.group-id}")
    public void onPayment(ConsumerRecord<String, String> record, @Payload String payload) {
        log.info("Consumed Payment event: topic={} partition={} offset={} key={} value={}",
                record.topic(), record.partition(), record.offset(), record.key(), payload);
    }

    @KafkaListener(topics = "${app.topics.movie}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMovie(ConsumerRecord<String, String> record, @Payload String payload) {
        log.info("Consumed Movie event: topic={} partition={} offset={} key={} value={}",
                record.topic(), record.partition(), record.offset(), record.key(), payload);
    }
}
