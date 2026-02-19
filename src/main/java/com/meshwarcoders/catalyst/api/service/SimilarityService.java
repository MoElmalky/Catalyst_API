package com.meshwarcoders.catalyst.api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SimilarityService {

    private final RestTemplate restTemplate;

    public SimilarityService() {
        this.restTemplate = new RestTemplate();
    }

    public List<Double> calculate(List<Map<String, String>> pairs) {
        String flaskUrl = "https://exclamational-unlyrical-brittani.ngrok-free.dev/similarity";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<Map<String, String>>> requestEntity = new HttpEntity<>(pairs, headers);

        ResponseEntity<Map> responseEntity = restTemplate.exchange(
                flaskUrl,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        Map<String, List> body = responseEntity.getBody();

        return body.get("similarity");
    }
}
