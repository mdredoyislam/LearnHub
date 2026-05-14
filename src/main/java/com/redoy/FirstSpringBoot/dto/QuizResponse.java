package com.redoy.FirstSpringBoot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {
    private Long quizId;
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    // correctAnswer is only sent to teacher/admin, not to students during exam
    private String correctAnswer;
    private Long courseId;
}
