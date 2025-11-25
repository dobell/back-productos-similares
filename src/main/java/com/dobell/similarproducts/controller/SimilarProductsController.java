package com.dobell.similarproducts.controller;

import com.dobell.similarproducts.model.ProductDetail;
import com.dobell.similarproducts.service.SimilarProductsService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para llamadas rest
 * @author dobell
 */
@RestController
@RequestMapping("/product")
public class SimilarProductsController {

    private static final Logger logger = LoggerFactory.getLogger(
        SimilarProductsController.class
    );

    private final SimilarProductsService similarProductsService;

    @Autowired
    public SimilarProductsController(
        SimilarProductsService similarProductsService
    ) {
        this.similarProductsService = similarProductsService;
    }

    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<ProductDetail>> getSimilarProducts(
        @PathVariable String productId
    ) {
        logger.info(
            "Solicitud recibida para productos similares del producto ID: {}",
            productId
        );
        List<ProductDetail> similarProducts =
            similarProductsService.getSimilarProducts(productId);
        logger.info(
            "Devuelto {} productos similares para el producto ID: {}",
            similarProducts.size(),
            productId
        );
        return ResponseEntity.ok(similarProducts);
    }
}
