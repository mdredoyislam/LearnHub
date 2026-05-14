package com.redoy.FirstSpringBoot.controller;

import com.redoy.FirstSpringBoot.dto.AnalyticsResponse;
import com.redoy.FirstSpringBoot.dto.CourseResponse;
import com.redoy.FirstSpringBoot.dto.UserProfileDto;
import com.redoy.FirstSpringBoot.entity.IncomeRecord;
import com.redoy.FirstSpringBoot.entity.WithdrawRequest;
import com.redoy.FirstSpringBoot.service.AdminService;
import com.redoy.FirstSpringBoot.service.CourseService;
import com.redoy.FirstSpringBoot.service.WithdrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final CourseService courseService;
    private final WithdrawService withdrawService;

    @GetMapping("/users")
    public ResponseEntity<List<UserProfileDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<UserProfileDto> updateRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(adminService.updateUserRole(id, body.get("role")));
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<UserProfileDto> updateStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        return ResponseEntity.ok(adminService.updateUserStatus(id, body.get("status")));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    @GetMapping("/analytics")
    public ResponseEntity<AnalyticsResponse> getAnalytics() {
        return ResponseEntity.ok(adminService.getAnalytics());
    }

    @GetMapping("/enrollments/pending")
    public ResponseEntity<List<IncomeRecord>> getPendingEnrollments() {
        return ResponseEntity.ok(adminService.getPendingEnrollments());
    }

    @PostMapping("/enrollments/{id}/approve")
    public ResponseEntity<?> approveEnrollment(@PathVariable Long id) {
        adminService.approveEnrollment(id);
        return ResponseEntity.ok(Map.of("message", "Enrollment approved"));
    }

    @PostMapping("/enrollments/{id}/reject")
    public ResponseEntity<?> rejectEnrollment(@PathVariable Long id) {
        adminService.rejectEnrollment(id);
        return ResponseEntity.ok(Map.of("message", "Enrollment rejected"));
    }

    @GetMapping("/courses")
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllPublicCourses());
    }

    @GetMapping("/withdrawals/pending")
    public ResponseEntity<List<WithdrawRequest>> getPendingWithdrawals() {
        return ResponseEntity.ok(withdrawService.getAllPendingWithdrawals());
    }

    @PostMapping("/withdrawals/{id}/approve")
    public ResponseEntity<?> approveWithdraw(@PathVariable Long id) {
        withdrawService.approveWithdraw(id);
        return ResponseEntity.ok(Map.of("message", "Withdrawal approved"));
    }

    @PostMapping("/withdrawals/{id}/reject")
    public ResponseEntity<?> rejectWithdraw(@PathVariable Long id) {
        withdrawService.rejectWithdraw(id);
        return ResponseEntity.ok(Map.of("message", "Withdrawal rejected"));
    }
}
