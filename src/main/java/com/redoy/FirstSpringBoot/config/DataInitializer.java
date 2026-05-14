package com.redoy.FirstSpringBoot.config;

import com.redoy.FirstSpringBoot.entity.Courses;
import com.redoy.FirstSpringBoot.entity.UserAccount;
import com.redoy.FirstSpringBoot.repository.CoursesRepository;
import com.redoy.FirstSpringBoot.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserAccountRepository userAccountRepository;
    private final CoursesRepository coursesRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String PASS = "Password123!";

    @Override
    public void run(String... args) {
        // ── ADMINS ──────────────────────────────────────────
        createUser("Admin User",    "admin@test.com",    PASS, "01700000001", UserAccount.Role.ADMIN);
        createUser("Super Admin",   "admin2@test.com",   PASS, "01700000002", UserAccount.Role.ADMIN);

        // ── TEACHERS ────────────────────────────────────────
        UserAccount t1 = createUser("Rahim Uddin",    "teacher@test.com",  PASS, "01711111111", UserAccount.Role.TEACHER);
        UserAccount t2 = createUser("Karim Hossain",  "teacher2@test.com", PASS, "01711111112", UserAccount.Role.TEACHER);
        UserAccount t3 = createUser("Nadia Islam",    "teacher3@test.com", PASS, "01711111113", UserAccount.Role.TEACHER);

        // ── STUDENTS ────────────────────────────────────────
        createUser("Arif Ahmed",    "student@test.com",  PASS, "01822222221", UserAccount.Role.STUDENT);
        createUser("Sadia Khanam",  "student2@test.com", PASS, "01822222222", UserAccount.Role.STUDENT);
        createUser("Rakib Hasan",   "student3@test.com", PASS, "01822222223", UserAccount.Role.STUDENT);
        createUser("Mim Akter",     "student4@test.com", PASS, "01822222224", UserAccount.Role.STUDENT);

        // ── DEMO COURSES (only if none exist) ───────────────
        if (t1 != null && coursesRepository.count() == 0) {
            seedCourse("Java Spring Boot Full Course",
                "Complete Spring Boot 4.x course covering REST APIs, Spring Security & JPA.",
                new BigDecimal("999"), false, t1);
            seedCourse("Python for Beginners",
                "Learn Python from absolute zero — variables, loops, functions, and more.",
                new BigDecimal("499"), false, t2);
            seedCourse("Web Design Fundamentals",
                "Master HTML5, CSS3 and responsive design with real-world projects.",
                BigDecimal.ZERO, true, t3);
            seedCourse("English Spoken Course",
                "Improve your spoken English with structured lessons and practice exercises.",
                new BigDecimal("799"), false, t2);
            seedCourse("Database Design with MySQL",
                "Deep dive into relational databases, normalization, and advanced SQL queries.",
                new BigDecimal("699"), false, t1);
            seedCourse("React JS Masterclass",
                "Build modern web applications with React, hooks, and component architecture.",
                BigDecimal.ZERO, true, t3);
            log.info("✅ {} demo courses seeded.", 6);
        }

        log.info("✅ All demo accounts seeded successfully.");
    }

    /** Creates user if not present, or resets password if already exists. Returns the user. */
    private UserAccount createUser(String name, String email, String password, String phone, UserAccount.Role role) {
        if (userAccountRepository.existsByEmail(email)) {
            // Always reset password and role so demo accounts are always correct
            UserAccount existing = userAccountRepository.findByEmail(email).orElseThrow();
            existing.setPasswordHash(passwordEncoder.encode(password));
            existing.setRole(role);
            existing.setStatus(true);
            return userAccountRepository.save(existing);
        }
        UserAccount user = UserAccount.builder()
                .fullName(name)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .phone(phone)
                .role(role)
                .status(true)
                .build();
        UserAccount saved = userAccountRepository.save(user);
        log.info("Created [{}] → {} ({})", role, email, name);
        return saved;
    }

    private void seedCourse(String name, String description, BigDecimal price, boolean free, UserAccount teacher) {
        Courses course = Courses.builder()
                .courseName(name)
                .description(description)
                .courseAmount(price)
                .isFree(free)
                .teacher(teacher)
                .build();
        coursesRepository.save(course);
    }
}
