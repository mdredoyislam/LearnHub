package com.redoy.FirstSpringBoot.service;

import com.redoy.FirstSpringBoot.dto.ExamResultResponse;
import com.redoy.FirstSpringBoot.dto.ExamSubmitRequest;
import com.redoy.FirstSpringBoot.entity.*;
import com.redoy.FirstSpringBoot.exception.ResourceNotFoundException;
import com.redoy.FirstSpringBoot.exception.UnauthorizedException;
import com.redoy.FirstSpringBoot.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final ResponseRepository responseRepository;
    private final QuizRepository quizRepository;
    private final CoursesRepository coursesRepository;
    private final IncomeRecordRepository incomeRecordRepository;

    @Transactional
    public ExamResultResponse submitExam(ExamSubmitRequest request, UserAccount student) {
        Courses course = coursesRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if (examRepository.existsByStudentAndCourse(student, course)) {
            throw new IllegalArgumentException("You already submitted the exam for this course");
        }
        IncomeRecord incomeRecord = null;
        if (!course.isFree()) {
            incomeRecord = incomeRecordRepository.findFirstByStudentAndCourseOrderByIncomeRecordIdDesc(student, course)
                    .orElseThrow(() -> new UnauthorizedException("Enroll before taking the exam"));
        }
        List<Quiz> questions = quizRepository.findByCourse(course);
        if (questions.isEmpty()) throw new IllegalArgumentException("No quiz questions for this course");

        Exam exam = Exam.builder().student(student).course(course).incomeRecord(incomeRecord).examMark(0).build();
        exam = examRepository.save(exam);

        Map<Long, String> answers = request.getAnswers();
        int correct = 0;
        List<Response> responses = new ArrayList<>();
        for (Quiz q : questions) {
            String sel = answers.getOrDefault(q.getQuizId(), "");
            boolean ok = q.getCorrectAnswer().equalsIgnoreCase(sel);
            if (ok) correct++;
            responses.add(Response.builder().quiz(q).exam(exam).selectedAnswer(sel).correct(ok).build());
        }
        responseRepository.saveAll(responses);
        int mark = (int) Math.round((correct * 100.0) / questions.size());
        exam.setExamMark(mark);
        examRepository.save(exam);
        return buildResult(exam, mark, questions.size(), student, course);
    }

    public ExamResultResponse getExamResult(Long courseId, UserAccount student) {
        Courses course = coursesRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        Exam exam = examRepository.findByStudentAndCourse(student, course)
                .orElseThrow(() -> new ResourceNotFoundException("No exam found for this course"));
        int total = (int) quizRepository.countByCourse(course);
        return buildResult(exam, exam.getExamMark(), total, student, course);
    }

    private ExamResultResponse buildResult(Exam exam, int mark, int total, UserAccount student, Courses course) {
        return ExamResultResponse.builder()
                .examId(exam.getExamId()).examMark(mark).totalQuestions(total)
                .passed(mark >= 50).certificateEligible(mark >= 50)
                .studentName(student.getFullName()).courseName(course.getCourseName())
                .submittedAt(exam.getSubmittedAt()).build();
    }
}
