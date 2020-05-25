package com.pratyush.search.kafka.producer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Slf4j
@Service
public class Producer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, String message) {
        log.info("Pushing message in Kafka topic {} and message {} ", topic, message);
        kafkaTemplate.send(topic, message);
    }
}
