package com.dobell.similarproducts.service;

import com.dobell.similarproducts.model.ProductDetail;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Servicio encargado de llamar a la API externa para obtener los datos necesarios de productos relacionados
 * @author dobell
 */
@Service
public class SimilarProductsService {

    private static final Logger logger = LoggerFactory.getLogger(
        SimilarProductsService.class
    );

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
        if (productId == null || productId.trim().isEmpty()) {
            logger.warn("ID de producto inválido recibido: {}", productId);
            return List.of();
        }

        logger.info(
            "Obteniendo productos similares para el producto ID: {}",
            productId
        );

        // Get similar product IDs
        String[] similarIds = getSimilarProductIds(productId);

        if (similarIds == null || similarIds.length == 0) {
            logger.info(
                "No se encontraron productos similares para el producto ID: {}",
                productId
            );
            return List.of();
        }

        logger.debug(
            "Se encontraron {} productos similares para el producto ID: {}",
            similarIds.length,
            productId
        );

        // Obtención en paralelo de los datos de varios productos
        List<CompletableFuture<ProductDetail>> futures = Arrays.stream(
            similarIds
        )
            .map(id ->
                CompletableFuture.supplyAsync(() -> getProductDetail(id))
            )
            .collect(Collectors.toList());

        // Wait for all futures to complete and collect results
        return futures
            .stream()
            .map(future -> {
                try {
                    return future.get();
                } catch (InterruptedException | ExecutionException e) {
                    logger.error(
                        "Error al obtener detalles del producto similar: {}",
                        e.getMessage(),
                        e
                    );
                    Thread.currentThread().interrupt();
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
        logger.debug(
            "Obteniendo IDs de productos similares para el producto ID: {}",
            productId
        );
        String url = UriComponentsBuilder.fromHttpUrl(existingApiBaseUrl)
            .path("/product/{productId}/similarids")
            .buildAndExpand(productId)
            .toUriString();

        try {
            String[] result = restTemplate.getForObject(url, String[].class);
            logger.debug(
                "Se obtuvieron {} IDs de productos similares",
                result != null ? result.length : 0
            );
            return result;
        } catch (Exception e) {
            logger.error(
                "Error al obtener IDs de productos similares para el producto ID {}: {}",
                productId,
                e.getMessage(),
                e
            );
            return new String[0];
        }
    }

    /**
     * Obtención de detalles de un producto por id
     * @param productId producto a consultar
     * @return detalles del producto
     */
    public ProductDetail getProductDetail(String productId) {
        logger.debug("Obteniendo detalles del producto ID: {}", productId);
        String url = UriComponentsBuilder.fromHttpUrl(existingApiBaseUrl)
            .path("/product/{productId}")
            .buildAndExpand(productId)
            .toUriString();

        try {
            ProductDetail result = restTemplate.getForObject(
                url,
                ProductDetail.class
            );
            if (result != null) {
                logger.debug(
                    "Se obtuvieron detalles del producto ID: {}",
                    productId
                );
            } else {
                logger.warn(
                    "No se encontraron detalles para el producto ID: {}",
                    productId
                );
            }
            return result;
        } catch (Exception e) {
            logger.error(
                "Error al obtener detalles del producto ID {}: {}",
                productId,
                e.getMessage(),
                e
            );
            return null;
        }
    }
}
