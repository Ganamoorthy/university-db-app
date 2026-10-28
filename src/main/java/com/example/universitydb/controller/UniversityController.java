package com.example.universitydb.controller;

import com.example.universitydb.dto.*;
import com.example.universitydb.model.Course;
import com.example.universitydb.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UniversityController {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final EnrollmentRepository enrollmentRepository;

    @GetMapping("/students")
    public List<StudentDto> students(@RequestParam(required = false) String name) {
        var list = (name == null || name.isEmpty()) ? studentRepository.findAllByOrderByFullNameAsc()
                : studentRepository.findByFullNameContainingIgnoreCase(name.trim());
        return list.stream().map(StudentDto::from).toList();
    }

    @GetMapping("/students/{code}")
    public ResponseEntity<StudentDto> student(@PathVariable String code) {
        return studentRepository.findByStudentCode(code)
                .map(s -> ResponseEntity.ok(StudentDto.from(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/students/{code}/enrollments")
    public List<EnrollmentDto> studentEnrollments(@PathVariable String code) {
        return enrollmentRepository.findByStudent_StudentCode(code)
                .stream().map(EnrollmentDto::from).toList();
    }

    @GetMapping("/courses")
    public List<CourseDto> courses(@RequestParam(required = false) Integer minCredits,
            @RequestParam(required = false) String department) {
        List<Course> list;
        if (minCredits != null) {
            list = courseRepository.findByCreditsGreaterThanEqual(minCredits);
        } else if (department != null && !department.isBlank()) {
            list = courseRepository.findByLecturer_Department_Name(department.trim());
        } else {
            list = courseRepository.findAll();
        }
        return list.stream().map(CourseDto::from).toList();
    }

    @GetMapping("/courses/{code}/students")
    public List<StudentDto> courseStudents(@PathVariable String code) {
        return enrollmentRepository.findByCourse_Code(code)
                .stream().map(e -> StudentDto.from(e.getStudent())).toList();
    }

    @GetMapping("/departments/{code}/lecturers")
    public ResponseEntity<List<LecturerDto>> departmentLecturers(@PathVariable String code) {
        return departmentRepository.findWithLecturers(code)
                .map(d -> ResponseEntity.ok(d.getLecturers().stream().map(LecturerDto::from).toList()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/enrollments")
    public List<EnrollmentDto> enrollments(@RequestParam String grade) {
        return enrollmentRepository.findByGrade(grade)
                .stream().map(EnrollmentDto::from).toList();
    }

    @GetMapping("/reports/course-counts")
    public List<CourseCountDto> courseCounts() {
        return enrollmentRepository.countStudentsPerCourse()
                .stream().map(CourseCountDto::from).toList();
    }
}