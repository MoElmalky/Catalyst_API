package com.meshwarcoders.catalyst.api.controller;

import com.meshwarcoders.catalyst.api.dto.request.*;
import com.meshwarcoders.catalyst.api.dto.response.ApiResponse;
import com.meshwarcoders.catalyst.api.dto.response.AuthResponse;
import com.meshwarcoders.catalyst.api.dto.response.RefreshDto;
import com.meshwarcoders.catalyst.api.model.common.UserType;
import com.meshwarcoders.catalyst.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/{userType}/signup")
    public ResponseEntity<ApiResponse> signUp(@Valid @RequestBody SignUpRequest request,
            @PathVariable UserType userType) {
        AuthResponse authResponse = authService.signUp(request, userType);
        ApiResponse response = new ApiResponse(true, "Teacher registered successfully!", authResponse);
        return ResponseEntity.created(URI.create("/" + userType.toString() + "/" + authResponse.id())).body(response);
    }

    @PostMapping("/{userType}/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request,
            @PathVariable UserType userType) {
        AuthResponse authResponse = authService.login(request, userType);
        ApiResponse response = new ApiResponse(true, "Login successful!", authResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userType}/reset-password")
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody Map<String, Object> body,
            @PathVariable UserType userType) {
        authService.forgotPassword(body.get("email").toString(), userType);
        ApiResponse response = new ApiResponse(true,
                "Password reset instructions have been sent to your email!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userType}/reset-password/validate")
    public ResponseEntity<ApiResponse> verifyResetToken(@Valid @RequestBody Map<String, Object> body,
            @PathVariable UserType userType) {
        if (authService.verifyPasswordToken(body.get("token").toString(), userType)) {
            ApiResponse response = new ApiResponse(true, "Email verified successfully!");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{userType}/reset-password/confirm")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request,
            @PathVariable UserType userType) {
        authService.resetPassword(request, userType);
        ApiResponse response = new ApiResponse(true, "Password reset successfully!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userType}/confirm-email")
    public ResponseEntity<ApiResponse> confirmEmail(@Valid @RequestBody Map<String, Object> body,
            @PathVariable UserType userType) {
        authService.confirmEmail(body.get("token").toString(), userType);
        ApiResponse response = new ApiResponse(true, "Email confirmed successfully!");
        return ResponseEntity.ok(response);
    }

    // allow confirmation via GET link from email so backend handles everything
    @GetMapping("/{userType}/confirm-email")
    public ResponseEntity<String> confirmEmailViaLink(@RequestParam("code") String token,
            @PathVariable UserType userType) {
        authService.confirmEmail(token, userType);
        return ResponseEntity.ok("Email confirmed successfully. You can close this page and return to the app.");
    }

    @PostMapping("/{userType}/send-confirm-email")
    public ResponseEntity<ApiResponse> sendConfirmEmail(@Valid @RequestBody Map<String, Object> body,
            @PathVariable UserType userType) {
        authService.sendConfirmEmail(body.get("email").toString(), userType);
        ApiResponse response = new ApiResponse(true, "Confirmation email sent successfully!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userType}/refresh")
    public ResponseEntity<ApiResponse> refreshAccessToken(@RequestBody Map<String, String> body,
            @PathVariable UserType userType) {
        RefreshDto refreshDto = authService.refreshAccessToken(body.get("refreshToken"), userType);
        ApiResponse response = new ApiResponse(true, "Token refreshed successfully!", refreshDto);
        return ResponseEntity.ok(response);
    }

}
