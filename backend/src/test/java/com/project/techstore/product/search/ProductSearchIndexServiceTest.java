package com.project.techstore.product.search;

import com.project.techstore.brand.entity.Brand;
import com.project.techstore.category.entity.Category;
import com.project.techstore.product.entity.Product;
import com.project.techstore.product.entity.ProductStatus;
import com.project.techstore.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductSearchIndexServiceTest {

    @Mock
    private ElasticsearchOperations operations;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private IndexOperations indexOperations;

    @InjectMocks
    private ProductSearchIndexService indexService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        Category category = Category.builder().name("Laptop").slug("laptop").build();
        Brand brand = Brand.builder().name("Apple").slug("apple").build();

        sampleProduct = Product.builder()
                .name("MacBook Pro 16")
                .slug("macbook-pro-16")
                .description("Laptop Apple")
                .category(category)
                .brand(brand)
                .status(ProductStatus.ACTIVE)
                .variants(new ArrayList<>())
                .attributes(new ArrayList<>())
                .images(new ArrayList<>())
                .build();
        sampleProduct.setId(100L);
    }

    @Test
    void testInitializeIndexSuccess() {
        when(operations.indexOps(ProductSearchDocument.class)).thenReturn(indexOperations);
        when(indexOperations.exists()).thenReturn(true);

        indexService.initializeIndex();

        verify(indexOperations).exists();
        verify(indexOperations, never()).createWithMapping();
    }

    @Test
    void testEnsureIndexCreatesWhenNotExists() {
        when(operations.indexOps(ProductSearchDocument.class)).thenReturn(indexOperations);
        when(indexOperations.exists()).thenReturn(false);
        when(productRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sampleProduct)));

        indexService.ensureIndexAvailable();

        verify(indexOperations).createWithMapping();
        verify(operations).save(anyList());
    }

    @Test
    void testUpsertProductExisting() {
        when(operations.indexOps(ProductSearchDocument.class)).thenReturn(indexOperations);
        when(indexOperations.exists()).thenReturn(true);
        when(productRepository.findWithDetailsById(100L)).thenReturn(Optional.of(sampleProduct));

        indexService.upsertProduct(100L);

        verify(operations).save(any(ProductSearchDocument.class));
    }

    @Test
    void testUpsertProductNotFoundDeletes() {
        when(operations.indexOps(ProductSearchDocument.class)).thenReturn(indexOperations);
        when(indexOperations.exists()).thenReturn(true);
        when(productRepository.findWithDetailsById(100L)).thenReturn(Optional.empty());

        indexService.upsertProduct(100L);

        verify(operations).delete("100", ProductSearchDocument.class);
    }

    @Test
    void testDeleteProduct() {
        when(operations.indexOps(ProductSearchDocument.class)).thenReturn(indexOperations);
        when(indexOperations.exists()).thenReturn(true);

        indexService.deleteProduct(100L);

        verify(operations).delete("100", ProductSearchDocument.class);
    }

    @Test
    void testReindexAllProducts() {
        when(operations.indexOps(ProductSearchDocument.class)).thenReturn(indexOperations);
        when(indexOperations.exists()).thenReturn(true);
        when(productRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sampleProduct)));

        long indexed = indexService.reindexAllProducts();

        assertEquals(1, indexed);
        verify(indexOperations).delete();
        verify(indexOperations).createWithMapping();
        verify(operations).save(anyList());
    }
}
