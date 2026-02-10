package com.meshwarcoders.catalyst.api.service;

import com.google.firebase.messaging.*;
import com.meshwarcoders.catalyst.api.model.StudentModel;
import com.meshwarcoders.catalyst.api.model.TeacherModel;
import com.meshwarcoders.catalyst.api.model.UserDeviceModel;
import com.meshwarcoders.catalyst.api.repository.UserDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private UserDeviceRepository userDeviceRepository;

    public void notifyTeacher(TeacherModel teacher, String title, String body, Map<String, String> data) {
        List<UserDeviceModel> devices = userDeviceRepository.findByTeacher(teacher);
        sendMulticast(devices, title, body, data);
    }

    public void notifyStudent(StudentModel student, String title, String body, Map<String, String> data) {
        List<UserDeviceModel> devices = userDeviceRepository.findByStudent(student);
        sendMulticast(devices, title, body, data);
    }

    public void notifyStudents(List<StudentModel> students, String title, String body, Map<String, String> data) {
        List<UserDeviceModel> devices = userDeviceRepository.findByStudentIn(students);
        sendMulticast(devices, title, body, data);
    }

    private void sendMulticast(List<UserDeviceModel> devices, String title, String body, Map<String, String> data) {
        if (devices.isEmpty())
            return;

        List<String> tokens = devices.stream()
                .map(UserDeviceModel::getToken)
                .filter(token -> token != null && !token.isBlank())
                .collect(Collectors.toList());

        if (tokens.isEmpty()) {
            System.out.println("No valid FCM tokens found for target devices.");
            return;
        }

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .addAllTokens(tokens)
                .putAllData(data)
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            System.out.println("Successfully sent " + response.getSuccessCount() + " messages");
        } catch (FirebaseMessagingException e) {
            System.err.println("Error sending multicat notification: " + e.getMessage());
        }
    }
}
