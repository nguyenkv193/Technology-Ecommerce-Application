package com.project.techstore.behavior.controller;

import com.project.techstore.behavior.dto.TrackBehaviorRequest;
import com.project.techstore.behavior.dto.UserBehaviorResponse;
import com.project.techstore.behavior.dto.UserInteractionExportDto;
import com.project.techstore.behavior.service.BehaviorService;
import com.project.techstore.common.response.ApiResponse;
import com.project.techstore.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/behaviors")
@RequiredArgsConstructor
@Tag(name = "User Behavior Tracking", description = "Thu thập dữ liệu hành vi người dùng & Xuất ma trận tương tác cho AI Recommendation")
public class BehaviorController {

    private final BehaviorService behaviorService;

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

    @GetMapping("/export-interactions")
    @Operation(summary = "Xuất ma trận tương tác User-Item (Implicit Feedback Matrix) cho Python FastAPI AI Service")
    public ResponseEntity<ApiResponse<List<UserInteractionExportDto>>> exportInteractions() {
        return ResponseEntity.ok(ApiResponse.success(behaviorService.exportInteractionsForAi()));
    }
}
