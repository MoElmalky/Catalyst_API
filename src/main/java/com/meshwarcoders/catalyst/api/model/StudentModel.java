package com.meshwarcoders.catalyst.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "students")
public class StudentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(unique = true)
    private String email;

    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "student")
    private List<StudentLessonModel> studentLessons = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "student")
    private List<StudentExamModel> studentExams = new ArrayList<>();

}
