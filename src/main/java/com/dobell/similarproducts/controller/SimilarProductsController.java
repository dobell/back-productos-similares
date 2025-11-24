package com.dobell.similarproducts.controller;


import com.dobell.similarproducts.model.ProductDetail;
import com.dobell.similarproducts.service.SimilarProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 * @author dobell
 */
@RestController
@RequestMapping("/product")
public class SimilarProductsController {

    private final SimilarProductsService similarProductsService;

    @Autowired
    public SimilarProductsController(SimilarProductsService similarProductsService) {
        this.similarProductsService = similarProductsService;
    }

    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<ProductDetail>> getSimilarProducts(@PathVariable String productId) {
        List<ProductDetail> similarProducts = similarProductsService.getSimilarProducts(productId);
        return ResponseEntity.ok(similarProducts);
    }
}