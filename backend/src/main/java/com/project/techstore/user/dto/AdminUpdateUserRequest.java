package com.project.techstore.user.dto;

import com.project.techstore.user.entity.Role;
import com.project.techstore.user.entity.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yêu cầu cập nhật vai trò hoặc trạng thái tài khoản bởi Admin")
public class AdminUpdateUserRequest {

    @Schema(description = "Vai trò mới (USER, STAFF, ADMIN)", example = "STAFF")
    private Role role;

    @Schema(description = "Trạng thái mới (ACTIVE, INACTIVE, BLOCKED)", example = "BLOCKED")
    private UserStatus status;
}
