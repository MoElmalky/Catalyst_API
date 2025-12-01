package com.meshwarcoders.catalyst.api.service;

import com.meshwarcoders.catalyst.api.exception.NotFoundException;
import com.meshwarcoders.catalyst.api.model.LessonModel;
import com.meshwarcoders.catalyst.api.model.LessonScheduleModel;
import com.meshwarcoders.catalyst.api.model.TeacherModel;
import com.meshwarcoders.catalyst.api.repository.LessonRepository;
import com.meshwarcoders.catalyst.api.repository.TeacherRepository;
import com.meshwarcoders.catalyst.api.dto.CreateLessonRequest;
import com.meshwarcoders.catalyst.api.dto.LessonScheduleRequest;
import com.meshwarcoders.catalyst.api.dto.LessonDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
public class LessonService {
    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Transactional
    public LessonDto createLesson(Long teacherId, CreateLessonRequest request){
        LessonModel lesson = new LessonModel();

        TeacherModel teacher = teacherRepository.findById(teacherId).orElseThrow(() -> new NotFoundException("Teacher Not Found!"));
        lesson.setTeacher(teacher);
        lesson.setSubject(request.getSubject());
        lessonRepository.save(lesson);
        if(request.getLessonSchedules() != null){
            for(LessonScheduleRequest ls: request.getLessonSchedules()){
                LessonScheduleModel lsm = new LessonScheduleModel();
                lsm.setLesson(lesson);
                lsm.setDuration(ls.getDuration());
                if(ls.getStartTime() != null && !ls.getStartTime().isBlank()){
                    lsm.setStartTime(LocalTime.parse(ls.getStartTime()));
                }
                lsm.setDay(ls.getDay());
            }
        }
        return toLessonDto(lesson);
    }

    private LessonDto toLessonDto(LessonModel lesson){
        LessonDto lessonDto = new LessonDto(lesson.getId(), lesson.getSubject());
        lessonDto.setLessonSchedules(lesson.getLessonSchedules());
        return lessonDto;
    }
}
