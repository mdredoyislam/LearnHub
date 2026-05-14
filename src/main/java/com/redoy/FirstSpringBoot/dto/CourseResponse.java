package com.redoy.FirstSpringBoot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private Long coursesId;
    private String courseName;
    private String photo;
    private BigDecimal courseAmount;
    private String pdfBook;
    private String description;
    private boolean isFree;
    private Long teacherId;
    private String teacherName;
    private long lessonCount;
    private long quizCount;
    private long enrollmentCount;
    private LocalDateTime createdAt;
}
