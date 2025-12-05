package com.meshwarcoders.catalyst.api.dto;

import java.util.List;

public record LessonDetailsDto(Long id,
                               String subject,
                               TeacherDetailsDto teacher,
                               LessonStudentsDto studentsInLesson,
                               List<LessonScheduleDto> lessonSchedule,
                               LessonExamsDto lessonExams){ }
