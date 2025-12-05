package com.meshwarcoders.catalyst.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meshwarcoders.catalyst.api.dto.NotificationDto;
import com.meshwarcoders.catalyst.api.model.NotificationModel;
import com.meshwarcoders.catalyst.api.model.common.NotificationType;
import com.meshwarcoders.catalyst.api.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public void sendNotification(String recipientEmail,
                                 NotificationType type,
                                 String title,
                                 String body,
                                 Map<String, Object> payload) {
        NotificationModel n = new NotificationModel();
        n.setRecipientEmail(recipientEmail);
        n.setType(type);
        n.setTitle(title);
        n.setBody(body);

        if (payload != null && !payload.isEmpty()) {
            try {
                n.setPayload(objectMapper.writeValueAsString(payload));
            } catch (JsonProcessingException e) {
                n.setPayload(null);
            }
        }

        notificationRepository.save(n);
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsForUser(String email) {
        return notificationRepository.findByRecipientEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(this::toDto).toList();
    }

    @Transactional
    public void markAsRead(Long id, String email) {
        NotificationModel n = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!n.getRecipientEmail().equals(email)) {
            throw new RuntimeException("You are not allowed to modify this notification");
        }

        if (!n.isRead()) {
            n.setRead(true);
            notificationRepository.save(n);
        }
    }

    private NotificationDto toDto(NotificationModel n) {
        Map<String, Object> payloadMap = Collections.emptyMap();
        if (n.getPayload() != null) {
            try {
                payloadMap = objectMapper.readValue(n.getPayload(), Map.class);
            } catch (Exception ignored) {
            }
        }

        return new NotificationDto(
                n.getId(),
                n.getType(),
                n.getTitle(),
                n.getBody(),
                payloadMap,
                n.isRead(),
                n.getCreatedAt()
        );
    }
}