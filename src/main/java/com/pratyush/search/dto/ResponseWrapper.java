package com.pratyush.search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ResponseWrapper<T> {

    private final boolean success;
    private final String message;
    private final T data;
}