package com.project.techstore.user;

import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
import com.project.techstore.user.dto.AddressRequest;
import com.project.techstore.user.dto.AddressResponse;
import com.project.techstore.user.entity.Address;
import com.project.techstore.user.entity.User;
import com.project.techstore.user.repository.AddressRepository;
import com.project.techstore.user.service.AddressService;
import com.project.techstore.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AddressService addressService;

    private User sampleUser;
    private Address sampleAddress;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .email("user@techstore.vn")
                .fullName("Nguyen Van A")
                .build();
        sampleUser.setId(1L);

        sampleAddress = Address.builder()
                .user(sampleUser)
                .recipientName("Nguyen Van A")
                .phone("0901234567")
                .province("Hồ Chí Minh")
                .district("Quận 1")
                .ward("Bến Nghé")
                .addressDetail("123 Lê Lợi")
                .isDefault(true)
                .build();
        sampleAddress.setId(100L);
    }

    @Test
    void testGetUserAddresses() {
        when(addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(1L))
                .thenReturn(List.of(sampleAddress));

        List<AddressResponse> result = addressService.getUserAddresses(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Nguyen Van A", result.get(0).getRecipientName());
        assertTrue(result.get(0).getIsDefault());
    }

    @Test
    void testCreateFirstAddressBecomesDefault() {
        AddressRequest request = AddressRequest.builder()
                .recipientName("Nguyen Van A")
                .phone("0901234567")
                .province("Hồ Chí Minh")
                .district("Quận 1")
                .ward("Bến Nghé")
                .addressDetail("123 Lê Lợi")
                .isDefault(false)
                .build();

        when(userService.getUserEntity(1L)).thenReturn(sampleUser);
        when(addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(1L))
                .thenReturn(Collections.emptyList());
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address saved = invocation.getArgument(0);
            saved.setId(101L);
            return saved;
        });

        AddressResponse result = addressService.createAddress(1L, request);

        assertNotNull(result);
        assertTrue(result.getIsDefault()); // First address automatically becomes default
        verify(addressRepository, never()).resetDefaultAddressForUser(1L);
    }

    @Test
    void testCreateAddressExplicitDefaultResetsExistingDefaults() {
        AddressRequest request = AddressRequest.builder()
                .recipientName("Nguyen Van B")
                .phone("0912345678")
                .province("Hà Nội")
                .district("Cầu Giấy")
                .ward("Dịch Vọng")
                .addressDetail("456 Cầu Giấy")
                .isDefault(true)
                .build();

        when(userService.getUserEntity(1L)).thenReturn(sampleUser);
        when(addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(1L))
                .thenReturn(List.of(sampleAddress));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address saved = invocation.getArgument(0);
            saved.setId(102L);
            return saved;
        });

        AddressResponse result = addressService.createAddress(1L, request);

        assertNotNull(result);
        assertTrue(result.getIsDefault());
        verify(addressRepository).resetDefaultAddressForUser(1L);
    }

    @Test
    void testUpdateAddressSuccess() {
        AddressRequest request = AddressRequest.builder()
                .recipientName("Nguyen Van A Updated")
                .phone("0909999999")
                .province("Hồ Chí Minh")
                .district("Quận 1")
                .ward("Bến Nghé")
                .addressDetail("789 Nguyễn Huệ")
                .isDefault(true)
                .build();

        when(addressRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(sampleAddress));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse result = addressService.updateAddress(1L, 100L, request);

        assertNotNull(result);
        assertEquals("Nguyen Van A Updated", result.getRecipientName());
        assertEquals("0909999999", result.getPhone());
    }

    @Test
    void testDeleteAddressSuccess() {
        when(addressRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(sampleAddress));

        addressService.deleteAddress(1L, 100L);

        verify(addressRepository).delete(sampleAddress);
    }

    @Test
    void testDeleteAddressNotFound() {
        when(addressRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> addressService.deleteAddress(1L, 999L));
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void testSetDefaultAddress() {
        sampleAddress.setIsDefault(false);
        when(addressRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(sampleAddress));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse result = addressService.setDefaultAddress(1L, 100L);

        assertNotNull(result);
        assertTrue(result.getIsDefault());
        verify(addressRepository).resetDefaultAddressForUser(1L);
    }
}
