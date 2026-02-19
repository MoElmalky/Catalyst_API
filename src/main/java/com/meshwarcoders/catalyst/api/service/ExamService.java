package com.meshwarcoders.catalyst.api.service;

import java.util.*;

import com.meshwarcoders.catalyst.api.dto.request.*;
import com.meshwarcoders.catalyst.api.dto.response.*;
import com.meshwarcoders.catalyst.api.exception.NotFoundException;
import com.meshwarcoders.catalyst.api.exception.UnauthorizedException;
import com.meshwarcoders.catalyst.api.event.ExamSubmittedEvent;
import com.meshwarcoders.catalyst.api.model.*;
import com.meshwarcoders.catalyst.api.model.common.EnrollmentStatus;
import com.meshwarcoders.catalyst.api.repository.*;
import com.meshwarcoders.catalyst.util.UtilFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDateTime;
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

    @Autowired
    private SimilarityService similarityService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

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
    public ExamDetailsDto getExamByIdAsTeacher(Long examId, String email) {
        ExamModel exam = examRepository.findById(examId)
                .orElseThrow(() -> new NotFoundException("Exam not found!"));
        TeacherModel teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Teacher not found!"));
        if (!exam.getLesson().getTeacher().getId().equals(teacher.getId())) {
            throw new UnauthorizedException("You do not own this lesson!");
        }

        List<StudentExamModel> studentExams = studentExamRepository.findByExam(exam);

        List<StudentExamDto> studentGrads = studentExams.stream().map(e -> new StudentExamDto(
                e.getId(), e.getStudent().getFullName(), e.getGrade(), e.getVerified())).toList();

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
                questions,
                studentGrads);
    }

    @Transactional(readOnly = true)
    public ExamDetailsDto getExamByIdAsStudent(Long examId, String email) {
        ExamModel exam = examRepository.findById(examId)
                .orElseThrow(() -> new NotFoundException("Exam not found!"));

        StudentModel student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Student not found!"));
        StudentLessonModel sl = studentLessonRepository.findByLessonAndStudent(exam.getLesson(), student)
                .orElseThrow(() -> new UnauthorizedException("You are not enrolled in this lesson!"));
        if (sl.getStatus() != EnrollmentStatus.APPROVED) {
            throw new UnauthorizedException("You are not approved in this lesson!");
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
                questions,
                null);
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
        eventPublisher.publishEvent(new ExamSubmittedEvent(answers));
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void gradeAnswers(ExamSubmittedEvent event) {
        List<Long> answerIds = event.answers().stream().map(StudentAnswerModel::getId).toList();
        List<StudentAnswerModel> answers = studentAnswerRepository.findAllById(answerIds);

        if (answers.isEmpty())
            return;

        System.out.println("Grading......");

        List<StudentAnswerModel> writingAnswers = new ArrayList<>();
        List<Map<String, String>> writingPairs = new ArrayList<>();
        List<Integer> writingMaxPoints = new ArrayList<>();
        double totalGrade = 0.0;

        for (StudentAnswerModel ans : answers) {
            ExamQuestionModel question = ans.getQuestion();
            int maxPoints = question.getMaxPoints();

            switch (question.getType()) {
                case MCQ -> {
                    double mcqMark = calculateMcqMark(ans, question, maxPoints);
                    ans.setMark(mcqMark);
                    totalGrade += mcqMark;
                }
                case WRITING -> {
                    writingAnswers.add(ans);
                    writingMaxPoints.add(maxPoints);
                    writingPairs.add(Map.of(
                            "studentAnswer", ans.getTextAnswer() != null ? ans.getTextAnswer() : "",
                            "teacherAnswer", question.getAnswer() != null ? question.getAnswer() : ""));
                }
            }
        }

        if (!writingAnswers.isEmpty()) {
            List<Double> writingMarks = similarityService.calculate(writingPairs);
            for (int i = 0; i < writingAnswers.size(); i++) {
                double mark = writingMarks.get(i) * writingMaxPoints.get(i);
                writingAnswers.get(i).setMark(mark);
                totalGrade += mark;
            }
        }

        StudentExamModel studentExam = answers.get(0).getStudentExam();
        studentExam.setGrade((int) Math.round(totalGrade));
        studentAnswerRepository.saveAll(answers);
        studentExamRepository.save(studentExam);
    }

    private double calculateMcqMark(StudentAnswerModel ans, ExamQuestionModel question, int maxPoints) {
        if (ans.getSelectedOptions() == null || ans.getSelectedOptions().isEmpty()) {
            return 0.0;
        }
        int correctCount = UtilFunctions.countCommonUnique(ans.getSelectedOptions(), question.getCorrectOptionIndex());
        return ((double) correctCount / ans.getSelectedOptions().size()) * maxPoints;
    }

    @Transactional(readOnly = true)
    public StudentExamAnswersResponseDto getStudentExamAnswers(Long teacherId, Long studentExamId) {
        StudentExamModel studentExam = studentExamRepository.findById(studentExamId)
                .orElseThrow(() -> new NotFoundException("Student exam not found!"));

        if (!studentExam.getExam().getLesson().getTeacher().getId().equals(teacherId)) {
            throw new UnauthorizedException("You do not have permission to view this exam!");
        }

        List<StudentAnswerModel> answers = studentAnswerRepository.findByStudentExam(studentExam);

        return new StudentExamAnswersResponseDto(
                studentExam.getStudent().getFullName(),
                studentExam.getGrade(),
                answers.stream().map(a -> new StudentAnswerResponseDto(
                        new QuestionDto(
                                a.getQuestion().getId(),
                                a.getQuestion().getText(),
                                a.getQuestion().getType(),
                                a.getQuestion().getOptions(),
                                a.getQuestion().getMaxPoints()),
                        a.getSelectedOptions(),
                        a.getTextAnswer(),
                        a.getMark())).toList());
    }

    @Transactional
    public StudentExamDto verifyStudentExam(Long teacherId, Long studentExamId, List<AnswerMarkDto> answers) {
        StudentExamModel studentExam = studentExamRepository.findById(studentExamId)
                .orElseThrow(() -> new NotFoundException("Student exam not found!"));

        if (!studentExam.getExam().getLesson().getTeacher().getId().equals(teacherId)) {
            throw new UnauthorizedException("You do not have permission to verify this exam!");
        }

        if (answers != null && !answers.isEmpty()) {
            for (AnswerMarkDto markDto : answers) {
                StudentAnswerModel answer = studentAnswerRepository.findById(markDto.answerId())
                        .orElseThrow(() -> new NotFoundException(
                                "Answer not found: " + markDto.answerId()));

                if (!answer.getStudentExam().getId().equals(studentExamId)) {
                    throw new UnauthorizedException("Answer " + markDto.answerId()
                            + " does not belong to this exam!");
                }

                answer.setMark(markDto.mark());
                studentAnswerRepository.save(answer);
            }
        }

        List<StudentAnswerModel> allAnswers = studentAnswerRepository.findByStudentExam(studentExam);
        double totalGrade = allAnswers.stream()
                .mapToDouble(a -> a.getMark() != null ? a.getMark() : 0.0)
                .sum();

        studentExam.setGrade((int) Math.round(totalGrade));
        studentExam.setVerified(true);
        studentExamRepository.save(studentExam);

        return new StudentExamDto(
                studentExam.getId(),
                studentExam.getStudent().getFullName(),
                studentExam.getGrade(),
                studentExam.getVerified());
    }
}