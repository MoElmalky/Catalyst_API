package com.meshwarcoders.catalyst.api.dto;

import java.util.List;

public class JoinRequestBulkActionResultDto {
    private Long lessonId;
    private List<Long> affectedStudentLessonsIds;
    private List<Long> skippedStudentLessonsIds;

    public JoinRequestBulkActionResultDto(Long lessonId, List<Long> affectedStudentIds, List<Long> skippedStudentIds) {
        this.lessonId = lessonId;
        this.affectedStudentLessonsIds = affectedStudentIds;
        this.skippedStudentLessonsIds = skippedStudentIds;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public List<Long> getAffectedStudentIds() {
        return affectedStudentLessonsIds;
    }

    public List<Long> getSkippedStudentIds() {
        return skippedStudentLessonsIds;
    }
}