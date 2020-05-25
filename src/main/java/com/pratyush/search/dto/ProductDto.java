package com.pratyush.search.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ProductDto {

    @JsonProperty("product_id")
    private String id;
    private String sellerId;
    private String title;
    private String pageTitle;
    private String description;
    private String manufacturer;
    private Boolean isLowQuantity;
    private Boolean isSoldOut;
    private Boolean isBackorder;
    private List<MetaFieldDto> metafields;
    private Boolean requiresShipping;
    private Boolean isVisible;
    private Date publishedAt;
    private Date createdAt;
    private Date updatedAt;
    private WorkflowDto workflow;
}
