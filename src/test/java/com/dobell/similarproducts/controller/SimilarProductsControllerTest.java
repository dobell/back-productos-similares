package com.dobell.similarproducts.controller;

import com.dobell.similarproducts.model.ProductDetail;
import com.dobell.similarproducts.service.SimilarProductsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class SimilarProductsControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private SimilarProductsService similarProductsService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
            new SimilarProductsController(similarProductsService)
        ).build();
    }

    @Test
    void testGetSimilarProducts_Success() throws Exception {
        // Given
        String productId = "123";
        ProductDetail product1 = new ProductDetail("456", "Product 456", 29.99, true);
        ProductDetail product2 = new ProductDetail("789", "Product 789", 39.99, false);

        when(similarProductsService.getSimilarProducts(productId))
            .thenReturn(List.of(product1, product2));

        // When & Then
        mockMvc.perform(get("/product/{productId}/similar", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("456"))
                .andExpect(jsonPath("$[0].name").value("Product 456"))
                .andExpect(jsonPath("$[0].price").value(29.99))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].id").value("789"))
                .andExpect(jsonPath("$[1].name").value("Product 789"))
                .andExpect(jsonPath("$[1].price").value(39.99))
                .andExpect(jsonPath("$[1].available").value(false));

        verify(similarProductsService, times(1)).getSimilarProducts(productId);
    }

    @Test
    void testGetSimilarProducts_EmptyResult() throws Exception {
        // Given
        String productId = "123";

        when(similarProductsService.getSimilarProducts(productId))
            .thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/product/{productId}/similar", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(similarProductsService, times(1)).getSimilarProducts(productId);
    }

    @Test
    void testGetSimilarProducts_InvalidProductId() throws Exception {
        // Given
        String productId = "invalid";

        when(similarProductsService.getSimilarProducts(productId))
            .thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/product/{productId}/similar", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(similarProductsService, times(1)).getSimilarProducts(productId);
    }
}
