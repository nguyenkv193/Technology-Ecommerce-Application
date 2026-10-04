package com.project.techstore.user.service;

import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
import com.project.techstore.user.dto.AddressRequest;
import com.project.techstore.user.dto.AddressResponse;
import com.project.techstore.user.entity.Address;
import com.project.techstore.user.entity.User;
import com.project.techstore.user.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<AddressResponse> getUserAddresses(Long userId) {
        return addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId)
                .stream()
                .map(AddressResponse::from)
                .toList();
    }

    @Transactional
    public AddressResponse createAddress(Long userId, AddressRequest request) {
        User user = userService.getUserEntity(userId);

        boolean hasExistingAddresses = !addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId).isEmpty();
        boolean shouldBeDefault = !hasExistingAddresses || Boolean.TRUE.equals(request.getIsDefault());

        if (shouldBeDefault && hasExistingAddresses) {
            addressRepository.resetDefaultAddressForUser(userId);
        }

        Address address = Address.builder()
                .user(user)
                .recipientName(request.getRecipientName().trim())
                .phone(request.getPhone().trim())
                .province(request.getProvince().trim())
                .district(request.getDistrict().trim())
                .ward(request.getWard().trim())
                .addressDetail(request.getAddressDetail().trim())
                .isDefault(shouldBeDefault)
                .build();

        Address saved = addressRepository.save(address);
        log.info("Đã tạo địa chỉ mới id={} cho user id={}", saved.getId(), userId);
        return AddressResponse.from(saved);
    }

    @Transactional
    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request) {
        Address address = getAddressEntityOfUser(userId, addressId);

        if (Boolean.TRUE.equals(request.getIsDefault()) && !Boolean.TRUE.equals(address.getIsDefault())) {
            addressRepository.resetDefaultAddressForUser(userId);
            address.setIsDefault(true);
        }

        address.setRecipientName(request.getRecipientName().trim());
        address.setPhone(request.getPhone().trim());
        address.setProvince(request.getProvince().trim());
        address.setDistrict(request.getDistrict().trim());
        address.setWard(request.getWard().trim());
        address.setAddressDetail(request.getAddressDetail().trim());

        Address updated = addressRepository.save(address);
        log.info("Đã cập nhật địa chỉ id={} cho user id={}", addressId, userId);
        return AddressResponse.from(updated);
    }

    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address address = getAddressEntityOfUser(userId, addressId);
        addressRepository.delete(address);
        log.info("Đã xóa địa chỉ id={} của user id={}", addressId, userId);
    }

    @Transactional
    public AddressResponse setDefaultAddress(Long userId, Long addressId) {
        Address address = getAddressEntityOfUser(userId, addressId);
        addressRepository.resetDefaultAddressForUser(userId);
        address.setIsDefault(true);
        Address updated = addressRepository.save(address);
        log.info("Đã đặt địa chỉ id={} làm mặc định cho user id={}", addressId, userId);
        return AddressResponse.from(updated);
    }

    private Address getAddressEntityOfUser(Long userId, Long addressId) {
        return addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy địa chỉ với ID: " + addressId));
    }
}
