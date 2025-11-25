package com.dobell.similarproducts.service;

import com.dobell.similarproducts.model.ProductDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class SimilarProductsServiceTest {

    @InjectMocks
    private SimilarProductsService similarProductsService;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSimilarProducts_Success() {
        // Given
        String productId = "123";
        String[] similarIds = {"456", "789"};
        ProductDetail product1 = new ProductDetail("456", "Product 456", 29.99, true);
        ProductDetail product2 = new ProductDetail("789", "Product 789", 39.99, false);

        when(restTemplate.getForObject(anyString(), eq(String[].class)))
                .thenReturn(similarIds);
        when(restTemplate.getForObject(anyString(), eq(ProductDetail.class)))
                .thenReturn(product1, product2);

        // When
        List<ProductDetail> result = similarProductsService.getSimilarProducts(productId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("456", result.get(0).getId());
        assertEquals("789", result.get(1).getId());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String[].class));
        verify(restTemplate, times(2)).getForObject(anyString(), eq(ProductDetail.class));
    }

    @Test
    void testGetSimilarProducts_EmptyResult() {
        // Given
        String productId = "123";

        when(restTemplate.getForObject(anyString(), eq(String[].class)))
                .thenReturn(new String[0]);

        // When
        List<ProductDetail> result = similarProductsService.getSimilarProducts(productId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String[].class));
    }

    @Test
    void testGetSimilarProducts_NullResult() {
        // Given
        String productId = "123";

        when(restTemplate.getForObject(anyString(), eq(String[].class)))
                .thenReturn(null);

        // When
        List<ProductDetail> result = similarProductsService.getSimilarProducts(productId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String[].class));
    }

    @Test
    void testGetSimilarProducts_RestTemplateException() {
        // Given
        String productId = "123";

        when(restTemplate.getForObject(anyString(), eq(String[].class)))
                .thenThrow(new RuntimeException("Network error"));

        // When
        List<ProductDetail> result = similarProductsService.getSimilarProducts(productId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String[].class));
    }

    @Test
    void testGetProductDetail_Success() {
        // Given
        String productId = "456";
        ProductDetail expectedProduct = new ProductDetail("456", "Product 456", 29.99, true);

        when(restTemplate.getForObject(anyString(), eq(ProductDetail.class)))
                .thenReturn(expectedProduct);

        // When
        ProductDetail result = similarProductsService.getProductDetail(productId);

        // Then
        assertNotNull(result);
        assertEquals("456", result.getId());
        assertEquals("Product 456", result.getName());
        assertEquals(29.99, result.getPrice());
        assertTrue(result.isAvailable());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(ProductDetail.class));
    }

    @Test
    void testGetProductDetail_RestTemplateException() {
        // Given
        String productId = "456";

        when(restTemplate.getForObject(anyString(), eq(ProductDetail.class)))
                .thenThrow(new RuntimeException("Network error"));

        // When
        ProductDetail result = similarProductsService.getProductDetail(productId);

        // Then
        assertNull(result);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(ProductDetail.class));
    }
}
