package com.redoy.FirstSpringBoot.repository;

import com.redoy.FirstSpringBoot.entity.Courses;
import com.redoy.FirstSpringBoot.entity.Exam;
import com.redoy.FirstSpringBoot.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    Optional<Exam> findByStudentAndCourse(UserAccount student, Courses course);
    boolean existsByStudentAndCourse(UserAccount student, Courses course);
}
