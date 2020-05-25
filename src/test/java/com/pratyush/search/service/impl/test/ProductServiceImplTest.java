package com.pratyush.search.service.impl.test;

import com.pratyush.search.dto.ProductDto;
import com.pratyush.search.elasticsearch.repository.ProductSearchRepository;
import com.pratyush.search.kafka.producer.Producer;
import com.pratyush.search.service.ProductService;
import com.pratyush.search.service.impl.ProductServiceImpl;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class ProductServiceImplTest {

    private static ObjectMapper objectMapper = new ObjectMapper();
    @MockBean
    private ProductSearchRepository productSearchRepository;
    @MockBean
    private Producer producer;
    @MockBean
    private RestTemplate restTemplate;
    private ProductService productService = new ProductServiceImpl(productSearchRepository, objectMapper, producer, restTemplate);

    @BeforeAll
    public static void beforeAll() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> T getObjectFromFile(String fileName, Class<T> className) throws IOException {
        File file = new File("src/test/resources/__files/" + fileName);
        return objectMapper.readValue(file, className);
    }

    @Test
    public void insertProduct_throws_error_when_product_id_is_null() throws IOException {
        ProductDto productSearchDto = getObjectFromFile("productdto.json", ProductDto.class);
        productSearchDto.setId(null);
        IllegalStateException thrown = Assertions.assertThrows(
                IllegalStateException.class,
                () -> productService.insertProduct(productSearchDto),
                "Expected insertProduct() to throw IllegalStateException, but it didn't"
        );
        Assertions.assertTrue(thrown.getMessage().contains("product_id"));
    }

    @Test
    public void insertProduct_throws_error_when_is_visible_is_null() throws IOException {
        ProductDto productSearchDto = getObjectFromFile("productdto.json", ProductDto.class);
        productSearchDto.setIsVisible(null);
        IllegalStateException thrown = Assertions.assertThrows(
                IllegalStateException.class,
                () -> productService.insertProduct(productSearchDto),
                "Expected insertProduct() to throw IllegalStateException, but it didn't"
        );
        Assertions.assertTrue(thrown.getMessage().contains("isVisible"));
    }

    @Test
    public void insertProduct_throws_error_when_title_is_null() throws IOException {
        ProductDto productSearchDto = getObjectFromFile("productdto.json", ProductDto.class);
        productSearchDto.setTitle(null);
        IllegalStateException thrown = Assertions.assertThrows(
                IllegalStateException.class,
                () -> productService.insertProduct(productSearchDto),
                "Expected insertProduct() to throw IllegalStateException, but it didn't"
        );
        Assertions.assertTrue(thrown.getMessage().contains("title"));
    }

    @Test
    public void insertProduct_throws_error_when_description_is_null() throws IOException {
        ProductDto productSearchDto = getObjectFromFile("productdto.json", ProductDto.class);
        productSearchDto.setDescription(null);
        IllegalStateException thrown = Assertions.assertThrows(
                IllegalStateException.class,
                () -> productService.insertProduct(productSearchDto),
                "Expected insertProduct() to throw IllegalStateException, but it didn't"
        );
        Assertions.assertTrue(thrown.getMessage().contains("description"));
    }

    @Test
    public void insertProduct_throws_error_when_manufacturer_is_null() throws IOException {
        ProductDto productSearchDto = getObjectFromFile("productdto.json", ProductDto.class);
        productSearchDto.setManufacturer(null);
        IllegalStateException thrown = Assertions.assertThrows(
                IllegalStateException.class,
                () -> productService.insertProduct(productSearchDto),
                "Expected insertProduct() to throw IllegalStateException, but it didn't"
        );
        Assertions.assertTrue(thrown.getMessage().contains("manufacturer"));
    }
}