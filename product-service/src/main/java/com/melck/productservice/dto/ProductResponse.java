package com.melck.productservice.dto;

import com.melck.productservice.entity.Category;
import com.melck.productservice.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Data
@NoArgsConstructor
public class ProductResponse implements Serializable {
    private Long id;
    private String name;
    private String skuCode;
    private String description;
    private BigDecimal price;
    private String imgUrl;
    private String coverImg;
    private double rate;
    private Integer qtyReviews;
    private List<CategoryResponse> categories = new ArrayList<>();


    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.skuCode = product.getSkuCode();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.imgUrl = product.getImgUrl();
        this.coverImg = product.getCoverImg();
        this.rate = product.getRate();
        this.qtyReviews = product.getQtyReviews();
    }

    public static ProductResponse of(Product product) {
        var response = new ProductResponse(product);
        product.getCategories().forEach( cat -> response.getCategories().add( new CategoryResponse(cat)));
        log.info("testando {}", response);
        return response;
    }
}
