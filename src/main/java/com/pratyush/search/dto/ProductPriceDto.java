package com.pratyush.search.dto;

import lombok.Data;

@Data
public class ProductPriceDto extends ProductDto {
    private PriceDto price;
}