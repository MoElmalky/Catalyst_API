package com.meshwarcoders.catalyst.api.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "student_exams")
@Getter @Setter
public class StudentExamModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(optional = false)
    private ExamModel exam;

    @ManyToOne(optional = false)
    private StudentModel student;

    private Integer grade;

    private Boolean verified = false;
}
