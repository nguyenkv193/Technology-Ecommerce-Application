package com.project.techstore.banner.service;

import com.project.techstore.banner.dto.BannerRequest;
import com.project.techstore.banner.dto.BannerResponse;
import com.project.techstore.banner.entity.Banner;
import com.project.techstore.banner.repository.BannerRepository;
import com.project.techstore.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;

    @Transactional(readOnly = true)
    public List<BannerResponse> getActiveBanners(String position) {
        Instant now = Instant.now();
        return bannerRepository.findActiveBannersByPosition(position, now)
                .stream()
                .map(BannerResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BannerResponse> getAllBanners(String position) {
        List<Banner> banners = (position != null && !position.isBlank())
                ? bannerRepository.findByPositionOrderByDisplayOrderAsc(position)
                : bannerRepository.findAllByOrderByPositionAscDisplayOrderAsc();

        return banners.stream().map(BannerResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public BannerResponse getById(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner", "id", id));
        return BannerResponse.from(banner);
    }

    @Transactional
    public BannerResponse create(BannerRequest request) {
        Banner banner = Banner.builder()
                .title(request.getTitle())
                .imageUrl(request.getImageUrl())
                .mobileImageUrl(request.getMobileImageUrl())
                .linkUrl(request.getLinkUrl())
                .targetType(request.getTargetType() != null ? request.getTargetType() : "_self")
                .position(request.getPosition() != null ? request.getPosition() : "HERO_SLIDER")
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .clickCount(0L)
                .build();

        Banner saved = bannerRepository.save(banner);
        log.info("Đã tạo mới banner: {} (ID: {})", saved.getTitle(), saved.getId());
        return BannerResponse.from(saved);
    }

    @Transactional
    public BannerResponse update(Long id, BannerRequest request) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner", "id", id));

        banner.setTitle(request.getTitle());
        banner.setImageUrl(request.getImageUrl());
        banner.setMobileImageUrl(request.getMobileImageUrl());
        banner.setLinkUrl(request.getLinkUrl());
        banner.setTargetType(request.getTargetType() != null ? request.getTargetType() : "_self");
        banner.setPosition(request.getPosition() != null ? request.getPosition() : banner.getPosition());
        banner.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : banner.getDisplayOrder());
        banner.setStatus(request.getStatus() != null ? request.getStatus() : banner.getStatus());
        banner.setStartDate(request.getStartDate());
        banner.setEndDate(request.getEndDate());

        Banner updated = bannerRepository.save(banner);
        log.info("Đã cập nhật banner ID: {}", id);
        return BannerResponse.from(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!bannerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Banner", "id", id);
        }
        bannerRepository.deleteById(id);
        log.info("Đã xóa banner ID: {}", id);
    }

    @Transactional
    public void toggleStatus(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner", "id", id));
        banner.setStatus("ACTIVE".equalsIgnoreCase(banner.getStatus()) ? "INACTIVE" : "ACTIVE");
        bannerRepository.save(banner);
    }

    @Transactional
    public void recordClick(Long id) {
        bannerRepository.incrementClickCount(id);
    }
}
