package com.meshwarcoders.catalyst.api.repository;

import com.meshwarcoders.catalyst.api.model.StudentAnswerModel;
import com.meshwarcoders.catalyst.api.model.StudentExamModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswerModel, Long> {
    List<StudentAnswerModel> findByStudentExam(StudentExamModel studentExam);
}
