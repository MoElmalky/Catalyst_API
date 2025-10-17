package com.meshwarcoders.catalyst.api.model;

import jakarta.persistence.*;

@Entity(name = "student_exam")
public class StudentExamModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private ExamModel exam;

    @ManyToOne(optional = false)
    private StudentModel student;

    private Integer grade;
}
