package com.meshwarcoders.catalyst.api.repository;

import com.meshwarcoders.catalyst.api.model.TeacherModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<TeacherModel, Long> {
    Optional<TeacherModel> findByEmail(String email);
    Optional<TeacherModel> findByEmailAndEmailConfirmedTrue(String email);

    Optional<TeacherModel> findByResetPasswordToken(String token);

    Optional<TeacherModel> findByEmailConfirmToken(String token);


    boolean existsByEmail(String email);
}