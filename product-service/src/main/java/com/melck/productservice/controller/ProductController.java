package com.melck.productservice.controller;

import com.melck.productservice.dto.ProductRequest;
import com.melck.productservice.dto.ProductResponse;
import com.melck.productservice.entity.Category;
import com.melck.productservice.services.CategoryService;
import com.melck.productservice.services.ProductService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {

    private final ProductService service;
    private final CategoryService categoryService;

    @PostMapping
    @CacheEvict(value = "products", allEntries = true)
    @Operation(summary = "Register a new product" , description = "This endpoint is used to register new products in database ")
    public ResponseEntity<ProductResponse> registerProduct(@Valid @RequestBody ProductRequest productRequest) {
        ProductResponse newProduct = service.registerProduct(productRequest);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newProduct.getId()).toUri();
        return ResponseEntity.created(uri).body(newProduct);
    }

    @GetMapping("/cats")
    public ResponseEntity<List<Category>> findAllCats() {
        List<Category> categories = categoryService.findAllCats();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/cats/{id}")
    public ResponseEntity<Category> findCategoryById(@PathVariable Long id) {
        Category category = categoryService.findCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping
    @CircuitBreaker(name = "review", fallbackMethod = "fallbackMethod")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(value = "categoryId", defaultValue = "0") Long categoryId,
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "isInSale", defaultValue = "false" ) boolean isInSale,
            Pageable pageable
    ) {
        Page<ProductResponse> products = service.getAllProduct(categoryId, name.trim(), isInSale, pageable);
        return ResponseEntity.ok().body(products);
    }

    @GetMapping("/category")
    public ResponseEntity<Page<ProductResponse>> getAllProductsPerCategory(
            @RequestParam(value = "categoryId", defaultValue = "0") Long categoryId,
            Pageable pageable
    ) {
        Page<ProductResponse> products = service.getAllProductPerCategory(categoryId, pageable);
        return ResponseEntity.ok().body(products);
    }

    @GetMapping("/category/filter")
    @CircuitBreaker(name = "review", fallbackMethod = "fallbackMethod")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(value = "categoryId", defaultValue = "0") Long categoryId,
            @RequestParam(value = "min-price", defaultValue = "0") Long minPrice,
            @RequestParam(value = "max-price", defaultValue = "10000000") Long maxPrice,
            Pageable pageable
    ) {
        Page<ProductResponse> products = service.getAllProductFilter(categoryId, minPrice, maxPrice, pageable);
        return ResponseEntity.ok().body(products);
    }

    @GetMapping("/{id}")
    @CircuitBreaker(name = "review", fallbackMethod = "fallbackMethod")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse product = service.getProductById(id);
        return ResponseEntity.ok().body(product);
    }

    public ResponseEntity<String> fallbackMethod(WebClientResponseException e) {
        log.info("Oops! Something went wrong, the review service is down. Please try again later.", e);
        return ResponseEntity.ok().body("Oops! Something went wrong, the review service is down. Please try again later.");
    }

    @GetMapping("/cart")
    public ResponseEntity<List<ProductResponse>> getProductsIntoACart(@RequestParam Set<Long> productsId) {
        List<ProductResponse> products = service.getProductsInACart(productsId);
        return ResponseEntity.ok().body(products);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> editProduct(@PathVariable Long productId, @RequestBody ProductRequest productRequest) {
        return ResponseEntity.ok().body(service.editProduct(productId, productRequest));
    }
}
