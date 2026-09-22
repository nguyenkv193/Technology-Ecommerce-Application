package com.project.techstore.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Danh mục mã lỗi chuẩn hóa toàn hệ thống theo Master Plan.
 */
@Getter
public enum ErrorCode {

    // 400 Bad Request / Validation
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Yêu cầu không hợp lệ"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Dữ liệu đầu vào không hợp lệ"),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Email này đã được đăng ký trên hệ thống"),
    OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "Sản phẩm đã hết hàng trong kho"),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "Refresh token không hợp lệ hoặc đã hết hạn"),

    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Bạn cần đăng nhập để thực hiện thao tác này"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu không chính xác"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Phiên đăng nhập đã hết hạn"),

    // 403 Forbidden
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập tài nguyên này"),

    // 404 Not Found
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy tài nguyên yêu cầu"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục"),
    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu"),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng"),
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy giỏ hàng"),

    // 409 Conflict
    CONFLICT(HttpStatus.CONFLICT, "Dữ liệu bị xung đột"),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi hệ thống, vui lòng thử lại sau"),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi truy vấn cơ sở dữ liệu");

    private final HttpStatus httpStatus;
    private final String defaultMessage;

    ErrorCode(HttpStatus httpStatus, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }
}
