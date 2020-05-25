package com.pratyush.search.controller;


import com.pratyush.search.dto.PriceDto;
import com.pratyush.search.dto.ProductDto;
import com.pratyush.search.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.pratyush.search.dto.ResponseWrapper;


@RestController
@RequestMapping("v1/products")
@AllArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseWrapper<?> insertProduct(@RequestBody ProductDto productDto) {
        productService.insertProduct(productDto);
        return new ResponseWrapper<>(true, null, null);
    }

    @GetMapping("{productId}")
    public ResponseWrapper<?> fetchProduct(@PathVariable String productId) {
        return new ResponseWrapper<>(true, null, productService.fetchProduct(productId));
    }

    @PutMapping("{productId}")
    public ResponseWrapper<Object> updateProduct(@PathVariable String productId, @RequestBody ProductDto productDto) {
        productService.updateProduct(productId, productDto);
        return new ResponseWrapper<>(true, null, null);
    }

    @PostMapping("{productId}/price")
    public ResponseWrapper<Object> addProductPrice(@PathVariable String productId, @RequestBody PriceDto priceDto) {
        productService.addProductPrice(productId, priceDto);
        return new ResponseWrapper<>(true, null, null);
    }

    @GetMapping("{productId}/price")
    public ResponseWrapper<?> fetchProductPrice(@PathVariable String productId) {
        return new ResponseWrapper<>(true, null, productService.fetchProductPrice(productId));

    }

    @GetMapping("{productId}/aggregator/price")
    public ResponseWrapper<?> fetchProductAndPrice(@PathVariable String productId) {
        return new ResponseWrapper<>(true, null, productService.fetchProductAndPrice(productId));
    }

}
