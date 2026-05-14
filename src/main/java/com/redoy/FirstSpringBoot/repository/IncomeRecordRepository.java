package com.redoy.FirstSpringBoot.repository;

import com.redoy.FirstSpringBoot.entity.Courses;
import com.redoy.FirstSpringBoot.entity.IncomeRecord;
import com.redoy.FirstSpringBoot.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeRecordRepository extends JpaRepository<IncomeRecord, Long> {
    Optional<IncomeRecord> findFirstByStudentAndCourseOrderByIncomeRecordIdDesc(UserAccount student, Courses course);
    boolean existsByStudentAndCourse(UserAccount student, Courses course);
    boolean existsByStudentAndCourseAndStatus(UserAccount student, Courses course, String status);
    
    List<IncomeRecord> findByTeacher(UserAccount teacher);
    List<IncomeRecord> findByTeacherAndStatus(UserAccount teacher, String status);
    List<IncomeRecord> findByStudent(UserAccount student);
    List<IncomeRecord> findByStudentAndStatus(UserAccount student, String status);
    List<IncomeRecord> findByStatus(String status);

    @Query("SELECT COALESCE(SUM(ir.amount), 0) FROM IncomeRecord ir WHERE ir.status = 'APPROVED'")
    BigDecimal getTotalRevenue();

    @Query("SELECT COALESCE(SUM(ir.siteCommission), 0) FROM IncomeRecord ir WHERE ir.status = 'APPROVED'")
    BigDecimal getTotalCommission();

    @Query("SELECT COALESCE(SUM(ir.amount - ir.siteCommission), 0) FROM IncomeRecord ir WHERE ir.teacher = :teacher AND ir.status = 'APPROVED'")
    BigDecimal getTotalEarningsByTeacher(UserAccount teacher);
}
