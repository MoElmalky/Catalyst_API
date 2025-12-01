package com.meshwarcoders.catalyst.api.repository;

import com.meshwarcoders.catalyst.api.model.ExamQuestionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestionModel, Long> {
}