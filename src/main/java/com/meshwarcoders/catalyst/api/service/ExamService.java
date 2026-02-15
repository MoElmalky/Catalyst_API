package com.meshwarcoders.catalyst.api.service;

import java.util.Map;

import com.meshwarcoders.catalyst.api.dto.request.AnswerRequest;
import com.meshwarcoders.catalyst.api.dto.request.CreateExamRequest;
import com.meshwarcoders.catalyst.api.dto.request.ExamQuestionRequest;
import com.meshwarcoders.catalyst.api.dto.response.ExamDetailsDto;
import com.meshwarcoders.catalyst.api.dto.response.ExamSummaryDto;
import com.meshwarcoders.catalyst.api.dto.response.QuestionDto;
import com.meshwarcoders.catalyst.api.exception.NotFoundException;
import com.meshwarcoders.catalyst.api.exception.UnauthorizedException;
import com.meshwarcoders.catalyst.api.model.*;
import com.meshwarcoders.catalyst.api.model.common.EnrollmentStatus;
import com.meshwarcoders.catalyst.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamQuestionRepository examQuestionRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentLessonRepository studentLessonRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentExamRepository studentExamRepository;

    @Autowired
    private StudentAnswerRepository studentAnswerRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public ExamSummaryDto createExam(Long teacherId, Long lessonId, CreateExamRequest request) {
        LessonModel lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found!"));

        if (!lesson.getTeacher().getId().equals(teacherId)) {
            throw new UnauthorizedException("You do not own this lesson!");
        }

        ExamModel exam = new ExamModel();
        exam.setLesson(lesson);
        exam.setExamName(request.examName());
        exam.setMaxGrade(request.maxGrade());
        exam.setExamType(request.examType());

        Integer defaultPoints = request.defaultPoints();
        ;

        if (request.examDateTime() != null && !request.examDateTime().isBlank()) {
            exam.setExamDateTime(LocalDateTime.parse(request.examDateTime()));
        }
        if (request.closingDate() != null && !request.closingDate().isBlank()) {
            exam.setClosingDate(LocalDateTime.parse(request.closingDate()));
        }
        exam.setDurationMinutes(request.durationMinutes());

        exam = examRepository.save(exam);

        if (request.questions() != null) {
            for (ExamQuestionRequest q : request.questions()) {
                ExamQuestionModel qm = new ExamQuestionModel();
                qm.setExam(exam);
                qm.setText(q.text());
                qm.setType(q.type());
                qm.setOptions(q.options());
                qm.setCorrectOptionIndex(q.correctOptionIndex());
                qm.setMaxPoints(q.maxPoints() == null ? defaultPoints : q.maxPoints());
                qm.setAnswer(q.answer());
                examQuestionRepository.save(qm);
            }
        }

        // Notify Students
        List<StudentModel> students = studentLessonRepository.findByLessonAndStatus(lesson, EnrollmentStatus.APPROVED)
                .stream()
                .map(StudentLessonModel::getStudent)
                .toList();

        notificationService.notifyStudents(
                students,
                "New Exam Added",
                "A new exam '" + exam.getExamName() + "' has been added to " + lesson.getSubject(),
                Map.of(
                        "type", "NEW_EXAM",
                        "lessonId", lesson.getId().toString(),
                        "examId", exam.getId().toString()));

        return toSummaryDto(exam);
    }

    @Transactional(readOnly = true)
    public List<ExamSummaryDto> getExamsForLessonAsTeacher(Long teacherId, Long lessonId) {
        LessonModel lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found!"));

        if (!lesson.getTeacher().getId().equals(teacherId)) {
            throw new UnauthorizedException("You do not own this lesson!");
        }

        return lesson.getExams()
                .stream()
                .map(this::toSummaryDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ExamSummaryDto> getExamsForLessonAsStudent(String studentEmail, Long lessonId) {
        StudentModel student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new NotFoundException("Student not found!"));

        LessonModel lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found!"));

        StudentLessonModel sl = studentLessonRepository.findByLessonAndStudent(lesson, student)
                .orElseThrow(() -> new UnauthorizedException("You are not enrolled in this lesson!"));

        if (sl.getStatus() != EnrollmentStatus.APPROVED) {
            throw new UnauthorizedException("You are not approved in this lesson!");
        }

        return examRepository.findByLesson(lesson)
                .stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExamDetailsDto getExamById(Long examId, String email, boolean isTeacher) {
        ExamModel exam = examRepository.findById(examId)
                .orElseThrow(() -> new NotFoundException("Exam not found!"));

        if (isTeacher) {
            TeacherModel teacher = teacherRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("Teacher not found!"));
            if (!exam.getLesson().getTeacher().getId().equals(teacher.getId())) {
                throw new UnauthorizedException("You do not own this lesson!");
            }
        } else {
            StudentModel student = studentRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("Student not found!"));
            StudentLessonModel sl = studentLessonRepository.findByLessonAndStudent(exam.getLesson(), student)
                    .orElseThrow(() -> new UnauthorizedException("You are not enrolled in this lesson!"));
            if (sl.getStatus() != EnrollmentStatus.APPROVED) {
                throw new UnauthorizedException("You are not approved in this lesson!");
            }
        }

        List<QuestionDto> questions = exam.getQuestions().stream()
                .map(q -> new QuestionDto(
                        q.getId(),
                        q.getText(),
                        q.getType(),
                        q.getOptions(),
                        q.getMaxPoints()))
                .toList();

        return new ExamDetailsDto(
                exam.getId(),
                exam.getLesson().getId(),
                exam.getExamName(),
                exam.getMaxGrade(),
                exam.getExamDateTime() != null ? exam.getExamDateTime().toString() : null,
                exam.getClosingDate() != null ? exam.getClosingDate().toString() : null,
                exam.getDurationMinutes(),
                exam.getExamType(),
                questions);
    }

    private ExamSummaryDto toSummaryDto(ExamModel exam) {
        String dateTimeString = exam.getExamDateTime() != null ? exam.getExamDateTime().toString() : null;
        String closingDateString = exam.getClosingDate() != null ? exam.getClosingDate().toString() : null;

        return new ExamSummaryDto(
                exam.getId(),
                exam.getLesson().getId(),
                exam.getExamName(),
                exam.getMaxGrade(),
                dateTimeString,
                closingDateString,
                exam.getDurationMinutes(),
                exam.getExamType());
    }

    @Transactional
    public void submitExam(String studentEmail, Long examId, List<AnswerRequest> answerRequests) {
        StudentModel student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new NotFoundException("Student not found!"));

        ExamModel exam = examRepository.findById(examId)
                .orElseThrow(() -> new NotFoundException("Exam not found!"));

        // Validate enrollment
        StudentLessonModel sl = studentLessonRepository.findByLessonAndStudent(exam.getLesson(), student)
                .orElseThrow(() -> new UnauthorizedException("You are not enrolled in this lesson!"));

        if (sl.getStatus() != EnrollmentStatus.APPROVED) {
            throw new UnauthorizedException("You are not approved in this lesson!");
        }

        // Check closing date
        if (exam.getClosingDate() != null && LocalDateTime.now().isAfter(exam.getClosingDate())) {
            throw new UnauthorizedException("The deadline for this exam has passed!");
        }

        // Check if already submitted
        if (studentExamRepository.findByStudentAndExam(student, exam).isPresent()) {
            throw new UnauthorizedException("You have already submitted this exam!");
        }

        StudentExamModel studentExam = new StudentExamModel();
        studentExam.setStudent(student);
        studentExam.setExam(exam);
        studentExam.setGrade(null); // No auto-grading as requested
        studentExamRepository.save(studentExam);

        List<StudentAnswerModel> answers = answerRequests.stream()
                .map(a -> {
                    ExamQuestionModel question = examQuestionRepository.findById(a.questionId())
                            .orElseThrow(() -> new NotFoundException("Question not found: " + a.questionId()));

                    if (!question.getExam().getId().equals(exam.getId())) {
                        throw new UnauthorizedException("Question does not belong to this exam!");
                    }

                    StudentAnswerModel answer = new StudentAnswerModel();
                    answer.setStudentExam(studentExam);
                    answer.setQuestion(question);
                    answer.setSelectedOptions(a.selectedOptions());
                    answer.setTextAnswer(a.textAnswer());
                    return answer;
                })
                .toList();

        studentAnswerRepository.saveAll(answers);
    }
}