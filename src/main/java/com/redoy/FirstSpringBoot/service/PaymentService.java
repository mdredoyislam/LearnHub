package com.redoy.FirstSpringBoot.service;

import com.redoy.FirstSpringBoot.dto.CourseResponse;
import com.redoy.FirstSpringBoot.entity.Courses;
import com.redoy.FirstSpringBoot.entity.IncomeRecord;
import com.redoy.FirstSpringBoot.entity.UserAccount;
import com.redoy.FirstSpringBoot.exception.ResourceNotFoundException;
import com.redoy.FirstSpringBoot.repository.CoursesRepository;
import com.redoy.FirstSpringBoot.repository.IncomeRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final IncomeRecordRepository incomeRecordRepository;
    private final CoursesRepository coursesRepository;
    private final CourseService courseService;

    private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.10"); // 10% Site Commission

    @Transactional
    public CourseResponse enrollFree(Long courseId, UserAccount student) {
        Courses course = coursesRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        if (!course.isFree()) throw new IllegalArgumentException("This course requires payment");
        
        if (incomeRecordRepository.existsByStudentAndCourseAndStatus(student, course, "APPROVED")) {
            throw new IllegalArgumentException("Already enrolled in this course");
        }

        // Free courses are auto-approved
        IncomeRecord record = IncomeRecord.builder()
                .teacher(course.getTeacher())
                .student(student)
                .course(course)
                .amount(BigDecimal.ZERO)
                .siteCommission(BigDecimal.ZERO)
                .status("APPROVED")
                .paymentMethod("FREE")
                .build();
        incomeRecordRepository.save(record);
        return courseService.mapToResponse(course);
    }

    @Transactional
    public void submitPaidEnrollment(Long courseId, String transactionId, String method, UserAccount student) {
        Courses course = coursesRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        if (course.isFree()) throw new IllegalArgumentException("This course is free");
        
        incomeRecordRepository.findFirstByStudentAndCourseOrderByIncomeRecordIdDesc(student, course).ifPresent(existing -> {
            if ("APPROVED".equals(existing.getStatus())) {
                throw new IllegalArgumentException("Already enrolled and approved");
            } else if ("PENDING".equals(existing.getStatus())) {
                throw new IllegalArgumentException("Enrollment request is already pending approval");
            }
        });

        BigDecimal amount = course.getCourseAmount();
        BigDecimal commission = amount.multiply(COMMISSION_RATE);

        IncomeRecord record = IncomeRecord.builder()
                .teacher(course.getTeacher())
                .student(student)
                .course(course)
                .amount(amount)
                .siteCommission(commission)
                .transactionId(transactionId)
                .paymentMethod(method)
                .status("PENDING")
                .build();
        
        incomeRecordRepository.save(record);
    }

    public List<CourseResponse> getEnrolledCourses(UserAccount student) {
        return incomeRecordRepository.findByStudentAndStatus(student, "APPROVED").stream()
                .map(ir -> courseService.mapToResponse(ir.getCourse()))
                .collect(Collectors.toList());
    }

    public boolean isEnrolled(Long courseId, UserAccount student) {
        Courses course = coursesRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        return incomeRecordRepository.existsByStudentAndCourseAndStatus(student, course, "APPROVED");
    }

    public String getEnrollmentStatus(Long courseId, UserAccount student) {
        Courses course = coursesRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        return incomeRecordRepository.findFirstByStudentAndCourseOrderByIncomeRecordIdDesc(student, course)
                .map(IncomeRecord::getStatus)
                .orElse("NONE");
    }

    public BigDecimal getTeacherEarnings(UserAccount teacher) {
        BigDecimal total = incomeRecordRepository.findByTeacherAndStatus(teacher, "APPROVED").stream()
                .map(ir -> ir.getAmount().subtract(ir.getSiteCommission()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total;
    }

    public List<IncomeRecord> getPendingEnrollmentsByTeacher(UserAccount teacher) {
        return incomeRecordRepository.findByTeacherAndStatus(teacher, "PENDING");
    }

    @Transactional
    public void approveEnrollmentByTeacher(Long recordId, UserAccount teacher) {
        IncomeRecord record = incomeRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));
        if (!record.getTeacher().getUserAccountId().equals(teacher.getUserAccountId())) {
            throw new IllegalArgumentException("Not authorized to approve this enrollment");
        }
        record.setStatus("APPROVED");
        incomeRecordRepository.save(record);
    }

    @Transactional
    public void rejectEnrollmentByTeacher(Long recordId, UserAccount teacher) {
        IncomeRecord record = incomeRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));
        if (!record.getTeacher().getUserAccountId().equals(teacher.getUserAccountId())) {
            throw new IllegalArgumentException("Not authorized to reject this enrollment");
        }
        record.setStatus("REJECTED");
        incomeRecordRepository.save(record);
    }
}
