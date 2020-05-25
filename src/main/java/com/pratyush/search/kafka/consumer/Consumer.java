package com.pratyush.search.kafka.consumer;

import com.pratyush.search.elasticsearch.entity.ProductSearchEntity;
import com.pratyush.search.elasticsearch.repository.ProductSearchRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class Consumer {

    private static final String PRODUCT_SEARCH_TOPIC_NAME = "product_details_search";
    private static final String PRODUCT_GROUP_ID = "search";
    private final ProductSearchRepository productSearchRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = PRODUCT_SEARCH_TOPIC_NAME, groupId = PRODUCT_GROUP_ID)
    public void consume(String message) throws JsonProcessingException {
        log.info("Consuming message from topic {} and message {} ", PRODUCT_SEARCH_TOPIC_NAME, message);
        ProductSearchEntity productSearchEntity = objectMapper.readValue(message, ProductSearchEntity.class);
        productSearchRepository.save(productSearchEntity);
    }
}
