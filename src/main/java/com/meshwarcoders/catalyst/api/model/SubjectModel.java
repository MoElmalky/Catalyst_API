package com.meshwarcoders.catalyst.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity(name = "subjects")
public class SubjectModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "subjectName cannot be null")
    @NotBlank(message = "subjectName cannot be blank")
    private String name;

    @ManyToOne
    private TeacherModel teacher;



}
