package com.meshwarcoders.catalyst.api.service;

import com.meshwarcoders.catalyst.api.dto.request.*;
import com.meshwarcoders.catalyst.api.dto.response.AuthResponse;
import com.meshwarcoders.catalyst.api.dto.response.RefreshDto;
import com.meshwarcoders.catalyst.api.exception.BadRequestException;
import com.meshwarcoders.catalyst.api.exception.NotFoundException;
import com.meshwarcoders.catalyst.api.exception.UnauthorizedException;
import com.meshwarcoders.catalyst.api.model.RefreshTokenModel;
import com.meshwarcoders.catalyst.api.model.StudentModel;
import com.meshwarcoders.catalyst.api.model.TeacherModel;
import com.meshwarcoders.catalyst.api.model.UserDeviceModel;
import com.meshwarcoders.catalyst.api.model.common.AuthUser;
import com.meshwarcoders.catalyst.api.model.common.UserType;
import com.meshwarcoders.catalyst.api.repository.RefreshTokenRepository;
import com.meshwarcoders.catalyst.api.repository.StudentRepository;
import com.meshwarcoders.catalyst.api.repository.TeacherRepository;
import com.meshwarcoders.catalyst.api.repository.UserDeviceRepository;
import com.meshwarcoders.catalyst.api.security.JwtUtils;
import com.meshwarcoders.catalyst.util.EmailTemplates;
import com.sendgrid.helpers.mail.objects.Content;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {
    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserDeviceRepository userDeviceRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EmailService emailService;


    @Transactional
    public AuthResponse signUp(SignUpRequest request, UserType userType) {

        AuthUser user;

        RefreshTokenModel refreshTokenModel = new RefreshTokenModel();

        switch (userType) {
            case TEACHER -> {
                if (teacherRepository.existsByEmail(request.email())) {
                    throw new BadRequestException("A Teacher with this email already registered!");
                }
                user = new TeacherModel();
            }
            case STUDENT -> {
                if (studentRepository.existsByEmail(request.email())) {
                    throw new BadRequestException("A Student with this email already registered!");
                }
                user = new StudentModel();
            }
            default -> throw new BadRequestException("UserType is invalid!");
        }

        user.setEmail(request.email());
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));

        DeviceDataRequest deviceData = request.deviceData();
        UserDeviceModel userDevice = userDeviceRepository.findByToken(deviceData.fcmToken())
                .orElseGet(() -> {
                    UserDeviceModel d = new UserDeviceModel();
                    d.setToken(deviceData.fcmToken());
                    d.setDeviceType(deviceData.deviceType());
                    d.setDeviceId(deviceData.deviceId());
                    return d;
        });

        userDevice.setLastActive(Instant.now());

        // Generate Confirmation Token
        String confirmToken = jwtUtils.generateConfirmationToken();
        String tokenHash = jwtUtils.sha256Hex(confirmToken);

        user.setEmailConfirmToken(tokenHash);

        String familyId = UUID.randomUUID().toString();

        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        refreshTokenModel.setFamilyId(familyId);
        refreshTokenModel.setToken(jwtUtils.sha256Hex(refreshToken));
        refreshTokenModel.setExpiresAt(
                jwtUtils.validateRefreshToken(refreshToken)
                        .getExpiration().toInstant());

        switch (userType) {
            case TEACHER -> {
                TeacherModel teacher = teacherRepository.save((TeacherModel) user);
                refreshTokenModel.setTeacher(teacher);
                userDevice.setTeacher(teacher);
            }
            case STUDENT -> {
                StudentModel student = studentRepository.save((StudentModel) user);
                refreshTokenModel.setStudent(student);
                userDevice.setStudent(student);
            }
        }

        refreshTokenRepository.save(refreshTokenModel);

        userDeviceRepository.save(userDevice);

        emailService.sendEmailConfirmation(user, userType, confirmToken);

        String accessToken = jwtUtils.generateAccessToken(user.getEmail(), userType);

        return new AuthResponse(accessToken, refreshToken, user.getId(), user.getEmail(), user.getEmailConfirmed());
    }

    @Transactional
    public AuthResponse login(LoginRequest request, UserType userType) {

        AuthUser user;

        RefreshTokenModel refreshTokenModel = new RefreshTokenModel();

        switch (userType) {
            case TEACHER -> {
                user = teacherRepository.findByEmail(request.email()).orElseThrow(() ->
                        new BadRequestException("No Teacher with this email exists!")
                );
            }
            case STUDENT -> {
                user = studentRepository.findByEmail(request.email()).orElseThrow(() ->
                        new BadRequestException("No Student with this email exists!")
                );
            }
            default -> throw new BadRequestException("UserType is invalid!");
        }

        // Check password
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password!");
        }

        DeviceDataRequest deviceData = request.deviceData();
        UserDeviceModel userDevice = userDeviceRepository.findByToken(deviceData.fcmToken())
                .orElseGet(() -> {
                    UserDeviceModel d = new UserDeviceModel();
                    d.setToken(deviceData.fcmToken());
                    d.setDeviceType(deviceData.deviceType());
                    d.setDeviceId(deviceData.deviceId());
                    return d;
                });

        userDevice.setLastActive(Instant.now());

        // Generate JWT token
        String accessToken = jwtUtils.generateAccessToken(user.getEmail(), userType);

        String familyId = UUID.randomUUID().toString();

        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        refreshTokenModel.setFamilyId(familyId);
        refreshTokenModel.setToken(jwtUtils.sha256Hex(refreshToken));
        refreshTokenModel.setExpiresAt(
                jwtUtils.validateRefreshToken(refreshToken)
                        .getExpiration().toInstant());

        switch (userType) {
            case TEACHER -> {
                TeacherModel teacher = teacherRepository.save((TeacherModel) user);
                refreshTokenModel.setTeacher(teacher);
                userDevice.setTeacher(teacher);
            }
            case STUDENT -> {
                StudentModel student = studentRepository.save((StudentModel) user);
                refreshTokenModel.setStudent(student);
                userDevice.setStudent(student);
            }
        }

        refreshTokenRepository.save(refreshTokenModel);

        userDeviceRepository.save(userDevice);

        return new AuthResponse(accessToken, refreshToken, user.getId(), user.getEmail(), user.getEmailConfirmed());
    }

    @Transactional
    public void forgotPassword(String email, UserType userType) {

        AuthUser user;

        switch (userType) {
            case TEACHER -> {
                user = teacherRepository.findByEmail(email).orElseThrow(() ->
                        new BadRequestException("Teacher does not exist!")
                );
            }
            case STUDENT -> {
                user = studentRepository.findByEmail(email).orElseThrow(() ->
                        new BadRequestException("Student does not exist!")
                );
            }
            default -> throw new BadRequestException("UserType is invalid!");
        }

        String resetToken = jwtUtils.generateResetPasswordToken();

        user.setResetPasswordToken(jwtUtils.sha256Hex(resetToken));

        emailService.sendRestPasswordEmail(user, userType, resetToken);

        switch (userType) {
            case TEACHER -> teacherRepository.save((TeacherModel) user);

            case STUDENT -> studentRepository.save((StudentModel) user);

        }
    }

    @Transactional(readOnly = true)
    public boolean verifyPasswordToken(String token, UserType userType) {
        AuthUser user;

        String tokenHash = jwtUtils.sha256Hex(token);

        switch (userType) {
            case TEACHER -> user = teacherRepository.findByResetPasswordToken(tokenHash).orElseThrow(() ->
                    new BadRequestException("Teacher does not exist!")
            );

            case STUDENT -> user = studentRepository.findByResetPasswordToken(tokenHash).orElseThrow(() ->
                    new BadRequestException("Student does not exist!")
            );

            default -> throw new BadRequestException("UserType is invalid!");
        }

        jwtUtils.validateResetPasswordToken(token);


        if (!tokenHash.equals(user.getResetPasswordToken())) {
            throw new BadRequestException("Invalid reset token!");
        }

        return true;
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request, UserType userType) {

        AuthUser user;

        String tokenHash = jwtUtils.sha256Hex(request.token());

        switch (userType) {
            case TEACHER -> {
                user = teacherRepository.findByResetPasswordToken(tokenHash).orElseThrow(() ->
                        new BadRequestException("Teacher does not exist!")
                );
            }

            case STUDENT -> {
                user = studentRepository.findByResetPasswordToken(tokenHash).orElseThrow(() ->
                        new BadRequestException("Student does not exist!")
                );
            }

            default -> throw new BadRequestException("UserType is invalid!");
        }


        if (!tokenHash.equals(user.getResetPasswordToken())) {
            throw new BadRequestException("Invalid reset token!");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setResetPasswordToken(null);

        switch (userType) {
            case TEACHER -> teacherRepository.save((TeacherModel) user);

            case STUDENT -> studentRepository.save((StudentModel) user);
        }
    }

    @Transactional
    public void confirmEmail(String token, UserType userType) {
        AuthUser user;

        String tokenHash = jwtUtils.sha256Hex(token);
        System.out.println(token);
        System.out.println(tokenHash);

        switch (userType) {
            case TEACHER -> user = teacherRepository.findByEmailConfirmToken(tokenHash).orElseThrow(
                    () -> new NotFoundException("Teacher does not exist!"));
            case STUDENT -> user = studentRepository.findByEmailConfirmToken(tokenHash).orElseThrow(
                    () -> new NotFoundException("Student does not exist!"));
            default -> throw new RuntimeException("Invalid userType!!");
        }

        if (user.getEmailConfirmed()) {
            throw new BadRequestException("Email already confirmed");
        }

        jwtUtils.validateConfirmationToken(token);

        if (!tokenHash.equals(user.getEmailConfirmToken())) {
            throw new BadRequestException("Invalid confirmation token!");
        }

        user.setEmailConfirmed(true);
        user.setEmailConfirmToken(null);

        switch (userType) {
            case TEACHER -> teacherRepository.save((TeacherModel) user);

            case STUDENT -> studentRepository.save((StudentModel) user);
        }
    }

    @Transactional
    public void sendConfirmEmail(String email, UserType userType){
        AuthUser user;
            switch (userType) {
                case TEACHER -> user = teacherRepository.findByEmail(email).orElseThrow(
                        () -> new NotFoundException("Teacher does not exist!"));
                case STUDENT -> user = studentRepository.findByEmail(email).orElseThrow(
                        () -> new NotFoundException("Student does not exist!"));
                default -> throw new RuntimeException("Invalid userType!!");
            }

        if(user.getEmailConfirmed()) throw new BadRequestException("Email already confirmed");

        String confirmToken = jwtUtils.generateConfirmationToken();
        String tokenHash = jwtUtils.sha256Hex(confirmToken);

        user.setEmailConfirmToken(tokenHash);
        emailService.sendEmailConfirmation(user, userType, confirmToken);

        switch (userType) {
            case TEACHER -> teacherRepository.save((TeacherModel) user);

            case STUDENT -> studentRepository.save((StudentModel) user);
        }
    }

    @Transactional
    public RefreshDto refreshAccessToken(String refreshToken, UserType userType){

        RefreshTokenModel refreshTokenModel = refreshTokenRepository.findByToken(jwtUtils.sha256Hex(refreshToken))
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshTokenModel.getRevoked()) {
            refreshTokenRepository.revokeFamily(refreshTokenModel.getFamilyId());
            throw new UnauthorizedException("Refresh token reuse detected");
        }

        if (refreshTokenModel.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.revokeFamily(refreshTokenModel.getFamilyId());
            throw new UnauthorizedException("Refresh token expired");
        }

        Claims claims = jwtUtils.validateRefreshToken(refreshToken);
        refreshTokenModel.setRevoked(true);

        String newRefreshToken = jwtUtils.generateRefreshToken(claims.getSubject());

        String newAccessToken = jwtUtils.generateAccessToken(claims.getSubject(), userType);

        refreshTokenModel.setToken(jwtUtils.sha256Hex(newRefreshToken));
        refreshTokenModel.setExpiresAt(claims.getExpiration().toInstant());

        refreshTokenRepository.save(refreshTokenModel);

        return new RefreshDto(newAccessToken, newRefreshToken);
    }

}
