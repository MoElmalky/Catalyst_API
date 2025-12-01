package com.meshwarcoders.catalyst.api.service;

import com.meshwarcoders.catalyst.api.dto.*;
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
import java.util.Optional;
import java.util.UUID;

@Service
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailConfirmRepository emailConfirmRepository;

    @Autowired
    private EmailConfirmationService emailConfirmationService;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private StudentLessonRepository studentLessonRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private NotificationService notificationService;

    @Value("${app.confirm-email.base-url}")
    private String emailConfirmBaseUrl;

    @Transactional
    public AuthResponse signUp(SignUpRequest request) {
        // Check if email already exists
        if (teacherRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered!");
        }

        // Create new teacher
        TeacherModel teacher = new TeacherModel();
        teacher.setFullName(request.getFullName());
        teacher.setEmail(request.getEmail());
        teacher.setPassword(passwordEncoder.encode(request.getPassword()));
        teacher.setEmailConfirmed(false);

        teacher = teacherRepository.save(teacher);

        sendEmailConfirmation(teacher);

        // Generate JWT token
        String token = jwtUtils.generateToken(teacher.getEmail(), "TEACHER");


        return new AuthResponse(token, teacher.getId(), teacher.getFullName(), teacher.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        // Find teacher by email
        TeacherModel teacher = teacherRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password!"));

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), teacher.getPassword())) {
            throw new UnauthorizedException("Invalid email or password!");
        }

        // Generate JWT token
        String token = jwtUtils.generateToken(teacher.getEmail(), "TEACHER");

        return new AuthResponse(token, teacher.getId(), teacher.getFullName(), teacher.getEmail());
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        // Find teacher by email
        TeacherModel teacher = teacherRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("No account found with this email!"));

        // Generate reset token
        String resetCode = UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        teacher.setResetPasswordToken(passwordEncoder.encode(resetCode));
        teacher.setResetPasswordTokenExpiry(LocalDateTime.now().plusMinutes(5));

        String htmlContent = EmailTemplates.passwordResetEmail(resetCode);
        String subject = "Catalyst Password Reset Code";

        Content content = new Content("text/html", htmlContent);

        try {
            emailService.sendEmail(teacher.getEmail(), subject, content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }

        teacherRepository.save(teacher);
    }

    @Transactional(readOnly = true)
    public String verifyResetCode(VerifyResetCodeRequest request) {
        TeacherModel teacher = teacherRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("No account found with this email!"));

        if (!passwordEncoder.matches(request.getCode(), teacher.getResetPasswordToken())) {
            throw new BadRequestException("Invalid reset token!");
        }

        if (teacher.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Reset token has expired!");
        }

        return jwtUtils.generateResetToken(request.getEmail());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String email = jwtUtils.validateResetToken(request.getResetToken());

        if (email == null) {
            throw new BadRequestException("Invalid reset token!");
        }

        TeacherModel teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("No account found!"));

        // Update password
        teacher.setPassword(passwordEncoder.encode(request.getNewPassword()));
        teacher.setResetPasswordToken(null);
        teacher.setResetPasswordTokenExpiry(null);

        teacherRepository.save(teacher);
    }

    @Transactional
    public void confirmEmail(ConfirmEmailRequest request) {
        EmailConfirmModel confirm = emailConfirmRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("No confirmation request found for this email!"));

        if (confirm.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Confirmation code has expired!");
        }

        String providedHash = JwtUtils.sha256Hex(request.getCode());
        if (!providedHash.equals(confirm.getConfirmationCode())) {
            throw new BadRequestException("Invalid confirmation code!");
        }

        emailConfirmationService.confirmEmail(request.getEmail());
        emailConfirmRepository.delete(confirm);
    }

    @Transactional(readOnly = true)
    public java.util.List<JoinRequestDto> getPendingJoinRequests(Long teacherId, Long lessonId) {
        LessonModel lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found!"));

        if (!lesson.getTeacher().getId().equals(teacherId)) {
            throw new UnauthorizedException("You do not own this lesson!");
        }

        java.util.List<StudentLessonModel> pending = studentLessonRepository
                .findByLessonAndStatus(lesson, EnrollmentStatus.PENDING);

        return pending.stream()
                .map(sl -> new JoinRequestDto(
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
    public JoinRequestBulkActionResultDto approveJoinRequests(Long teacherId, Long lessonId, java.util.List<Long> studentIds) {
        return updateJoinRequestsStatus(teacherId, lessonId, studentIds, EnrollmentStatus.APPROVED);
    }

    @Transactional
    public JoinRequestBulkActionResultDto rejectJoinRequests(Long teacherId, Long lessonId, java.util.List<Long> studentIds) {
        return updateJoinRequestsStatus(teacherId, lessonId, studentIds, EnrollmentStatus.REJECTED);
    }

    private JoinRequestBulkActionResultDto updateJoinRequestsStatus(Long teacherId,
                                                                    Long lessonId,
                                                                    java.util.List<Long> studentIds,
                                                                    EnrollmentStatus targetStatus) {
        LessonModel lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found!"));

        if (!lesson.getTeacher().getId().equals(teacherId)) {
            throw new UnauthorizedException("You do not own this lesson!");
        }

        java.util.List<Long> affected = new java.util.ArrayList<>();
        java.util.List<Long> skipped = new java.util.ArrayList<>();

        for (Long studentId : studentIds) {
            StudentModel student = studentRepository.findById(studentId).orElse(null);
            if (student == null) {
                skipped.add(studentId);
                continue;
            }

            java.util.Optional<StudentLessonModel> slOpt = studentLessonRepository.findByLessonAndStudent(lesson, student);
            if (slOpt.isEmpty()) {
                skipped.add(studentId);
                continue;
            }

            StudentLessonModel sl = slOpt.get();
            if (sl.getStatus() != EnrollmentStatus.PENDING) {
                skipped.add(studentId);
                continue;
            }

            sl.setStatus(targetStatus);
            studentLessonRepository.save(sl);
            affected.add(studentId);

            // Send notification to the student
            NotificationType notifType = targetStatus == EnrollmentStatus.APPROVED
                    ? NotificationType.JOIN_REQUEST_APPROVED
                    : NotificationType.JOIN_REQUEST_REJECTED;

            String title = targetStatus == EnrollmentStatus.APPROVED
                    ? "Join request approved"
                    : "Join request rejected";
            String body = targetStatus == EnrollmentStatus.APPROVED
                    ? "Your request to join the class was approved."
                    : "Your request to join the class was rejected.";

            java.util.Map<String, Object> payload = java.util.Map.of(
                    "lessonId", lessonId,
                    "lessonSubject", lesson.getSubject()
            );

            notificationService.sendNotification(student.getEmail(), notifType, title, body, payload);
        }

        return new JoinRequestBulkActionResultDto(lessonId, affected, skipped);
    }

    private void sendEmailConfirmation(TeacherModel teacher) {
        // Generate a long, URL-safe token and store only its hash in the DB
        String token = JwtUtils.generateURLSafeToken();
        String tokenHash = JwtUtils.sha256Hex(token);

        Optional<EmailConfirmModel> existing = emailConfirmRepository.findByEmail(teacher.getEmail());
        EmailConfirmModel confirm = existing.orElseGet(EmailConfirmModel::new);

        confirm.setEmail(teacher.getEmail());
        confirm.setUserType(UserType.TEACHER);
        confirm.setUserId(teacher.getId());
        confirm.setConfirmationCode(tokenHash);
        confirm.setExpiresAt(LocalDateTime.now().plusHours(24));

        emailConfirmRepository.save(confirm);

        // Build a backend confirmation URL that the user can click directly from email
        String encodedEmail = URLEncoder.encode(teacher.getEmail(), StandardCharsets.UTF_8);
        String confirmationLink = String.format("%s?email=%s&code=%s", emailConfirmBaseUrl, encodedEmail, token);

        String htmlContent = EmailTemplates.emailConfirmationEmail(confirmationLink);
        String subject = "Confirm your Catalyst account";
        Content content = new Content("text/html", htmlContent);

        try {
            emailService.sendEmail(teacher.getEmail(), subject, content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to send confirmation email", e);
        }
    }
}
