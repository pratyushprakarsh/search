package com.pratyush.search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PriceDto {

    private String range;
    private Double min;
    private Double max;
}
