package com.redoy.FirstSpringBoot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CourseRequest {
    @NotBlank
    private String courseName;
    private String photo;
    private BigDecimal courseAmount;
    private String pdfBook;
    private String description;
    @NotNull
    private boolean isFree;
}
