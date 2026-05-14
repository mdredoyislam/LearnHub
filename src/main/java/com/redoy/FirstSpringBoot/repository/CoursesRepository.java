package com.redoy.FirstSpringBoot.repository;

import com.redoy.FirstSpringBoot.entity.Courses;
import com.redoy.FirstSpringBoot.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CoursesRepository extends JpaRepository<Courses, Long> {
    List<Courses> findByTeacher(UserAccount teacher);
    long count();

    @Query("SELECT COALESCE(SUM(ir.amount), 0) FROM IncomeRecord ir WHERE ir.teacher = :teacher")
    BigDecimal getTotalEarningsByTeacher(UserAccount teacher);

    @Query("SELECT COUNT(ir) FROM IncomeRecord ir WHERE ir.course = :course")
    long getEnrollmentCount(Courses course);
}
