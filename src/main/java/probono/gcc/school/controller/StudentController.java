package probono.gcc.school.controller;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import probono.gcc.school.model.dto.StudentCreateRequestDto;
import probono.gcc.school.model.dto.StudentResponseDto;
import probono.gcc.school.model.dto.StudentUpdateRequestDto;
import probono.gcc.school.model.entity.Logs;
import probono.gcc.school.model.entity.Student;
import probono.gcc.school.model.enums.Status;
import probono.gcc.school.repository.StudentRepository;
import probono.gcc.school.service.StudentService;

import java.util.Optional;

@RestController
@RequestMapping("/students")
public class StudentController {

  @Autowired
  private StudentService studentService;

  @Autowired
  private StudentRepository studentRepository;

  @PostMapping("")
  public ResponseEntity<?> createStudent(
      @Valid @RequestBody StudentCreateRequestDto studentCreateRequestDto, BindingResult result) {

    if (result.hasErrors()) {
      String errorMessages = result.getAllErrors()
          .stream()
          .map(error -> error.getDefaultMessage())
          .collect(Collectors.joining(", "));
      return ResponseEntity.badRequest().body(errorMessages);
    }

    Logs logs = new Logs(LocalDateTime.now(), -1L);

    Student student = new Student();
    student.setLoginId(studentCreateRequestDto.getLoginId());
    student.setLoginPw(studentCreateRequestDto.getLoginPw());
    student.setName(studentCreateRequestDto.getName());
    student.setSerialNumber(studentCreateRequestDto.getSerialNumber());
    student.setGrade(studentCreateRequestDto.getGrade());

    student.setLogs(logs);

    StudentResponseDto createdStudent = studentService.createStudent(student);
    return ResponseEntity.ok(createdStudent);
  }

  @GetMapping("/{id}")
  public ResponseEntity<StudentResponseDto> getStudentById(@PathVariable Long id) {
    Optional<StudentResponseDto> student = studentService.getStudentById(id);
    return student.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<StudentResponseDto> updateStudent(@PathVariable Long id,
      @RequestBody StudentUpdateRequestDto studentUpdateRequestDto) {
    Optional<Student> optionalStudent = studentRepository.findById(id);
    if (optionalStudent.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    Student existingStudent = optionalStudent.get();
    StudentResponseDto updatedStudent = studentService.updateStudent(studentUpdateRequestDto,
        existingStudent);
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
