package com.meshwarcoders.catalyst.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meshwarcoders.catalyst.api.model.common.EmailableUser;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "students")
public class StudentModel implements EmailableUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(unique = true)
    private String email;

    @JsonIgnore
    private String password;

    private boolean emailConfirmed;

    @JsonIgnore
    @OneToMany(mappedBy = "student")
    private List<StudentLessonModel> studentLessons = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "student")
    private List<StudentExamModel> studentExams = new ArrayList<>();

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public boolean isEmailConfirmed() {
        return emailConfirmed;
    }

    @Override
    public void setEmailConfirmed(boolean confirmed) {
        this.emailConfirmed = confirmed;
    }
}
