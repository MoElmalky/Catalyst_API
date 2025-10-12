package com.meshwarcoders.catalyst.api.repository;

import com.meshwarcoders.catalyst.api.model.TeacherModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<TeacherModel, Long> {
    Optional<TeacherModel> findByEmail(String email);
    boolean existsByEmail(String email);
}