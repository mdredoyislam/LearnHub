package com.redoy.FirstSpringBoot.repository;

import com.redoy.FirstSpringBoot.entity.Exam;
import com.redoy.FirstSpringBoot.entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {
    List<Response> findByExam(Exam exam);
}
