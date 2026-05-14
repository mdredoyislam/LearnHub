package com.redoy.FirstSpringBoot.service;

import com.redoy.FirstSpringBoot.dto.CourseRequest;
import com.redoy.FirstSpringBoot.dto.CourseResponse;
import com.redoy.FirstSpringBoot.entity.Courses;
import com.redoy.FirstSpringBoot.entity.UserAccount;
import com.redoy.FirstSpringBoot.exception.ResourceNotFoundException;
import com.redoy.FirstSpringBoot.exception.UnauthorizedException;
import com.redoy.FirstSpringBoot.repository.CoursesRepository;
import com.redoy.FirstSpringBoot.repository.IncomeRecordRepository;
import com.redoy.FirstSpringBoot.repository.LessonsRepository;
import com.redoy.FirstSpringBoot.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CoursesRepository coursesRepository;
    private final LessonsRepository lessonsRepository;
    private final QuizRepository quizRepository;
    private final IncomeRecordRepository incomeRecordRepository;

    public List<CourseResponse> getAllPublicCourses() {
        return coursesRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CourseResponse getCourseById(Long id) {
        Courses course = coursesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + id));
        return mapToResponse(course);
    }

    public List<CourseResponse> getCoursesByTeacher(UserAccount teacher) {
        return coursesRepository.findByTeacher(teacher).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CourseResponse createCourse(CourseRequest request, UserAccount teacher) {
        Courses course = Courses.builder()
                .courseName(request.getCourseName())
                .photo(request.getPhoto())
                .courseAmount(request.getCourseAmount())
                .pdfBook(request.getPdfBook())
                .description(request.getDescription())
                .isFree(request.isFree())
                .teacher(teacher)
                .build();
        return mapToResponse(coursesRepository.save(course));
    }

    public CourseResponse updateCourse(Long courseId, CourseRequest request, UserAccount teacher) {
        Courses course = coursesRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));
        if (!course.getTeacher().getUserAccountId().equals(teacher.getUserAccountId())) {
            throw new UnauthorizedException("You don't own this course");
        }
        course.setCourseName(request.getCourseName());
        course.setPhoto(request.getPhoto());
        course.setCourseAmount(request.getCourseAmount());
        course.setPdfBook(request.getPdfBook());
        course.setDescription(request.getDescription());
        course.setFree(request.isFree());
        return mapToResponse(coursesRepository.save(course));
    }

    public void deleteCourse(Long courseId, UserAccount teacher) {
        Courses course = coursesRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));
        if (!course.getTeacher().getUserAccountId().equals(teacher.getUserAccountId())) {
            throw new UnauthorizedException("You don't own this course");
        }
        coursesRepository.delete(course);
    }

    public CourseResponse mapToResponse(Courses course) {
        long lessonCount = lessonsRepository.countByCourse(course);
        long quizCount = quizRepository.countByCourse(course);
        long enrollmentCount = coursesRepository.getEnrollmentCount(course);
        return CourseResponse.builder()
                .coursesId(course.getCoursesId())
                .courseName(course.getCourseName())
                .photo(course.getPhoto())
                .courseAmount(course.getCourseAmount())
                .pdfBook(course.getPdfBook())
                .description(course.getDescription())
                .isFree(course.isFree())
                .teacherId(course.getTeacher().getUserAccountId())
                .teacherName(course.getTeacher().getFullName())
                .lessonCount(lessonCount)
                .quizCount(quizCount)
                .enrollmentCount(enrollmentCount)
                .createdAt(course.getCreatedAt())
                .build();
    }
}
