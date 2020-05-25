package com.pratyush.search.elasticsearch.repository;

import com.pratyush.search.elasticsearch.entity.ProductSearchEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductSearchEntity, String> {
}
