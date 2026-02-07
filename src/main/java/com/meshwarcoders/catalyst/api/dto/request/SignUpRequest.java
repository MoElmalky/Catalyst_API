package com.meshwarcoders.catalyst.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(@NotBlank(message = "username is required") String username,
                            @NotBlank(message = "Email is required")
                            @Email(message = "Email should be valid")
                            String email,
                            @NotBlank(message = "Password is required")
                            @Size(min = 6, max = 20, message = "Password must be between 6-20 characters")
                            String password,
                            DeviceDataRequest deviceData) {

}
