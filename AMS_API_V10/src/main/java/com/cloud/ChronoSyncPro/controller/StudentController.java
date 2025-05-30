package com.cloud.ChronoSyncPro.controller;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloud.ChronoSyncPro.dtos.StudentRegisterRequest;
import com.cloud.ChronoSyncPro.dtos.UpdateStudent;
import com.cloud.ChronoSyncPro.entity.Student;
import com.cloud.ChronoSyncPro.service.StudentService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", allowCredentials = "true")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/admin/saveStudent")
    public ResponseEntity<?> saveStudent(@RequestBody StudentRegisterRequest studentRegisterRequest) {
        try {
            Student savedStudent = studentService.saveStudent(studentRegisterRequest);
            studentService.assignBatchToStudent(savedStudent, studentRegisterRequest.getBatches());
            studentService.saveStudent(studentRegisterRequest);
        } catch (Exception e) {
            if (e instanceof DataIntegrityViolationException) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/admin/updateStudent")
    public ResponseEntity<?> updateStudent(@RequestBody UpdateStudent updateStudent) {
        try {
            studentService.updatestudent(updateStudent);
        } catch (Exception e) {
            if (e instanceof DataIntegrityViolationException) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/admin/get-student-list")
    public ResponseEntity<List<Student>> getStudentList() {
        return new ResponseEntity<List<Student>>(studentService.getAllStudent(), HttpStatus.OK);
    }

    @GetMapping("/admin/getStudent/{id}")
    public ResponseEntity<?> getStudent(@PathVariable("id") Integer id) {
        UpdateStudent updateStudent = null;
        try {
            updateStudent = studentService.getStudentById(id);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<UpdateStudent>(updateStudent, HttpStatus.OK);
    }

    @GetMapping("/admin/getStudentsOfDepartmentOfSemester")
    public ResponseEntity<?> getStudentsUsingDepartmentAndSemester(@RequestParam Integer dept_id, @RequestParam String semester) {
        return new ResponseEntity<List<Student>>(studentService.getStudentsUsingDepartmentAndSemester(dept_id, semester), HttpStatus.OK);
    }

    @DeleteMapping("/admin/deleteStudent/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable("id") Integer id) {
        try {
            studentService.deleteStudent(id);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/admin/studentCount")
    public ResponseEntity<Long> studentCount() {
        return new ResponseEntity<Long>(studentService.studentCount(), HttpStatus.OK);
    }
}
