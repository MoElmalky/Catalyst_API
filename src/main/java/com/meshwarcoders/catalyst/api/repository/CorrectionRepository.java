package com.meshwarcoders.catalyst.api.repository;

import com.meshwarcoders.catalyst.api.model.CorrectionModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorrectionRepository extends JpaRepository<CorrectionModel, Long> {
}
