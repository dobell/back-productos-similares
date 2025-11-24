package com.dobell.similarproducts.service;

import com.dobell.similarproducts.model.ProductDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
/**
 *
 * @author dobell
 */
@Service
public class SimilarProductsService {

    private final RestTemplate restTemplate;
    
    @Value("${existing.api.base.url:http://localhost:3001}")
    private String existingApiBaseUrl;

    @Autowired
    public SimilarProductsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ProductDetail> getSimilarProducts(String productId) {
        // Get similar product IDs
        String[] similarIds = getSimilarProductIds(productId);
        
        if (similarIds == null || similarIds.length == 0) {
            return List.of();
        }

        // Get product details for each similar ID in parallel
        List<CompletableFuture<ProductDetail>> futures = Arrays.stream(similarIds)
                .map(id -> CompletableFuture.supplyAsync(() -> getProductDetail(id)))
                .collect(Collectors.toList());

        // Wait for all futures to complete and collect results
        return futures.stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        // Log error and return null for failed requests
                        System.err.println("Error getting product detail: " + e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private String[] getSimilarProductIds(String productId) {
        String url = UriComponentsBuilder.fromHttpUrl(existingApiBaseUrl)
                .path("/product/{productId}/similarids")
                .buildAndExpand(productId)
                .toUriString();

        try {
            return restTemplate.getForObject(url, String[].class);
        } catch (Exception e) {
            System.err.println("Error getting similar product IDs: " + e.getMessage());
            return new String[0];
        }
    }

    private ProductDetail getProductDetail(String productId) {
        String url = UriComponentsBuilder.fromHttpUrl(existingApiBaseUrl)
                .path("/product/{productId}")
                .buildAndExpand(productId)
                .toUriString();

        try {
            return restTemplate.getForObject(url, ProductDetail.class);
        } catch (Exception e) {
            System.err.println("Error getting product detail for ID " + productId + ": " + e.getMessage());
            return null;
        }
    }
}
