package com.redoy.FirstSpringBoot.service;

import com.redoy.FirstSpringBoot.dto.QuizRequest;
import com.redoy.FirstSpringBoot.dto.QuizResponse;
import com.redoy.FirstSpringBoot.entity.Courses;
import com.redoy.FirstSpringBoot.entity.Quiz;
import com.redoy.FirstSpringBoot.entity.UserAccount;
import com.redoy.FirstSpringBoot.exception.ResourceNotFoundException;
import com.redoy.FirstSpringBoot.exception.UnauthorizedException;
import com.redoy.FirstSpringBoot.repository.CoursesRepository;
import com.redoy.FirstSpringBoot.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final CoursesRepository coursesRepository;

    public List<QuizResponse> getQuizByCourse(Long courseId) {
        Courses course = coursesRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));
        return quizRepository.findByCourse(course).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<QuizResponse> getQuizForStudent(Long courseId) {
        Courses course = coursesRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));
        return quizRepository.findByCourse(course).stream()
                .map(q -> QuizResponse.builder()
                        .quizId(q.getQuizId())
                        .question(q.getQuestion())
                        .optionA(q.getOptionA())
                        .optionB(q.getOptionB())
                        .optionC(q.getOptionC())
                        .courseId(q.getCourse().getCoursesId())
                        .build())
                .collect(Collectors.toList());
    }

    public QuizResponse addQuiz(Long courseId, QuizRequest request, UserAccount teacher) {
        Courses course = coursesRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));
        if (!course.getTeacher().getUserAccountId().equals(teacher.getUserAccountId())) {
            throw new UnauthorizedException("You don't own this course");
        }
        Quiz quiz = Quiz.builder()
                .question(request.getQuestion())
                .optionA(request.getOptionA())
                .optionB(request.getOptionB())
                .optionC(request.getOptionC())
                .correctAnswer(request.getCorrectAnswer())
                .course(course)
                .teacher(teacher)
                .build();
        return mapToResponse(quizRepository.save(quiz));
    }

    public QuizResponse updateQuiz(Long quizId, QuizRequest request, UserAccount teacher) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found: " + quizId));
        if (!quiz.getTeacher().getUserAccountId().equals(teacher.getUserAccountId())) {
            throw new UnauthorizedException("You don't own this quiz question");
        }
        quiz.setQuestion(request.getQuestion());
        quiz.setOptionA(request.getOptionA());
        quiz.setOptionB(request.getOptionB());
        quiz.setOptionC(request.getOptionC());
        quiz.setCorrectAnswer(request.getCorrectAnswer());
        return mapToResponse(quizRepository.save(quiz));
    }

    public void deleteQuiz(Long quizId, UserAccount teacher) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found: " + quizId));
        if (!quiz.getTeacher().getUserAccountId().equals(teacher.getUserAccountId())) {
            throw new UnauthorizedException("You don't own this quiz question");
        }
        quizRepository.delete(quiz);
    }

    private QuizResponse mapToResponse(Quiz q) {
        return QuizResponse.builder()
                .quizId(q.getQuizId())
                .question(q.getQuestion())
                .optionA(q.getOptionA())
                .optionB(q.getOptionB())
                .optionC(q.getOptionC())
                .correctAnswer(q.getCorrectAnswer())
                .courseId(q.getCourse().getCoursesId())
                .build();
    }
}
