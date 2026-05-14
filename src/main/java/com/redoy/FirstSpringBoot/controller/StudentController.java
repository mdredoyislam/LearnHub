package com.redoy.FirstSpringBoot.controller;

import com.redoy.FirstSpringBoot.dto.*;
import com.redoy.FirstSpringBoot.entity.UserAccount;
import com.redoy.FirstSpringBoot.repository.UserAccountRepository;
import com.redoy.FirstSpringBoot.service.CourseService;
import com.redoy.FirstSpringBoot.service.ExamService;
import com.redoy.FirstSpringBoot.service.LessonService;
import com.redoy.FirstSpringBoot.service.PaymentService;
import com.redoy.FirstSpringBoot.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final CourseService courseService;
    private final LessonService lessonService;
    private final QuizService quizService;
    private final PaymentService paymentService;
    private final ExamService examService;
    private final UserAccountRepository userAccountRepository;

    private UserAccount getUser(UserDetails ud) {
        return userAccountRepository.findByEmail(ud.getUsername()).orElseThrow();
    }

    @GetMapping("/courses")
    public ResponseEntity<List<CourseResponse>> browseCourses() {
        return ResponseEntity.ok(courseService.getAllPublicCourses());
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<CourseResponse> getCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PostMapping("/enroll/free/{courseId}")
    public ResponseEntity<CourseResponse> enrollFree(@PathVariable Long courseId,
                                                     @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(paymentService.enrollFree(courseId, getUser(ud)));
    }

    @PostMapping("/enroll/paid/{courseId}")
    public ResponseEntity<?> enrollPaid(@PathVariable Long courseId,
                                        @RequestBody Map<String, String> body,
                                        @AuthenticationPrincipal UserDetails ud) {
        String transactionId = body.get("transactionId");
        String method = body.get("method");
        if (transactionId == null || method == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Transaction ID and Method are required"));
        }
        paymentService.submitPaidEnrollment(courseId, transactionId, method, getUser(ud));
        return ResponseEntity.ok(Map.of("message", "Enrollment request submitted. Waiting for approval."));
    }

    @GetMapping("/my-courses")
    public ResponseEntity<List<CourseResponse>> getMyCourses(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(paymentService.getEnrolledCourses(getUser(ud)));
    }

    @GetMapping("/courses/{courseId}/lessons")
    public ResponseEntity<List<LessonResponse>> getLessons(@PathVariable Long courseId,
                                                           @AuthenticationPrincipal UserDetails ud) {
        if (!paymentService.isEnrolled(courseId, getUser(ud))) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(lessonService.getLessonsByCourse(courseId));
    }

    @GetMapping("/courses/{courseId}/quiz")
    public ResponseEntity<List<QuizResponse>> getQuiz(@PathVariable Long courseId,
                                                      @AuthenticationPrincipal UserDetails ud) {
        if (!paymentService.isEnrolled(courseId, getUser(ud))) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(quizService.getQuizForStudent(courseId));
    }

    @PostMapping("/exam/submit")
    public ResponseEntity<ExamResultResponse> submitExam(@RequestBody ExamSubmitRequest request,
                                                         @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(examService.submitExam(request, getUser(ud)));
    }

    @GetMapping("/exam/{courseId}/result")
    public ResponseEntity<ExamResultResponse> getExamResult(@PathVariable Long courseId,
                                                            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(examService.getExamResult(courseId, getUser(ud)));
    }

    @GetMapping("/courses/{courseId}/enrolled")
    public ResponseEntity<Map<String, Object>> checkEnrolled(@PathVariable Long courseId,
                                                               @AuthenticationPrincipal UserDetails ud) {
        String status = paymentService.getEnrollmentStatus(courseId, getUser(ud));
        return ResponseEntity.ok(Map.of(
            "enrolled", "APPROVED".equals(status),
            "status", status
        ));
    }
}
