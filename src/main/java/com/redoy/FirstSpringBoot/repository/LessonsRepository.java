package com.redoy.FirstSpringBoot.repository;

import com.redoy.FirstSpringBoot.entity.Courses;
import com.redoy.FirstSpringBoot.entity.Lessons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonsRepository extends JpaRepository<Lessons, Long> {
    List<Lessons> findByCourseOrderByCreatedAtAsc(Courses course);
    long countByCourse(Courses course);
}
