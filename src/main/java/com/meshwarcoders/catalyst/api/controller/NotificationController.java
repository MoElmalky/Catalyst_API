package com.meshwarcoders.catalyst.api.controller;

import com.meshwarcoders.catalyst.api.dto.ApiResponse;
import com.meshwarcoders.catalyst.api.exception.UnauthorizedException;
import com.meshwarcoders.catalyst.api.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse> getMyNotifications(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Missing or invalid token!");
        }

        String email = authentication.getName();
        var list = notificationService.getNotificationsForUser(email);
        return ResponseEntity.ok(new ApiResponse(true,
                "Notifications fetched successfully!", list));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<ApiResponse> markAsRead(@PathVariable Long id,
                                                  Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Missing or invalid token!");
        }

        String email = authentication.getName();
        notificationService.markAsRead(id, email);
        return ResponseEntity.ok(new ApiResponse(true,
                "Notification marked as read!"));
    }
}