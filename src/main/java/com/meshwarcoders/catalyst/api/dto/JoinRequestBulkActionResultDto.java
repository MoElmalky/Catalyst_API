package com.meshwarcoders.catalyst.api.dto;

import java.util.List;

public class JoinRequestBulkActionResultDto {
    private Long lessonId;
    private List<Long> affectedStudentIds;
    private List<Long> skippedStudentIds;

    public JoinRequestBulkActionResultDto(Long lessonId, List<Long> affectedStudentIds, List<Long> skippedStudentIds) {
        this.lessonId = lessonId;
        this.affectedStudentIds = affectedStudentIds;
        this.skippedStudentIds = skippedStudentIds;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public List<Long> getAffectedStudentIds() {
        return affectedStudentIds;
    }

    public List<Long> getSkippedStudentIds() {
        return skippedStudentIds;
    }
}