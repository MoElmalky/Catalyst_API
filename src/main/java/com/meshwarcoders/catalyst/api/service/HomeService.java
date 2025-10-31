package com.meshwarcoders.catalyst.api.service;

import com.meshwarcoders.catalyst.api.dto.HomeDashboardResponse;
import com.meshwarcoders.catalyst.api.model.*;
import com.meshwarcoders.catalyst.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomeService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private StudentLessonRepository studentLessonRepository;

    @Autowired
    private ClassRequestRepository classRequestRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public HomeDashboardResponse getDashboardData() {
        // 1. Get all teachers with lesson count
        List<HomeDashboardResponse.TeacherInfo> teachers = teacherRepository.findAll()
                .stream()
                .map(teacher -> {
                    int lessonsCount = lessonRepository.findByTeacherId(teacher.getId()).size();
                    return new HomeDashboardResponse.TeacherInfo(
                            teacher.getId(),
                            teacher.getFullName(),
                            teacher.getEmail(),
                            lessonsCount
                    );
                })
                .collect(Collectors.toList());

        // 2. Get all lessons with student count
        List<HomeDashboardResponse.LessonInfo> lessons = lessonRepository.findAll()
                .stream()
                .map(lesson -> {
                    int studentsCount = studentLessonRepository.findByLessonId(lesson.getId()).size();
                    return new HomeDashboardResponse.LessonInfo(
                            lesson.getId(),
                            lesson.getSubject(),
                            lesson.getTeacher().getFullName(),
                            studentsCount
                    );
                })
                .collect(Collectors.toList());

        // 3. Get recent notifications
        List<HomeDashboardResponse.NotificationInfo> notifications = getRecentNotifications();

        return new HomeDashboardResponse(teachers, lessons, notifications);
    }

    @Transactional(readOnly = true)
    private List<HomeDashboardResponse.NotificationInfo> getRecentNotifications() {
        List<HomeDashboardResponse.NotificationInfo> notifications = new ArrayList<>();

        // Get recent pending requests (last 10)
        List<ClassRequestModel> recentRequests = classRequestRepository
                .findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")))
                .getContent();

        for (ClassRequestModel request : recentRequests) {
            String message = "";
            String type = "";

            switch (request.getStatus()) {
                case PENDING:
                    type = "NEW_REQUEST";
                    message = String.format("%s requested to join %s",
                            request.getStudent().getFullName(),
                            request.getLesson().getSubject());
                    break;
                case APPROVED:
                    type = "APPROVED";
                    message = String.format("%s was approved to join %s",
                            request.getStudent().getFullName(),
                            request.getLesson().getSubject());
                    break;
                case REJECTED:
                    type = "REJECTED";
                    message = String.format("%s was rejected from %s",
                            request.getStudent().getFullName(),
                            request.getLesson().getSubject());
                    break;
            }

            notifications.add(new HomeDashboardResponse.NotificationInfo(
                    type,
                    message,
                    request.getCreatedAt(),
                    request.getId()
            ));
        }

        // Get recently registered students (last 5)
        List<StudentModel> recentStudents = studentRepository
                .findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt")))
                .getContent();

        for (StudentModel student : recentStudents) {
            notifications.add(new HomeDashboardResponse.NotificationInfo(
                    "NEW_STUDENT",
                    String.format("New student registered: %s", student.getFullName()),
                    student.getCreatedAt(),
                    student.getId()
            ));
        }

        // Sort all notifications by timestamp (most recent first)
        notifications.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));

        // Return only the most recent 15 notifications
        return notifications.stream().limit(15).collect(Collectors.toList());
    }
}