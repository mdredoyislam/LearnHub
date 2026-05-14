package com.redoy.FirstSpringBoot.service;

import com.redoy.FirstSpringBoot.dto.LessonRequest;
import com.redoy.FirstSpringBoot.dto.LessonResponse;
import com.redoy.FirstSpringBoot.entity.Courses;
import com.redoy.FirstSpringBoot.entity.Lessons;
import com.redoy.FirstSpringBoot.entity.UserAccount;
import com.redoy.FirstSpringBoot.exception.ResourceNotFoundException;
import com.redoy.FirstSpringBoot.exception.UnauthorizedException;
import com.redoy.FirstSpringBoot.repository.CoursesRepository;
import com.redoy.FirstSpringBoot.repository.LessonsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonsRepository lessonsRepository;
    private final CoursesRepository coursesRepository;

    public List<LessonResponse> getLessonsByCourse(Long courseId) {
        Courses course = coursesRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));
        return lessonsRepository.findByCourseOrderByCreatedAtAsc(course)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public LessonResponse addLesson(Long courseId, LessonRequest request, UserAccount teacher) {
        Courses course = coursesRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));
        if (!course.getTeacher().getUserAccountId().equals(teacher.getUserAccountId())) {
            throw new UnauthorizedException("You don't own this course");
        }
        Lessons lesson = Lessons.builder()
                .lessonTitle(request.getLessonTitle())
                .videoUrl(request.getVideoUrl())
                .videoType(request.getVideoType())
                .description(request.getDescription())
                .course(course)
                .build();
        return mapToResponse(lessonsRepository.save(lesson));
    }

    public LessonResponse updateLesson(Long lessonId, LessonRequest request, UserAccount teacher) {
        Lessons lesson = lessonsRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found: " + lessonId));
        if (!lesson.getCourse().getTeacher().getUserAccountId().equals(teacher.getUserAccountId())) {
            throw new UnauthorizedException("You don't own this lesson");
        }
        lesson.setLessonTitle(request.getLessonTitle());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setVideoType(request.getVideoType());
        lesson.setDescription(request.getDescription());
        return mapToResponse(lessonsRepository.save(lesson));
    }

    public void deleteLesson(Long lessonId, UserAccount teacher) {
        Lessons lesson = lessonsRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found: " + lessonId));
        if (!lesson.getCourse().getTeacher().getUserAccountId().equals(teacher.getUserAccountId())) {
            throw new UnauthorizedException("You don't own this lesson");
        }
        lessonsRepository.delete(lesson);
    }

    private LessonResponse mapToResponse(Lessons lesson) {
        return LessonResponse.builder()
                .lessonsId(lesson.getLessonsId())
                .lessonTitle(lesson.getLessonTitle())
                .videoUrl(lesson.getVideoUrl())
                .videoType(lesson.getVideoType())
                .description(lesson.getDescription())
                .courseId(lesson.getCourse().getCoursesId())
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt())
                .build();
    }
}
