package com.meshwarcoders.catalyst.api.service;

import com.meshwarcoders.catalyst.api.dto.request.*;
import com.meshwarcoders.catalyst.api.dto.response.AuthResponse;
import com.meshwarcoders.catalyst.api.dto.response.JoinDto;
import com.meshwarcoders.catalyst.api.dto.response.JoinStudentDto;
import com.meshwarcoders.catalyst.api.dto.response.StudentSummaryDto;
import com.meshwarcoders.catalyst.api.exception.BadRequestException;
import com.meshwarcoders.catalyst.api.exception.NotFoundException;
import com.meshwarcoders.catalyst.api.exception.UnauthorizedException;
import com.meshwarcoders.catalyst.api.model.*;
import com.meshwarcoders.catalyst.api.model.common.EnrollmentStatus;
import com.meshwarcoders.catalyst.api.model.common.NotificationType;
import com.meshwarcoders.catalyst.api.model.common.UserType;
import com.meshwarcoders.catalyst.api.repository.*;
import com.meshwarcoders.catalyst.api.security.JwtUtils;
import com.meshwarcoders.catalyst.util.EmailTemplates;
import com.sendgrid.helpers.mail.objects.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EmailService emailService;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private StudentLessonRepository studentLessonRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public List<JoinStudentDto> getPendingJoinRequests(Long teacherId, Long lessonId) {
        LessonModel lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found!"));

        if (!lesson.getTeacher().getId().equals(teacherId)) {
            throw new UnauthorizedException("You do not own this lesson!");
        }

        List<StudentLessonModel> pending = studentLessonRepository
                .findByLessonAndStatus(lesson, EnrollmentStatus.PENDING);

        return pending.stream()
                .map(sl -> new JoinStudentDto(
                        sl.getId(),
                        lesson.getId(),
                        new StudentSummaryDto(
                                sl.getStudent().getId(),
                                sl.getStudent().getFullName(),
                                sl.getStudent().getEmail()
                        ),
                        sl.getStatus()))
                .toList();
    }

    @Transactional
    public JoinDto approveJoinRequests(Long teacherId, Long lessonId, List<Long> studentLessonIds) {
        return updateJoinRequestsStatus(teacherId, lessonId, studentLessonIds, EnrollmentStatus.APPROVED);
    }

    @Transactional
    public JoinDto rejectJoinRequests(Long teacherId, Long lessonId, List<Long> studentLessonIds) {
        return updateJoinRequestsStatus(teacherId, lessonId, studentLessonIds, EnrollmentStatus.REJECTED);
    }

    private JoinDto updateJoinRequestsStatus(Long teacherId,
                                             Long lessonId,
                                             List<Long> studentLessonIds,
                                             EnrollmentStatus targetStatus) {
        LessonModel lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found!"));

        if (!lesson.getTeacher().getId().equals(teacherId)) {
            throw new UnauthorizedException("You do not own this lesson!");
        }

        List<Long> affected = new ArrayList<>();
        List<Long> skipped = new ArrayList<>();

        for (Long studentLessonId : studentLessonIds) {

            Optional<StudentLessonModel> slOpt = studentLessonRepository.findById(studentLessonId);
            if (slOpt.isEmpty()) {
                skipped.add(studentLessonId);
                continue;
            }

            StudentLessonModel sl = slOpt.get();
            if (sl.getStatus() != EnrollmentStatus.PENDING) {
                skipped.add(studentLessonId);
                continue;
            }

            sl.setStatus(targetStatus);
            studentLessonRepository.save(sl);
            affected.add(studentLessonId);
        }

        return new JoinDto(lessonId, affected, skipped);
    }
}
