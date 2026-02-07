package com.meshwarcoders.catalyst.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meshwarcoders.catalyst.api.model.common.AuthUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "students")
@Getter @Setter
public class StudentModel extends AuthUser {

    private String fullName;

    @JsonIgnore
    @OneToMany(mappedBy = "student")
    private List<StudentLessonModel> studentLessons = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "student")
    private List<StudentExamModel> studentExams = new ArrayList<>();
}
