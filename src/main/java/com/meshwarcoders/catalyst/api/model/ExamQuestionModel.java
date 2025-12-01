package com.meshwarcoders.catalyst.api.model;

import com.meshwarcoders.catalyst.api.model.common.QuestionType;
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

    public void setExam(ExamModel exam) {
        this.exam = exam;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public void setCorrectOptionIndex(Integer correctOptionIndex) {
        this.correctOptionIndex = correctOptionIndex;
    }

    public void setMaxPoints(Integer maxPoints) {
        this.maxPoints = maxPoints;
    }
}