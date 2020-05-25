package com.pratyush.search.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConverterUtil {

    private final static ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    /**
     * Json string using json property
     */
    public static String toStringUsingJsonProperty(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Exception while converting to string: {}, Exception: ", obj, e);
        }
        return "";
    }

}


