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
 * Servicio encargado de llamar a la API externa para obtener los datos necesarios de productos relacionados
 * @author dobell
 */
@Service
public class SimilarProductsService {

    private final RestTemplate restTemplate;
    
    // url de pa API de consulta
    @Value("${existing.api.base.url:http://localhost:3001}")
    private String existingApiBaseUrl;

    @Autowired
    public SimilarProductsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Obtiene de la API los productos similares al del id indicado
     * @param productId id de producto actual
     * @return lista de productos similares
     */
    public List<ProductDetail> getSimilarProducts(String productId) {
        // Get similar product IDs
        String[] similarIds = getSimilarProductIds(productId);
        
        if (similarIds == null || similarIds.length == 0) {
            return List.of();
        }

        // Obtención en paralelo de los datos de varios productos
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

    /**
     * Obtención de ids de productos similares
     * @param productId producto actual
     * @return listado de id de productos similares
     */
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

    /**
     * Obtención de detalles de un producto por id
     * @param productId producto a consultar
     * @return detalles del producto
     */
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
