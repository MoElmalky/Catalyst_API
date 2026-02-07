package com.meshwarcoders.catalyst.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meshwarcoders.catalyst.api.model.common.AuthUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "teachers")
@Getter @Setter
public class TeacherModel extends AuthUser {

    private String fullName;

    @JsonIgnore
    @OneToMany(mappedBy = "teacher")
    private List<LessonModel> lessons = new ArrayList<>();
}
