package com.meshwarcoders.catalyst.api.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class JoinRequestBulkActionRequest {

    @NotEmpty(message = "studentIds cannot be empty")
    private List<Long> studentLessonIds;

    public List<Long> getStudentLessonIds() {
        return studentLessonIds;
    }

    public void setStudentLessonIds(List<Long> studentLessonIds) {
        this.studentLessonIds = studentLessonIds;
    }
}