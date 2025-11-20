package com.meshwarcoders.catalyst.api.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class JoinRequestBulkActionRequest {

    @NotEmpty(message = "studentIds cannot be empty")
    private List<Long> studentIds;

    public List<Long> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<Long> studentIds) {
        this.studentIds = studentIds;
    }
}