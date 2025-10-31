package com.meshwarcoders.catalyst.api.controller;

import com.meshwarcoders.catalyst.api.dto.ApiResponse;
import com.meshwarcoders.catalyst.api.dto.HomeDashboardResponse;
import com.meshwarcoders.catalyst.api.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/home")
@CrossOrigin(origins = "*")
public class HomeController {

    @Autowired
    private HomeService homeService;

    // ================== GET HOME DASHBOARD DATA ==================
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse> getDashboard() {
        try {
            HomeDashboardResponse dashboard = homeService.getDashboardData();
            return ResponseEntity.ok(
                    new ApiResponse(true, "Dashboard data fetched successfully!", dashboard));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}