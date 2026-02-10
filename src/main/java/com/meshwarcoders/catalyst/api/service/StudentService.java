package com.meshwarcoders.catalyst.api.service;

import java.util.Map;

import com.meshwarcoders.catalyst.api.dto.response.JoinStudentDto;
import com.meshwarcoders.catalyst.api.dto.response.StudentSummaryDto;
import com.meshwarcoders.catalyst.api.exception.BadRequestException;
import com.meshwarcoders.catalyst.api.exception.NotFoundException;
import com.meshwarcoders.catalyst.api.model.*;
import com.meshwarcoders.catalyst.api.model.common.EnrollmentStatus;
import com.meshwarcoders.catalyst.api.repository.LessonRepository;
import com.meshwarcoders.catalyst.api.repository.StudentLessonRepository;
import com.meshwarcoders.catalyst.api.repository.StudentRepository;
import com.meshwarcoders.catalyst.api.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EmailService emailService;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private StudentLessonRepository studentLessonRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public JoinStudentDto createJoinRequest(Long lessonId, String studentEmail) {
        StudentModel student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new NotFoundException("Student not found!"));

        LessonModel lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found!"));

        if (studentLessonRepository.existsByLessonAndStudent(lesson, student)) {
            throw new BadRequestException("You have already requested or joined this class.");
        }

        StudentLessonModel sl = new StudentLessonModel();
        sl.setLesson(lesson);
        sl.setStudent(student);
        sl.setStatus(EnrollmentStatus.PENDING);

        sl = studentLessonRepository.save(sl);

        // Notify Teacher
        notificationService.notifyTeacher(
                lesson.getTeacher(),
                "New Join Request",
                student.getFullName() + " wants to join " + lesson.getSubject(),
                Map.of(
                        "type", "JOIN_REQUEST",
                        "lessonId", lesson.getId().toString(),
                        "studentId", student.getId().toString()));

        StudentSummaryDto studentSummary = new StudentSummaryDto(student.getId(), student.getFullName(),
                student.getEmail());
        return new JoinStudentDto(sl.getId(), lesson.getId(), studentSummary, sl.getStatus());
    }
}