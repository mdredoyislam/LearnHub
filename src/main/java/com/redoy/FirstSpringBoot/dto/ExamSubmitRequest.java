package com.redoy.FirstSpringBoot.dto;

import lombok.Data;
import java.util.Map;

@Data
public class ExamSubmitRequest {
    private Long courseId;
    // Map of quizId -> selectedAnswer
    private Map<Long, String> answers;
}
