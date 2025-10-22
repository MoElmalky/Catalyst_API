package com.meshwarcoders.catalyst.api.service;

import com.meshwarcoders.catalyst.api.dto.ClassRequestResponse;
import com.meshwarcoders.catalyst.api.dto.JoinClassRequest;
import com.meshwarcoders.catalyst.api.model.*;
import com.meshwarcoders.catalyst.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassRequestService {

    @Autowired
    private ClassRequestRepository classRequestRepository;

    @Autowired
    private StudentLessonRepository studentLessonRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private StudentRepository studentRepository;

    // ================== STUDENT: REQUEST TO JOIN CLASS ==================
    @Transactional
    public ClassRequestResponse requestToJoinClass(Long studentId, JoinClassRequest request) {
        // Get student
        StudentModel student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found!"));

        // Get lesson
        LessonModel lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson not found!"));

        // Check if student is already enrolled
        if (studentLessonRepository.existsByStudentAndLesson(student, lesson)) {
            throw new RuntimeException("You are already enrolled in this class!");
        }

        // Check if there's already a pending request
        if (classRequestRepository.existsByStudentAndLessonAndStatus(
                student, lesson, ClassRequestModel.RequestStatus.PENDING)) {
            throw new RuntimeException("You already have a pending request for this class!");
        }

        // Create new request
        ClassRequestModel classRequest = new ClassRequestModel();
        classRequest.setStudent(student);
        classRequest.setLesson(lesson);
        classRequest.setMessage(request.getMessage());
        classRequest.setStatus(ClassRequestModel.RequestStatus.PENDING);

        classRequest = classRequestRepository.save(classRequest);

        return new ClassRequestResponse(classRequest);
    }

    // ================== STUDENT: GET MY REQUESTS ==================
    @Transactional(readOnly = true)
    public List<ClassRequestResponse> getMyRequests(Long studentId) {
        List<ClassRequestModel> requests = classRequestRepository.findByStudentId(studentId);
        return requests.stream()
                .map(ClassRequestResponse::new)
                .collect(Collectors.toList());
    }

    // ================== TEACHER: GET PENDING REQUESTS FOR MY LESSONS ==================
    @Transactional(readOnly = true)
    public List<ClassRequestResponse> getPendingRequestsForTeacher(Long teacherId) {
        // Get all teacher's lessons
        List<LessonModel> lessons = lessonRepository.findByTeacherId(teacherId);

        // Get all pending requests for these lessons
        return lessons.stream()
                .flatMap(lesson -> classRequestRepository
                        .findByLessonIdAndStatus(lesson.getId(), ClassRequestModel.RequestStatus.PENDING)
                        .stream())
                .map(ClassRequestResponse::new)
                .collect(Collectors.toList());
    }

    // ================== TEACHER: APPROVE REQUEST ==================
    @Transactional
    public void approveRequest(Long teacherId, Long requestId) {
        ClassRequestModel request = classRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found!"));

        // Verify teacher owns this lesson
        if (!request.getLesson().getTeacher().getId().equals(teacherId)) {
            throw new RuntimeException("You are not authorized to approve this request!");
        }

        // Check if request is still pending
        if (request.getStatus() != ClassRequestModel.RequestStatus.PENDING) {
            throw new RuntimeException("This request has already been processed!");
        }

        // Update request status
        request.setStatus(ClassRequestModel.RequestStatus.APPROVED);
        request.setRespondedAt(LocalDateTime.now());
        classRequestRepository.save(request);

        // Add student to lesson
        StudentLessonModel studentLesson = new StudentLessonModel();
        studentLesson.setStudent(request.getStudent());
        studentLesson.setLesson(request.getLesson());
        studentLessonRepository.save(studentLesson);
    }

    // ================== TEACHER: REJECT REQUEST ==================
    @Transactional
    public void rejectRequest(Long teacherId, Long requestId) {
        ClassRequestModel request = classRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found!"));

        // Verify teacher owns this lesson
        if (!request.getLesson().getTeacher().getId().equals(teacherId)) {
            throw new RuntimeException("You are not authorized to reject this request!");
        }

        // Check if request is still pending
        if (request.getStatus() != ClassRequestModel.RequestStatus.PENDING) {
            throw new RuntimeException("This request has already been processed!");
        }

        // Update request status
        request.setStatus(ClassRequestModel.RequestStatus.REJECTED);
        request.setRespondedAt(LocalDateTime.now());
        classRequestRepository.save(request);
    }

    // ================== TEACHER: REMOVE STUDENT FROM CLASS ==================
    @Transactional
    public void removeStudentFromClass(Long teacherId, Long lessonId, Long studentId) {
        // Get lesson
        LessonModel lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found!"));

        // Verify teacher owns this lesson
        if (!lesson.getTeacher().getId().equals(teacherId)) {
            throw new RuntimeException("You are not authorized to remove students from this class!");
        }

        // Get student
        StudentModel student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found!"));

        // Find enrollment
        StudentLessonModel enrollment = studentLessonRepository.findByStudentAndLesson(student, lesson)
                .orElseThrow(() -> new RuntimeException("Student is not enrolled in this class!"));

        // Remove enrollment
        studentLessonRepository.delete(enrollment);
    }

    // ================== TEACHER: GET ALL STUDENTS IN CLASS ==================
    @Transactional(readOnly = true)
    public List<StudentModel> getStudentsInClass(Long teacherId, Long lessonId) {
        // Get lesson
        LessonModel lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found!"));

        // Verify teacher owns this lesson
        if (!lesson.getTeacher().getId().equals(teacherId)) {
            throw new RuntimeException("You are not authorized to view this class!");
        }

        // Get all enrollments
        List<StudentLessonModel> enrollments = studentLessonRepository.findByLessonId(lessonId);

        // Return students
        return enrollments.stream()
                .map(StudentLessonModel::getStudent)
                .collect(Collectors.toList());
    }
}