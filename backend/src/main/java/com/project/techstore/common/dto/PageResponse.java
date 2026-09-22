package com.project.techstore.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Chuẩn hóa cấu trúc phân trang toàn hệ thống (Pagination DTO).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Cấu trúc phản hồi dữ liệu phân trang")
public class PageResponse<T> {

    @Schema(description = "Danh sách dữ liệu trong trang hiện tại")
    private List<T> content;

    @Schema(description = "Số trang hiện tại (bắt đầu từ 0)", example = "0")
    private int page;

    @Schema(description = "Kích thước số phần tử trên một trang", example = "10")
    private int size;

    @Schema(description = "Tổng số phần tử trên toàn bộ hệ thống", example = "50")
    private long totalElements;

    @Schema(description = "Tổng số trang", example = "5")
    private int totalPages;

    @Schema(description = "Đã là trang cuối cùng hay chưa", example = "false")
    private boolean last;

    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
