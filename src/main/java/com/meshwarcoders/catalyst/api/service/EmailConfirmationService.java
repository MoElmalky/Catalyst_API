package com.meshwarcoders.catalyst.api.service;

import com.meshwarcoders.catalyst.api.model.StudentModel;
import com.meshwarcoders.catalyst.api.model.TeacherModel;
import com.meshwarcoders.catalyst.api.model.common.EmailableUser;
import com.meshwarcoders.catalyst.api.repository.StudentRepository;
import com.meshwarcoders.catalyst.api.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmailConfirmationService {

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private TeacherRepository teacherRepo;
    public void confirmEmail(String email) {
        Optional<? extends EmailableUser> userOpt = findUserByEmail(email);
        EmailableUser user = userOpt.orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmailConfirmed(true);
        saveUser(user);
    }

    private Optional<? extends EmailableUser> findUserByEmail(String email) {
        return studentRepo.findByEmail(email)
                .map(s -> (EmailableUser) s)
                .or(() -> teacherRepo.findByEmail(email).map(t -> (EmailableUser) t));
    }

    private void saveUser(EmailableUser user) {
        if (user instanceof StudentModel) {
            studentRepo.save((StudentModel) user);
        } else if (user instanceof TeacherModel) {
            teacherRepo.save((TeacherModel) user);
        }
    }
}

