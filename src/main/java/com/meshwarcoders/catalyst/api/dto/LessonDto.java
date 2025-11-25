package com.meshwarcoders.catalyst.api.dto;

import com.meshwarcoders.catalyst.api.model.LessonScheduleModel;

import java.util.List;

public class LessonDto {
    private Long id;
    private String subject;
    private List<LessonScheduleDto> lessonSchedules;

    public LessonDto(Long id, String subject){
        this.id = id;
        this.subject = subject;
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

    public void setLessonSchedules(List<LessonScheduleModel> lessonSchedules) {
        for(LessonScheduleModel lsm : lessonSchedules){
            this.lessonSchedules.add(new LessonScheduleDto(lsm.getStartTime().toString(),lsm.getDay(),lsm.getDuration()));
        }
    }
}
