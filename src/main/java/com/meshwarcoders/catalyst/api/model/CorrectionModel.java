package com.meshwarcoders.catalyst.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "corrections")
@Getter
@Setter
public class CorrectionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String studentAnswer;

    @Column(columnDefinition = "TEXT")
    private String teacherAnswer;

    private Double similarityScore;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private StudentModel student;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private ExamQuestionModel question;
}
