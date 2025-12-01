package com.meshwarcoders.catalyst.api.service;

import com.meshwarcoders.catalyst.api.model.StudentModel;
import com.meshwarcoders.catalyst.api.model.TeacherModel;
import com.meshwarcoders.catalyst.api.repository.StudentRepository;
import com.meshwarcoders.catalyst.api.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        // Try teacher first
        TeacherModel teacher = teacherRepository.findByEmail(email).orElse(null);
        if (teacher != null) {
            return User.builder()
                    .username(teacher.getEmail())
                    .password(teacher.getPassword())
                    .roles("TEACHER")
                    .build();
        }

        // Then try student
        StudentModel student = studentRepository.findByEmail(email).orElse(null);
        if (student != null) {
            return User.builder()
                    .username(student.getEmail())
                    .password(student.getPassword())
                    .roles("STUDENT")
                    .build();
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
