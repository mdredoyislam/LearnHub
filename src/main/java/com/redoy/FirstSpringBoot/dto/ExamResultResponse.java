package com.redoy.FirstSpringBoot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResultResponse {
    private Long examId;
    private int examMark;
    private int totalQuestions;
    private boolean passed;
    private boolean certificateEligible;
    private String studentName;
    private String courseName;
    private LocalDateTime submittedAt;
}
