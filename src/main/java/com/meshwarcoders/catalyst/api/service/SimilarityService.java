package com.meshwarcoders.catalyst.api.service;

import com.meshwarcoders.catalyst.api.dto.request.SimilarityRequest;
import com.meshwarcoders.catalyst.api.dto.response.SimilarityResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

@Service
public class SimilarityService {

    private final RestTemplate restTemplate;

    public SimilarityService() {
        this.restTemplate = new RestTemplate();
    }

    public SimilarityResponse calculate(SimilarityRequest request) {
        String flaskUrl = "http://127.0.0.1:5000/similarity";

        // إعداد الجسم للطلب
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("studentId", request.getStudentId());
        requestBody.put("questionId", request.getQuestionId());
        requestBody.put("studentAnswer", request.getStudentAnswer());
        requestBody.put("teacherAnswer", request.getTeacherAnswer());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> responseEntity = restTemplate.exchange(
                flaskUrl,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        Map<String, Object> body = responseEntity.getBody();

        // جلب قيمة similarity من الـ Flask response
        double similarity = ((Number) body.get("similarity")).doubleValue();

        // نحسب score و passed حسب similarity
        double score = similarity * 100; // مثال: تحويل similarity لدرجة من 0 لـ 100
        boolean passed = similarity >= 0.6; // مثال: النجاح لو similarity >= 0.6

        return new SimilarityResponse(
                request.getStudentId(),
                request.getQuestionId(),
                similarity,
                score,
                passed
        );
    }
}
