package probono.gcc.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import probono.gcc.school.model.dto.StudentCreateRequestDto;
import probono.gcc.school.model.dto.StudentResponseDto;
import probono.gcc.school.model.dto.StudentUpdateRequestDto;
import probono.gcc.school.model.entity.Student;
import probono.gcc.school.repository.StudentRepository;
import probono.gcc.school.service.StudentService;

import java.util.Optional;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping("/new-student")
    public ResponseEntity<StudentResponseDto> createStudent(@RequestBody StudentCreateRequestDto studentCreateRequestDto) {
        StudentResponseDto createdStudent = studentService.createStudent(studentCreateRequestDto);
        return ResponseEntity.ok(createdStudent);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDto> getStudentById(@PathVariable Long id) {
        Optional<StudentResponseDto> student = studentService.getStudentById(id);
        return student.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDto> updateStudent(@PathVariable Long id, @RequestBody StudentUpdateRequestDto studentUpdateRequestDto) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Student existingStudent = optionalStudent.get();
        StudentResponseDto updatedStudent = studentService.updateStudent(studentUpdateRequestDto, existingStudent);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StudentResponseDto> deleteStudent(@PathVariable Long id) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Student existingStudent = optionalStudent.get();

        StudentResponseDto deletedStudent = studentService.deleteStudent(existingStudent);
        return ResponseEntity.ok(deletedStudent);
    }
}
