package com.meshwarcoders.catalyst.api.repository;

import com.meshwarcoders.catalyst.api.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentLessonRepository extends JpaRepository<StudentLessonModel, Long> {
    boolean existsByLessonAndStudent(LessonModel lesson, StudentModel student);

    Optional<StudentLessonModel> findByLessonAndStudent(LessonModel lesson, StudentModel student);

    List<StudentLessonModel> findByLessonAndStatus(LessonModel lesson, EnrollmentStatus status);
}