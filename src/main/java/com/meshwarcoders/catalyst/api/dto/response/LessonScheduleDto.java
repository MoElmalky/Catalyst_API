package com.meshwarcoders.catalyst.api.dto.response;

import com.meshwarcoders.catalyst.api.model.common.WeekDay;

public record LessonScheduleDto (
     String startTime,
     WeekDay day,
     Integer duration){

}

