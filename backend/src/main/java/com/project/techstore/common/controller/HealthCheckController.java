package com.project.techstore.common.controller;

import com.project.techstore.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health Check", description = "Kiểm tra trạng thái hoạt động của hệ thống")
public class HealthCheckController {

    @GetMapping
    @Operation(summary = "Kiểm tra sức khỏe Backend", description = "Trả về trạng thái hoạt động và cấu hình cơ bản của hệ thống")
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        Map<String, Object> healthInfo = new LinkedHashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("service", "TechStore E-Commerce & AI Backend");
        healthInfo.put("version", "1.0.0");
        healthInfo.put("environment", "development");
        healthInfo.put("currentTime", Instant.now().toString());

        return ResponseEntity.ok(ApiResponse.success("Hệ thống TechStore đang hoạt động ổn định", healthInfo));
    }
}
