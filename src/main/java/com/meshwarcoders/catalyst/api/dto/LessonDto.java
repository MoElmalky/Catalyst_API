package com.meshwarcoders.catalyst.api.dto;

import com.meshwarcoders.catalyst.api.model.LessonScheduleModel;

import java.util.List;
import java.util.stream.Collectors;

public class LessonDto {
    private Long id;
    private String subject;
    private List<LessonScheduleDto> lessonSchedules;

    public LessonDto(Long id, String subject, List<LessonScheduleModel> lessonSchedules){
        this.id = id;
        this.subject = subject;
        this.lessonSchedules = lessonSchedules.stream()
                .map(this::toLessonScheduleDto).toList();
    }
    public Long getId(){
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public List<LessonScheduleDto> getLessonSchedules() {
        return lessonSchedules;
    }

    public LessonScheduleDto toLessonScheduleDto(LessonScheduleModel ls){
        return new LessonScheduleDto(ls.getStartTime().toString(),ls.getDay(),ls.getDuration());
    }
}
