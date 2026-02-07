package com.meshwarcoders.catalyst.api.security;

import com.meshwarcoders.catalyst.api.exception.BadRequestException;
import com.meshwarcoders.catalyst.api.exception.NotFoundException;
import com.meshwarcoders.catalyst.api.model.StudentModel;
import com.meshwarcoders.catalyst.api.model.TeacherModel;
import com.meshwarcoders.catalyst.api.model.common.UserType;
import com.meshwarcoders.catalyst.api.repository.StudentRepository;
import com.meshwarcoders.catalyst.api.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        throw new RuntimeException("Use loadUser(email, userType) instead");
    }

    public UserDetails loadUser(String email, UserType userType) {
        switch (userType) {
            case STUDENT -> {
                StudentModel student = studentRepository.findByEmail(email)
                        .orElseThrow(() -> new NotFoundException("No Student with this email exists!"));

                return User.builder()
                        .username(student.getEmail())
                        .password(student.getPassword())
                        .roles("STUDENT")
                        .build();
            }
            case TEACHER -> {
                TeacherModel teacher = teacherRepository.findByEmail(email)
                        .orElseThrow(() -> new NotFoundException("No Teacher with this email exists!"));

                return User.builder()
                        .username(teacher.getEmail())
                        .password(teacher.getPassword())
                        .roles("TEACHER")
                        .build();

            }

            default -> throw new BadRequestException("UserType is invalid!");
        }
    }
}
