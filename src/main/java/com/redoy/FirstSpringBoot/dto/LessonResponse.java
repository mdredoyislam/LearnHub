package com.redoy.FirstSpringBoot.dto;

import com.redoy.FirstSpringBoot.entity.Lessons;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonResponse {
    private Long lessonsId;
    private String lessonTitle;
    private String videoUrl;
    private Lessons.VideoType videoType;
    private String description;
    private Long courseId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
