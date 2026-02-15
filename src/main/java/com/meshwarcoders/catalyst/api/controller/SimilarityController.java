package com.meshwarcoders.catalyst.api.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.meshwarcoders.catalyst.api.dto.response.SimilarityResponse;
import com.meshwarcoders.catalyst.api.dto.request.SimilarityRequest;
import com.meshwarcoders.catalyst.api.service.SimilarityService;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class SimilarityController {

    private final SimilarityService similarityService;

    public SimilarityController(SimilarityService similarityService) {
        this.similarityService = similarityService;
    }

    @PostMapping("/similarity")
    public SimilarityResponse similarity(@RequestBody SimilarityRequest request) {
        System.out.println("Reached SimilarityController!");
        // إرسال الـ request كامل للدالة الجديدة
        return similarityService.calculate(request);
    }
}
