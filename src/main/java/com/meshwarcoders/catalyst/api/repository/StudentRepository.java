package com.meshwarcoders.catalyst.api.repository;

import com.meshwarcoders.catalyst.api.model.StudentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<StudentModel, Long> {
    Optional<StudentModel> findByEmail(String email);
    boolean existsByEmail(String email);
}
