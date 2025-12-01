package com.meshwarcoders.catalyst.api.service;

import com.meshwarcoders.catalyst.api.dto.*;
import com.meshwarcoders.catalyst.api.exception.BadRequestException;
import com.meshwarcoders.catalyst.api.exception.NotFoundException;
import com.meshwarcoders.catalyst.api.exception.UnauthorizedException;
import com.meshwarcoders.catalyst.api.model.*;
import com.meshwarcoders.catalyst.api.model.common.EnrollmentStatus;
import com.meshwarcoders.catalyst.api.model.common.UserType;
import com.meshwarcoders.catalyst.api.repository.EmailConfirmRepository;
import com.meshwarcoders.catalyst.api.repository.LessonRepository;
import com.meshwarcoders.catalyst.api.repository.StudentLessonRepository;
import com.meshwarcoders.catalyst.api.repository.StudentRepository;
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
    private EmailConfirmRepository emailConfirmRepository;

    @Autowired
    private EmailConfirmationService emailConfirmationService;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private StudentLessonRepository studentLessonRepository;

    @Value("${app.confirm-email.student-base-url}")
    private String emailConfirmBaseUrl;

    @Transactional
    public AuthResponse signUp(SignUpRequest request) {
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered!");
        }

        StudentModel student = new StudentModel();
        student.setFullName(request.getFullName());
        student.setEmail(request.getEmail());
        student.setPassword(passwordEncoder.encode(request.getPassword()));
        student.setEmailConfirmed(false);

        student = studentRepository.save(student);

        sendEmailConfirmation(student);

        String token = jwtUtils.generateToken(student.getEmail());
        return new AuthResponse(token, student.getId(), student.getFullName(), student.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        StudentModel student = studentRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password!"));

        if (!passwordEncoder.matches(request.getPassword(), student.getPassword())) {
            throw new UnauthorizedException("Invalid email or password!");
        }

        String token = jwtUtils.generateToken(student.getEmail());
        return new AuthResponse(token, student.getId(), student.getFullName(), student.getEmail());
    }

    @Transactional
    public JoinRequestDto createJoinRequest(Long lessonId, String studentEmail) {
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

        StudentSummaryDto studentSummary = new StudentSummaryDto(student.getId(), student.getFullName(), student.getEmail());
        return new JoinRequestDto(sl.getId(), lesson.getId(), studentSummary, sl.getStatus());
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
    public AuthResponse getProfile(String email, String token) {
        StudentModel student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Student not found!"));

        return new AuthResponse(
                token,
                student.getId(),
                student.getFullName(),
                student.getEmail()
        );
    }

    private void sendEmailConfirmation(StudentModel student) {
        String token = JwtUtils.generateURLSafeToken();
        String tokenHash = JwtUtils.sha256Hex(token);

        Optional<EmailConfirmModel> existing = emailConfirmRepository.findByEmail(student.getEmail());
        EmailConfirmModel confirm = existing.orElseGet(EmailConfirmModel::new);

        confirm.setEmail(student.getEmail());
        confirm.setUserType(UserType.STUDENT);
        confirm.setUserId(student.getId());
        confirm.setConfirmationCode(tokenHash);
        confirm.setExpiresAt(LocalDateTime.now().plusHours(24));

        emailConfirmRepository.save(confirm);

        String encodedEmail = URLEncoder.encode(student.getEmail(), StandardCharsets.UTF_8);
        String confirmationLink = String.format("%s?email=%s&code=%s", emailConfirmBaseUrl, encodedEmail, token);

        String htmlContent = EmailTemplates.emailConfirmationEmail(confirmationLink);
        String subject = "Confirm your Catalyst account";
        Content content = new Content("text/html", htmlContent);

        try {
            emailService.sendEmail(student.getEmail(), subject, content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to send confirmation email", e);
        }
    }
}