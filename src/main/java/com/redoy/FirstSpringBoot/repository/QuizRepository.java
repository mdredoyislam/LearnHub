package com.redoy.FirstSpringBoot.repository;

import com.redoy.FirstSpringBoot.entity.Courses;
import com.redoy.FirstSpringBoot.entity.Quiz;
import com.redoy.FirstSpringBoot.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByCourse(Courses course);
    List<Quiz> findByCourseAndTeacher(Courses course, UserAccount teacher);
    long countByCourse(Courses course);
}
