package com.meshwarcoders.catalyst.api.service;

import com.meshwarcoders.catalyst.api.exception.BadRequestException;
import com.meshwarcoders.catalyst.api.exception.NotFoundException;
import com.meshwarcoders.catalyst.api.exception.UnauthorizedException;
import com.meshwarcoders.catalyst.api.model.LessonModel;
import com.meshwarcoders.catalyst.api.model.LessonScheduleModel;
import com.meshwarcoders.catalyst.api.model.TeacherModel;
import com.meshwarcoders.catalyst.api.repository.LessonRepository;
import com.meshwarcoders.catalyst.api.repository.LessonScheduleRepository;
import com.meshwarcoders.catalyst.api.repository.TeacherRepository;
import com.meshwarcoders.catalyst.api.dto.CreateLessonRequest;
import com.meshwarcoders.catalyst.api.dto.LessonScheduleRequest;
import com.meshwarcoders.catalyst.api.dto.LessonDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LessonService {
    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private LessonScheduleRepository lessonScheduleRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Transactional
    public LessonDto createLesson(Long teacherId, CreateLessonRequest request){
        LessonModel lesson = new LessonModel();

        TeacherModel teacher = teacherRepository.findById(teacherId).orElseThrow(() -> new NotFoundException("Teacher Not Found!"));
        lesson.setTeacher(teacher);
        lesson.setSubject(request.getSubject());
        lessonRepository.save(lesson);
        List<LessonScheduleModel> lessonSchedules = new ArrayList<>();
        if(request.getLessonSchedules() != null){
            for(LessonScheduleRequest ls: request.getLessonSchedules()){
                LessonScheduleModel lsm = new LessonScheduleModel();
                lsm.setLesson(lesson);
                lsm.setDuration(ls.getDuration());
                if(ls.getStartTime() != null && !ls.getStartTime().isBlank()){

                    try {
                        lsm.setStartTime(LocalTime.parse(ls.getStartTime()));
                    } catch (DateTimeParseException e){
                        throw new BadRequestException("Invalid start time: " + ls.getStartTime());
                    }

                }
                lsm.setDay(ls.getDay());
                lessonSchedules.add(lsm);
            }
            lessonScheduleRepository.saveAll(lessonSchedules);
        }
        return new LessonDto(lesson.getId(), lesson.getSubject(), lessonScheduleRepository.findByLessonId(lesson.getId()));
    }

    public LessonDto getLesson(Long lessonId, Long teacherId){
        LessonModel lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found!"));

        if(!lesson.getTeacher().getId().equals(teacherId)){
            throw new UnauthorizedException("You do not own this lesson!");
        }

        return new LessonDto(lesson.getId(), lesson.getSubject(), lessonScheduleRepository.findByLessonId(lessonId));
    }

    public List<LessonDto> getTeacherLessons(Long teacherId){
        return lessonRepository.findByTeacherId(teacherId)
                .stream()
                .map(lesson -> new LessonDto(lesson.getId(), lesson.getSubject(),
                        lessonScheduleRepository.findByLessonId(lesson.getId())))
                .toList();
    }
}
