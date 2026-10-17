package com.project.techstore.behavior.controller;

import com.project.techstore.behavior.dto.TrackBehaviorRequest;
import com.project.techstore.behavior.dto.RecommendationTrainingDataPage;
import com.project.techstore.behavior.dto.UserBehaviorResponse;
import com.project.techstore.behavior.service.AiServiceKeyValidator;
import com.project.techstore.behavior.service.BehaviorService;
import com.project.techstore.common.response.ApiResponse;
import com.project.techstore.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/v1/behaviors")
@RequiredArgsConstructor
@Tag(name = "User Behavior Tracking", description = "Thu thập dữ liệu hành vi người dùng phục vụ gợi ý sản phẩm")
public class BehaviorController {

    private final BehaviorService behaviorService;
    private final AiServiceKeyValidator aiServiceKeyValidator;

    @PostMapping("/track")
    @Operation(summary = "Ghi nhận sự kiện hành vi người dùng (VIEW, SEARCH, ADD_TO_CART, WISHLIST, PURCHASE, RATING)")
    public ResponseEntity<ApiResponse<UserBehaviorResponse>> trackBehavior(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody TrackBehaviorRequest request
    ) {
        Long userId = (currentUser != null) ? currentUser.getId() : null;
        UserBehaviorResponse response = behaviorService.track(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/my")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Lấy danh sách 50 hành vi gần nhất của người dùng hiện tại")
    public ResponseEntity<ApiResponse<List<UserBehaviorResponse>>> getMyBehaviors(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(behaviorService.getUserRecentBehaviors(currentUser.getId())));
    }

    @GetMapping("/recommendation-training-data")
    @Operation(summary = "Xuất ma trận tương tác đã tổng hợp, chỉ dành cho AI Service nội bộ")
    public ResponseEntity<ApiResponse<RecommendationTrainingDataPage>> recommendationTrainingData(
            @RequestHeader(name = "X-AI-Service-Key", required = false) String serviceKey,
            @RequestParam @Min(0) @Max(1000) int page,
            @RequestParam @Min(1) @Max(2000) int size,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant asOf
    ) {
        if (!aiServiceKeyValidator.matches(serviceKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AI service authentication failed");
        }
        return ResponseEntity.ok(ApiResponse.success(behaviorService.getTrainingInteractions(page, size, asOf)));
    }

}
