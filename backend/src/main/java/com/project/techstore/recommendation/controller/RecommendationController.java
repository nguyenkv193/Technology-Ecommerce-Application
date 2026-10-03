package com.project.techstore.recommendation.controller;

import com.project.techstore.common.response.ApiResponse;
import com.project.techstore.recommendation.dto.RecommendationResponse;
import com.project.techstore.recommendation.service.RecommendationService;
import com.project.techstore.user.entity.User;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
@Validated
public class RecommendationController {
    private final RecommendationService recommendations;

    @GetMapping("/me")
    public ApiResponse<RecommendationResponse> forCurrentUser(@AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "8") @Min(1) @Max(50) int limit) {
        return ApiResponse.success(recommendations.forUser(user == null ? null : user.getId(), limit));
    }

    @GetMapping("/similar/{productId}")
    public ApiResponse<RecommendationResponse> similar(@PathVariable @Min(1) Long productId,
            @RequestParam(defaultValue = "4") @Min(1) @Max(50) int limit) {
        return ApiResponse.success(recommendations.similar(productId, limit));
    }
}
