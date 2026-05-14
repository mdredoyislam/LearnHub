package com.redoy.FirstSpringBoot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuizRequest {
    @NotBlank
    private String question;
    @NotBlank
    private String optionA;
    @NotBlank
    private String optionB;
    private String optionC;
    @NotBlank
    private String correctAnswer;
}
