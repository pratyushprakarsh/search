package com.pratyush.search.elasticsearch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Price {

    private String range;
    private double min;
    private double max;
}
