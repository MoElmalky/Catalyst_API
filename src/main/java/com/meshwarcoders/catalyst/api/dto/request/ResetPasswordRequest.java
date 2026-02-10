package com.meshwarcoders.catalyst.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(@NotBlank(message = "Reset token is required") String token,
                                   @NotBlank(message = "New password is required")
                                   @Size(min = 6, max = 20, message = "Password must be between 6-20 characters")
                                   String newPassword) {
}
