package com.redoy.FirstSpringBoot.service;

import com.redoy.FirstSpringBoot.dto.AnalyticsResponse;
import com.redoy.FirstSpringBoot.dto.UserProfileDto;
import com.redoy.FirstSpringBoot.entity.IncomeRecord;
import com.redoy.FirstSpringBoot.entity.UserAccount;
import com.redoy.FirstSpringBoot.exception.ResourceNotFoundException;
import com.redoy.FirstSpringBoot.repository.IncomeRecordRepository;
import com.redoy.FirstSpringBoot.repository.CoursesRepository;
import com.redoy.FirstSpringBoot.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserAccountRepository userAccountRepository;
    private final CoursesRepository coursesRepository;
    private final IncomeRecordRepository incomeRecordRepository;

    public List<UserProfileDto> getAllUsers() {
        return userAccountRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public UserProfileDto updateUserRole(Long userId, String role) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        user.setRole(UserAccount.Role.valueOf(role.toUpperCase()));
        userAccountRepository.save(user);
        return mapToDto(user);
    }

    public UserProfileDto updateUserStatus(Long userId, boolean status) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        user.setStatus(status);
        userAccountRepository.save(user);
        return mapToDto(user);
    }

    public void deleteUser(Long userId) {
        if (!userAccountRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        userAccountRepository.deleteById(userId);
    }

    public List<IncomeRecord> getPendingEnrollments() {
        return incomeRecordRepository.findByStatus("PENDING");
    }

    @Transactional
    public void approveEnrollment(Long recordId) {
        IncomeRecord record = incomeRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));
        record.setStatus("APPROVED");
        incomeRecordRepository.save(record);
    }

    @Transactional
    public void rejectEnrollment(Long recordId) {
        IncomeRecord record = incomeRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));
        record.setStatus("REJECTED");
        incomeRecordRepository.save(record);
    }

    public AnalyticsResponse getAnalytics() {
        long totalUsers = userAccountRepository.count();
        long totalStudents = userAccountRepository.countByRole(UserAccount.Role.STUDENT);
        long totalTeachers = userAccountRepository.countByRole(UserAccount.Role.TEACHER);
        long totalAdmins = userAccountRepository.countByRole(UserAccount.Role.ADMIN);
        long totalCourses = coursesRepository.count();
        long totalEnrollments = incomeRecordRepository.count();
        
        BigDecimal totalRevenue = incomeRecordRepository.getTotalRevenue();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        BigDecimal totalCommission = incomeRecordRepository.getTotalCommission();
        if (totalCommission == null) totalCommission = BigDecimal.ZERO;

        return AnalyticsResponse.builder()
                .totalUsers(totalUsers)
                .totalStudents(totalStudents)
                .totalTeachers(totalTeachers)
                .totalAdmins(totalAdmins)
                .totalCourses(totalCourses)
                .totalEnrollments(totalEnrollments)
                .totalRevenue(totalRevenue)
                .totalCommission(totalCommission)
                .build();
    }

    private UserProfileDto mapToDto(UserAccount user) {
        return UserProfileDto.builder()
                .userAccountId(user.getUserAccountId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profilePhoto(user.getProfilePhoto())
                .status(user.isStatus())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
