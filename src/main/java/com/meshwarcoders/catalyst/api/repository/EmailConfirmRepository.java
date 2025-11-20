package com.meshwarcoders.catalyst.api.repository;

import com.meshwarcoders.catalyst.api.model.EmailConfirmModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailConfirmRepository extends JpaRepository<EmailConfirmModel, Long> {
    Optional<EmailConfirmModel> findByEmail(String email);
    boolean existsByEmail(String email);
}
