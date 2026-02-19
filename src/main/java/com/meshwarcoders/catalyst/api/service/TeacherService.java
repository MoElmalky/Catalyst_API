package com.meshwarcoders.catalyst.api.service;

import com.meshwarcoders.catalyst.api.dto.request.*;
import com.meshwarcoders.catalyst.api.dto.response.*;
import com.meshwarcoders.catalyst.api.exception.NotFoundException;
import com.meshwarcoders.catalyst.api.exception.UnauthorizedException;
import com.meshwarcoders.catalyst.api.model.*;
import com.meshwarcoders.catalyst.api.model.common.EnrollmentStatus;
import com.meshwarcoders.catalyst.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TeacherService {

        @Autowired
        private LessonRepository lessonRepository;

        @Autowired
        private StudentLessonRepository studentLessonRepository;

        @Autowired
        private NotificationService notificationService;

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
                                                                sl.getStudent().getEmail()),
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

                        // Notify Student
                        String title = targetStatus == EnrollmentStatus.APPROVED ? "Join Request Approved"
                                        : "Join Request Rejected";
                        String body = targetStatus == EnrollmentStatus.APPROVED
                                        ? "Your request to join " + lesson.getSubject() + " has been approved!"
                                        : "Your request to join " + lesson.getSubject() + " has been rejected.";

                        notificationService.notifyStudent(
                                        sl.getStudent(),
                                        title,
                                        body,
                                        Map.of(
                                                        "type", "JOIN_STATUS",
                                                        "lessonId", lesson.getId().toString(),
                                                        "status", targetStatus.name()));
                }

                return new JoinDto(lessonId, affected, skipped);
        }

        @Transactional(readOnly = true)
        public List<JoinStudentDto> getAllPendingJoinRequests(Long teacherId) {
                List<StudentLessonModel> pending = studentLessonRepository
                                .findByLessonTeacherIdAndStatus(teacherId, EnrollmentStatus.PENDING);

                return pending.stream()
                                .map(sl -> new JoinStudentDto(
                                                sl.getId(),
                                                sl.getLesson().getId(),
                                                new StudentSummaryDto(
                                                                sl.getStudent().getId(),
                                                                sl.getStudent().getFullName(),
                                                                sl.getStudent().getEmail()),
                                                sl.getStatus()))
                                .toList();
        }
}
