package com.pratyush.search.service.impl;

import com.pratyush.search.dto.PriceDto;
import com.pratyush.search.dto.ProductDto;
import com.pratyush.search.dto.ProductPriceDto;
import com.pratyush.search.dto.ResponseWrapper;
import com.pratyush.search.elasticsearch.entity.Price;
import com.pratyush.search.elasticsearch.entity.ProductSearchEntity;
import com.pratyush.search.elasticsearch.repository.ProductSearchRepository;
import com.pratyush.search.error.DuplicateEntityException;
import com.pratyush.search.error.EntityNotFoundException;
import com.pratyush.search.error.SystemErrorException;
import com.pratyush.search.kafka.producer.Producer;
import com.pratyush.search.service.ProductService;
import com.pratyush.search.util.ConverterUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.Asserts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private static final String PRODUCT_SEARCH_TOPIC = "product_details_search";
    private static final String PRODUCT_NOT_FOUND_ERROR_MESSAGE = "Product not found for Product ID ";
    private static final String PRODUCT_ALREADY_EXISTS_ERROR_MESSAGE = "Product already exists for Product ID ";
    private static final String PRICE_NOT_FOUND_ERROR_MESSAGE = "Price not found for Product ID ";
    private final ProductSearchRepository productSearchRepository;
    private final ObjectMapper objectMapper;
    private final Producer producer;
    private final RestTemplate restTemplate;
    @Value("${fetch.product.endpoint}")
    private String fetchProductEndPoint;
    @Value("${fetch.product.price.endpoint}")
    private String fetchProductPriceEndPoint;

    @PostConstruct
    public void init() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void insertProduct(ProductDto productDto) {
        Asserts.notBlank(productDto.getId(), "product_id");
        Asserts.notBlank(productDto.getTitle(), "title");
        Asserts.notBlank(productDto.getDescription(), "description");
        Asserts.notBlank(productDto.getManufacturer(), "manufacturer");
        Asserts.notNull(productDto.getIsVisible(), "isVisible");

        log.info("Inserting product ID {} and request {} ", productDto.getId(), ConverterUtil.toStringUsingJsonProperty(productDto));

        Optional<ProductSearchEntity> productSearchEntityOptional = productSearchRepository.findById(productDto.getId());
        if (productSearchEntityOptional.isPresent()) {
            throw new DuplicateEntityException(PRODUCT_ALREADY_EXISTS_ERROR_MESSAGE.concat(productDto.getId()));
        }

        sendProductEntityInQueue(objectMapper.convertValue(productDto, ProductSearchEntity.class));
    }

    @Override
    public ProductDto fetchProduct(String productId) {
        log.info("Fetching product for product ID {} ", productId);
        Optional<ProductSearchEntity> productSearchEntityOptional = productSearchRepository.findById(productId);
        if (!productSearchEntityOptional.isPresent()) {
            throw new EntityNotFoundException(PRODUCT_NOT_FOUND_ERROR_MESSAGE.concat(productId));
        }
        ProductDto productDto = objectMapper.convertValue(productSearchEntityOptional.get(), ProductDto.class);
        log.debug("Product data response for product ID {} : {}", productId, ConverterUtil.toStringUsingJsonProperty(productDto));
        return productDto;
    }

    @Override
    public void updateProduct(String productId, ProductDto productDto) {
        Asserts.notBlank(productDto.getTitle(), "title");
        Asserts.notBlank(productDto.getDescription(), "description");
        Asserts.notBlank(productDto.getManufacturer(), "manufacturer");
        Asserts.notNull(productDto.getIsVisible(), "isVisible");

        log.info("Updating product ID {} and request {} ", productId, ConverterUtil.toStringUsingJsonProperty(productDto));
        productDto.setId(productId);

        Optional<ProductSearchEntity> productSearchEntityOptional = productSearchRepository.findById(productId);
        if (!productSearchEntityOptional.isPresent()) {
            throw new EntityNotFoundException(PRODUCT_NOT_FOUND_ERROR_MESSAGE.concat(productId));
        }
        ProductSearchEntity productSearchEntity = objectMapper.convertValue(productDto, ProductSearchEntity.class);
        productSearchEntity.setPrice(productSearchEntityOptional.get().getPrice());

        sendProductEntityInQueue(productSearchEntity);
    }

    public void sendProductEntityInQueue(ProductSearchEntity productSearchEntity) {
        Date currentDate = new Date();
        productSearchEntity.setCreatedAt(currentDate);
        productSearchEntity.setPublishedAt(currentDate);
        productSearchEntity.setUpdatedAt(currentDate);

        producer.sendMessage(PRODUCT_SEARCH_TOPIC, ConverterUtil.toStringUsingJsonProperty(productSearchEntity));
        log.info("Product data for product ID {} pushed to queue", productSearchEntity.getId());
    }

    @Override
    public void addProductPrice(String productId, PriceDto priceDto) {
        Asserts.notBlank(priceDto.getRange(), "range");
        Asserts.notNull(priceDto.getMax(), "max");
        Asserts.notNull(priceDto.getMin(), "min");

        log.info("Adding Product Price for product ID {} and request {} ", productId, ConverterUtil.toStringUsingJsonProperty(priceDto));
        Optional<ProductSearchEntity> productSearchEntityOptional = productSearchRepository.findById(productId);
        if (!productSearchEntityOptional.isPresent()) {
            throw new EntityNotFoundException(PRODUCT_NOT_FOUND_ERROR_MESSAGE.concat(productId));
        }
        ProductSearchEntity productSearchEntity = productSearchEntityOptional.get();
        productSearchEntity.setPrice(objectMapper.convertValue(priceDto, Price.class));
        productSearchEntity.setUpdatedAt(new Date());

        productSearchRepository.save(productSearchEntity);
        log.info("Price updated for product ID {} and request {} ", productId, ConverterUtil.toStringUsingJsonProperty(priceDto));
    }

    @Override
    public PriceDto fetchProductPrice(String productId) {
        log.info("Fetching price for product ID {} ", productId);
        Optional<ProductSearchEntity> productSearchEntityOptional = productSearchRepository.findById(productId);
        if (!productSearchEntityOptional.isPresent()) {
            throw new EntityNotFoundException(PRODUCT_NOT_FOUND_ERROR_MESSAGE.concat(productId));
        }
        if (productSearchEntityOptional.get().getPrice() == null) {
            throw new EntityNotFoundException(PRICE_NOT_FOUND_ERROR_MESSAGE.concat(productId));
        }
        PriceDto priceDto = objectMapper.convertValue(productSearchEntityOptional.get().getPrice(), PriceDto.class);
        log.debug("Price data response for product ID {} : {}", productId, ConverterUtil.toStringUsingJsonProperty(priceDto));
        return priceDto;
    }

    @Override
    public ProductPriceDto fetchProductAndPrice(String productId) {
        ProductPriceDto productPriceDto;
        CompletableFuture<ProductDto> productDtoFuture = null;
        CompletableFuture<PriceDto> priceDtoFuture = null;

        try {
            log.info("Calling Fetch Product API for productId {} ", productId);
            productDtoFuture = CompletableFuture.supplyAsync(() -> restTemplate.exchange(fetchProductEndPoint, HttpMethod.GET, new HttpEntity<>(null), new ParameterizedTypeReference<ResponseWrapper<ProductDto>>() {
                    },
                    productId).getBody().getData());

            log.info("Calling Fetch Product Price API for productId {} ", productId);
            priceDtoFuture = CompletableFuture.supplyAsync(() -> restTemplate.exchange(fetchProductPriceEndPoint, HttpMethod.GET, new HttpEntity<>(null), new ParameterizedTypeReference<ResponseWrapper<PriceDto>>() {
                    },
                    productId).getBody().getData());

            if (productDtoFuture != null && productDtoFuture.get() != null) {
                ProductDto productDto = productDtoFuture.get();
                log.debug("Fetch Product API response for Product ID {} : {} ", productId, ConverterUtil.toStringUsingJsonProperty(productDto));
                productPriceDto = objectMapper.convertValue(productDto, ProductPriceDto.class);

                try {
                    if (priceDtoFuture != null && priceDtoFuture.get() != null) {
                        PriceDto priceDto = priceDtoFuture.get();
                        log.debug("Fetch Product Price API response for Product ID {} : {} ", productId, ConverterUtil.toStringUsingJsonProperty(priceDto));
                        productPriceDto.setPrice(priceDto);
                    }
                } catch (ExecutionException e) {
                    // Ignore
                }

                log.debug("Combined Product price data response for product ID {} : {}", productId, ConverterUtil.toStringUsingJsonProperty(productPriceDto));
            } else {
                throw new EntityNotFoundException(PRODUCT_NOT_FOUND_ERROR_MESSAGE.concat(productId));
            }
        } catch (ExecutionException e) {
            throw new EntityNotFoundException(PRODUCT_NOT_FOUND_ERROR_MESSAGE.concat(productId));
        } catch (Exception e) {
            log.error("Error Occured while Fetching Product Prices ", e);
            throw new SystemErrorException("Error Occured while Fetching Product Prices");
        } finally {

            if (productDtoFuture != null && !productDtoFuture.isDone()) {
                productDtoFuture.cancel(true);
            }
            if (priceDtoFuture != null && !priceDtoFuture.isDone()) {
                priceDtoFuture.cancel(true);
            }

        }

        return productPriceDto;

    }
}