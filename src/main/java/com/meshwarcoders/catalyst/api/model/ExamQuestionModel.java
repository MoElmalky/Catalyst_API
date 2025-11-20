package com.meshwarcoders.catalyst.api.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "exam_questions")
public class ExamQuestionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private ExamModel exam;

    @Column(nullable = false)
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;

    // For MCQ questions
    @ElementCollection
    private List<String> options = new ArrayList<>();

    private Integer correctOptionIndex;

    private Integer maxPoints;
}