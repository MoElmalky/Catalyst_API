package com.meshwarcoders.catalyst.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ========== PUBLIC ENDPOINTS (بدون Token) ==========
                        
                        // Teacher Auth (Public)
                        .requestMatchers(
                                "/api/teachers/signup",
                                "/api/teachers/login",
                                "/api/teachers/forgot-password",
                                "/api/teachers/verify-reset-code",
                                "/api/teachers/reset-password",
                                "/api/teachers/confirm-email",
                                "/api/students/signup",
                                "/api/students/login",
                                "/api/students/confirm-email"
                        ).permitAll()
                        
                        // Teacher Public Info
                        .requestMatchers(
                                "/api/teachers/all",
                                "/api/teachers/{id}"
                        ).permitAll()
                        
                        // Student Auth (Public)
                        .requestMatchers(
                                "/api/students/signup",
                                "/api/students/login",
                                "/api/students/forgot-password",
                                "/api/students/verify-reset-code",
                                "/api/students/reset-password"
                        ).permitAll()
                        
                        // Student Public Info
                        .requestMatchers(
                                "/api/students/all",
                                "/api/students/{id}"
                        ).permitAll()
                        
                        // Lessons (Public - للطلاب يقدروا يشوفوا الكورسات المتاحة)
                        .requestMatchers(
                                "/api/lessons/all",
                                "/api/lessons/teacher/{teacherId}",
                                "/api/lessons/{id}"
                        ).permitAll()
                        
                        // Home Dashboard (Public)
                        .requestMatchers(
                                "/api/home/dashboard"
                        ).permitAll()

                        // ========== STUDENT ENDPOINTS (محتاج Student Token) ==========
                        .requestMatchers(
                                "/api/students/profile",
                                "/api/class-requests/join",
                                "/api/class-requests/my-requests"
                        ).hasRole("STUDENT")

                        // ========== TEACHER ENDPOINTS (محتاج Teacher Token) ==========
                        .requestMatchers(
                                "/api/teachers/profile",
                                "/api/lessons/my-lessons",
                                "/api/lessons/create",
                                "/api/lessons/{id}",  // PUT and DELETE
                                "/api/class-requests/pending",
                                "/api/class-requests/approve/{requestId}",
                                "/api/class-requests/reject/{requestId}",
                                "/api/class-requests/remove-student/{lessonId}/{studentId}",
                                "/api/class-requests/students/{lessonId}",
                                "/api/student-management/remove-from-all-classes/{studentId}",
                                "/api/student-management/student-enrollment/{studentId}"
                        ).hasRole("TEACHER")

                        // ========== ANY OTHER REQUEST ==========
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}