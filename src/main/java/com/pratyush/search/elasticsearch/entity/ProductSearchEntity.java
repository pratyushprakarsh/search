package com.pratyush.search.elasticsearch.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

@Document(indexName = "product_details_search")
@Data
public class ProductSearchEntity {

    @Id
    @JsonProperty("product_id")
    private String id;
    private String sellerId;
    private String title;
    private String pageTitle;
    private String description;
    private String manufacturer;
    private Price price;
    private Boolean isLowQuantity;
    private Boolean isSoldOut;
    private Boolean isBackorder;
    private List<MetaField> metafields;
    private Boolean requiresShipping;
    private Boolean isVisible;
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Date publishedAt;
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Date createdAt;
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Date updatedAt;
    private Workflow workflow;
}
