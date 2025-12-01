package com.meshwarcoders.catalyst.api.service;

import com.meshwarcoders.catalyst.api.dto.CreateExamRequest;
import com.meshwarcoders.catalyst.api.dto.ExamQuestionRequest;
import com.meshwarcoders.catalyst.api.dto.ExamSummaryDto;
import com.meshwarcoders.catalyst.api.exception.NotFoundException;
import com.meshwarcoders.catalyst.api.exception.UnauthorizedException;
import com.meshwarcoders.catalyst.api.model.*;
import com.meshwarcoders.catalyst.api.model.common.EnrollmentStatus;
import com.meshwarcoders.catalyst.api.model.common.NotificationType;
import com.meshwarcoders.catalyst.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamQuestionRepository examQuestionRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentLessonRepository studentLessonRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public ExamSummaryDto createExam(Long teacherId, Long lessonId, CreateExamRequest request) {
        LessonModel lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found!"));

        if (!lesson.getTeacher().getId().equals(teacherId)) {
            throw new UnauthorizedException("You do not own this lesson!");
        }

        ExamModel exam = new ExamModel();
        exam.setLesson(lesson);
        exam.setExamName(request.getExamName());
        exam.setMaxGrade(request.getMaxGrade());

        if (request.getExamDateTime() != null && !request.getExamDateTime().isBlank()) {
            exam.setExamDateTime(LocalDateTime.parse(request.getExamDateTime()));
        }
        exam.setDurationMinutes(request.getDurationMinutes());

        exam = examRepository.save(exam);

        if (request.getQuestions() != null) {
            for (ExamQuestionRequest q : request.getQuestions()) {
                ExamQuestionModel qm = new ExamQuestionModel();
                qm.setExam(exam);
                qm.setText(q.getText());
                qm.setType(q.getType());
                qm.setOptions(q.getOptions());
                qm.setCorrectOptionIndex(q.getCorrectOptionIndex());
                qm.setMaxPoints(q.getMaxPoints());
                examQuestionRepository.save(qm);
            }
        }

        // Notify all approved students in this lesson about the new exam
        List<StudentLessonModel> approvedStudents = studentLessonRepository
                .findByLessonAndStatus(lesson, EnrollmentStatus.APPROVED);

        String dateTimeString = exam.getExamDateTime() != null ? exam.getExamDateTime().toString() : null;
        for (StudentLessonModel sl : approvedStudents) {
            StudentModel student = sl.getStudent();
            java.util.Map<String, Object> payload = java.util.Map.of(
                    "lessonId", lesson.getId(),
                    "lessonSubject", lesson.getSubject(),
                    "examId", exam.getId(),
                    "examName", exam.getExamName(),
                    "examDateTime", dateTimeString
            );

            notificationService.sendNotification(
                    student.getEmail(),
                    NotificationType.EXAM_CREATED,
                    "New exam created",
                    "A new exam has been scheduled for your class.",
                    payload
            );
        }

        return toSummaryDto(exam);
    }

    @Transactional(readOnly = true)
    public List<ExamSummaryDto> getExamsForLessonAsTeacher(Long teacherId, Long lessonId) {
        LessonModel lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found!"));

        if (!lesson.getTeacher().getId().equals(teacherId)) {
            throw new UnauthorizedException("You do not own this lesson!");
        }

        return examRepository.findByLesson(lesson)
                .stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExamSummaryDto> getExamsForLessonAsStudent(String studentEmail, Long lessonId) {
        StudentModel student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new NotFoundException("Student not found!"));

        LessonModel lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found!"));

        StudentLessonModel sl = studentLessonRepository.findByLessonAndStudent(lesson, student)
                .orElseThrow(() -> new UnauthorizedException("You are not enrolled in this lesson!"));

        if (sl.getStatus() != EnrollmentStatus.APPROVED) {
            throw new UnauthorizedException("You are not approved in this lesson!");
        }

        return examRepository.findByLesson(lesson)
                .stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());
    }

    private ExamSummaryDto toSummaryDto(ExamModel exam) {
        String dateTimeString = exam.getExamDateTime() != null ? exam.getExamDateTime().toString() : null;

        return new ExamSummaryDto(
                exam.getId(),
                exam.getLesson().getId(),
                exam.getExamName(),
                exam.getMaxGrade(),
                dateTimeString,
                exam.getDurationMinutes()
        );
    }
}