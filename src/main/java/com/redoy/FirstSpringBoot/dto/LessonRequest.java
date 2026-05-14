package com.redoy.FirstSpringBoot.dto;

import com.redoy.FirstSpringBoot.entity.Lessons;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LessonRequest {
    @NotBlank
    private String lessonTitle;
    private String videoUrl;
    @NotNull
    private Lessons.VideoType videoType;
    private String description;
}
