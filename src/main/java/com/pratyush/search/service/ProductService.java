package com.pratyush.search.service;

import com.pratyush.search.dto.PriceDto;
import com.pratyush.search.dto.ProductDto;
import com.pratyush.search.dto.ProductPriceDto;

public interface ProductService {
    void insertProduct( ProductDto productDto);

    ProductDto fetchProduct(String productId);

    void updateProduct(String productId, ProductDto productDto);

    void addProductPrice(String productId, PriceDto priceDto);

    PriceDto fetchProductPrice(String productId);

    ProductPriceDto fetchProductAndPrice(String productId);
}
