package com.redoy.FirstSpringBoot.controller;

import com.redoy.FirstSpringBoot.dto.*;
import com.redoy.FirstSpringBoot.entity.UserAccount;
import com.redoy.FirstSpringBoot.repository.UserAccountRepository;
import com.redoy.FirstSpringBoot.service.CourseService;
import com.redoy.FirstSpringBoot.service.LessonService;
import com.redoy.FirstSpringBoot.service.PaymentService;
import com.redoy.FirstSpringBoot.service.QuizService;
import jakarta.validation.Valid;
import com.redoy.FirstSpringBoot.entity.WithdrawRequest;
import com.redoy.FirstSpringBoot.service.WithdrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final CourseService courseService;
    private final LessonService lessonService;
    private final QuizService quizService;
    private final PaymentService paymentService;
    private final WithdrawService withdrawService;
    private final UserAccountRepository userAccountRepository;

    private UserAccount getUser(UserDetails ud) {
        return userAccountRepository.findByEmail(ud.getUsername()).orElseThrow();
    }

    // Courses
    @GetMapping("/courses")
    public ResponseEntity<List<CourseResponse>> getMyCourses(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(courseService.getCoursesByTeacher(getUser(ud)));
    }

    @PostMapping("/courses")
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseRequest request,
                                                       @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(courseService.createCourse(request, getUser(ud)));
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable Long id,
                                                       @Valid @RequestBody CourseRequest request,
                                                       @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(courseService.updateCourse(id, request, getUser(ud)));
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Map<String, String>> deleteCourse(@PathVariable Long id,
                                                             @AuthenticationPrincipal UserDetails ud) {
        courseService.deleteCourse(id, getUser(ud));
        return ResponseEntity.ok(Map.of("message", "Course deleted"));
    }

    // Lessons
    @GetMapping("/courses/{courseId}/lessons")
    public ResponseEntity<List<LessonResponse>> getLessons(@PathVariable Long courseId) {
        return ResponseEntity.ok(lessonService.getLessonsByCourse(courseId));
    }

    @PostMapping("/courses/{courseId}/lessons")
    public ResponseEntity<LessonResponse> addLesson(@PathVariable Long courseId,
                                                    @Valid @RequestBody LessonRequest request,
                                                    @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(lessonService.addLesson(courseId, request, getUser(ud)));
    }

    @PutMapping("/lessons/{id}")
    public ResponseEntity<LessonResponse> updateLesson(@PathVariable Long id,
                                                       @Valid @RequestBody LessonRequest request,
                                                       @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(lessonService.updateLesson(id, request, getUser(ud)));
    }

    @DeleteMapping("/lessons/{id}")
    public ResponseEntity<Map<String, String>> deleteLesson(@PathVariable Long id,
                                                             @AuthenticationPrincipal UserDetails ud) {
        lessonService.deleteLesson(id, getUser(ud));
        return ResponseEntity.ok(Map.of("message", "Lesson deleted"));
    }

    // Quiz
    @GetMapping("/courses/{courseId}/quiz")
    public ResponseEntity<List<QuizResponse>> getQuiz(@PathVariable Long courseId) {
        return ResponseEntity.ok(quizService.getQuizByCourse(courseId));
    }

    @PostMapping("/courses/{courseId}/quiz")
    public ResponseEntity<QuizResponse> addQuiz(@PathVariable Long courseId,
                                                @Valid @RequestBody QuizRequest request,
                                                @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(quizService.addQuiz(courseId, request, getUser(ud)));
    }

    @PutMapping("/quiz/{id}")
    public ResponseEntity<QuizResponse> updateQuiz(@PathVariable Long id,
                                                   @Valid @RequestBody QuizRequest request,
                                                   @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(quizService.updateQuiz(id, request, getUser(ud)));
    }

    @DeleteMapping("/quiz/{id}")
    public ResponseEntity<Map<String, String>> deleteQuiz(@PathVariable Long id,
                                                           @AuthenticationPrincipal UserDetails ud) {
        quizService.deleteQuiz(id, getUser(ud));
        return ResponseEntity.ok(Map.of("message", "Quiz deleted"));
    }

    // Earnings
    @GetMapping("/earnings")
    public ResponseEntity<BigDecimal> getEarnings(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(paymentService.getTeacherEarnings(getUser(ud)));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> requestWithdraw(@RequestBody Map<String, Object> body, @AuthenticationPrincipal UserDetails ud) {
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        String details = (String) body.get("details");
        withdrawService.requestWithdraw(getUser(ud), amount, details);
        return ResponseEntity.ok(Map.of("message", "Withdrawal request submitted"));
    }

    @GetMapping("/withdrawals")
    public ResponseEntity<List<WithdrawRequest>> getWithdrawals(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(withdrawService.getTeacherWithdrawals(getUser(ud)));
    }

    // Enrollments
    @GetMapping("/enrollments/pending")
    public ResponseEntity<List<com.redoy.FirstSpringBoot.entity.IncomeRecord>> getPendingEnrollments(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(paymentService.getPendingEnrollmentsByTeacher(getUser(ud)));
    }

    @PostMapping("/enrollments/{id}/approve")
    public ResponseEntity<?> approveEnrollment(@PathVariable Long id, @AuthenticationPrincipal UserDetails ud) {
        paymentService.approveEnrollmentByTeacher(id, getUser(ud));
        return ResponseEntity.ok(Map.of("message", "Enrollment approved"));
    }

    @PostMapping("/enrollments/{id}/reject")
    public ResponseEntity<?> rejectEnrollment(@PathVariable Long id, @AuthenticationPrincipal UserDetails ud) {
        paymentService.rejectEnrollmentByTeacher(id, getUser(ud));
        return ResponseEntity.ok(Map.of("message", "Enrollment rejected"));
    }
}
