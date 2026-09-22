package com.project.techstore.brand;

import com.project.techstore.brand.dto.BrandRequest;
import com.project.techstore.brand.dto.BrandResponse;
import com.project.techstore.brand.entity.Brand;
import com.project.techstore.brand.repository.BrandRepository;
import com.project.techstore.brand.service.BrandService;
import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    private Brand sampleBrand;

    @BeforeEach
    void setUp() {
        sampleBrand = Brand.builder()
                .name("Apple")
                .slug("apple")
                .description("Tập đoàn Apple")
                .status("ACTIVE")
                .build();
        sampleBrand.setId(1L);
    }

    @Test
    void testGetAllBrands() {
        when(brandRepository.findAll()).thenReturn(List.of(sampleBrand));

        List<BrandResponse> list = brandService.getAllBrands();

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("Apple", list.get(0).getName());
    }

    @Test
    void testGetByIdSuccess() {
        when(brandRepository.findById(1L)).thenReturn(Optional.of(sampleBrand));

        BrandResponse res = brandService.getById(1L);

        assertNotNull(res);
        assertEquals(1L, res.getId());
        assertEquals("Apple", res.getName());
    }

    @Test
    void testGetByIdNotFound() {
        when(brandRepository.findById(99L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> brandService.getById(99L));
        assertEquals(ErrorCode.BRAND_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void testCreateBrandSuccess() {
        BrandRequest request = BrandRequest.builder()
                .name("ASUS ROG")
                .description("Dòng gaming ASUS")
                .build();

        when(brandRepository.existsByName("ASUS ROG")).thenReturn(false);
        when(brandRepository.existsBySlug("asus-rog")).thenReturn(false);
        when(brandRepository.save(any(Brand.class))).thenAnswer(inv -> {
            Brand b = inv.getArgument(0);
            b.setId(2L);
            return b;
        });

        BrandResponse created = brandService.create(request);

        assertNotNull(created);
        assertEquals("ASUS ROG", created.getName());
        assertEquals("asus-rog", created.getSlug());
        verify(brandRepository, times(1)).save(any(Brand.class));
    }

    @Test
    void testCreateBrandDuplicateNameThrowsException() {
        BrandRequest request = BrandRequest.builder()
                .name("Apple")
                .build();

        when(brandRepository.existsByName("Apple")).thenReturn(true);

        AppException ex = assertThrows(AppException.class, () -> brandService.create(request));
        assertEquals(ErrorCode.CONFLICT, ex.getErrorCode());
        verify(brandRepository, never()).save(any(Brand.class));
    }
}
