package probono.gcc.school.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import probono.gcc.school.exception.CustomException;
import probono.gcc.school.mapper.StudentMapper;
import probono.gcc.school.model.dto.users.StudentCreateRequestDTO;
import probono.gcc.school.model.dto.users.StudentResponseDTO;
import probono.gcc.school.model.dto.users.StudentUpdateRequestDTO;
import probono.gcc.school.model.dto.users.TeacherRequestDTO;
import probono.gcc.school.model.dto.users.TeacherResponseDTO;
import probono.gcc.school.model.dto.users.UserResponse;
import probono.gcc.school.model.entity.Users;
import probono.gcc.school.model.enums.Grades;
import probono.gcc.school.service.ImageService;
import probono.gcc.school.service.StudentService;

@Slf4j
@RestController
@AllArgsConstructor
public class StudentController {

  @Lazy
  private final StudentService studentService;
  private ModelMapper modelMapper;

  private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

  //student 생성
  @PostMapping("/students/join")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
  public ResponseEntity<StudentResponseDTO> createStudent(
      @RequestBody @Valid StudentCreateRequestDTO requestDto) {
    StudentResponseDTO student = studentService.createStudent(requestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(student);
  }

  // 모든 Students 조회
  @GetMapping("/students")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
  public ResponseEntity<Page<UserResponse>> getAllStudents(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {
    Page<UserResponse> students = studentService.findAllStudents(page, size);
    return ResponseEntity.ok(students);
  }

  // 특정 Student 조회 (username로 조회)
  @GetMapping("/students/{username}")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN','STUDENT')")
  public ResponseEntity<StudentResponseDTO> getOneStudent(@PathVariable String username) {

    StudentResponseDTO student = studentService.findOneStudent(username);
    return ResponseEntity.ok(student);

  }

  @PutMapping("/students/{username}")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
  public ResponseEntity<?> updateStudent(
      @PathVariable String username, @RequestBody @Valid StudentUpdateRequestDTO requestDto) {

    StudentResponseDTO studentResponseDTO = studentService.updateStudent(username, requestDto);
    return ResponseEntity.ok(studentResponseDTO);

  }

  // Delete a student
  @DeleteMapping("/students/{username}")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Student deleted", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "404", description = "Student not found", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<?> deleteStudent(@PathVariable String username) {

    // Perform delete operation using service
    studentService.deleteStudent(username);
    Users deletedStudent = studentService.findById(username);

    // Teacher 엔티티를 DTO로 변환

    StudentResponseDTO responseDTO = modelMapper.map(deletedStudent, StudentResponseDTO.class);
    // Return success response
    return ResponseEntity.ok(responseDTO);

  }

  // Check if username is already taken
  @GetMapping("/students/checkusername/{username}")
  public ResponseEntity<?> checkusername(@PathVariable String username) {
    boolean exists = studentService.isusernameExists(username);
    if (exists) {
      // 로그인 ID가 이미 존재하는 경우
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Login ID already exists.");
    } else {
      // 로그인 ID가 존재하지 않는 경우
      return ResponseEntity.ok("Login ID is available.");
    }
  }

  @GetMapping("/students/checkSerialNumber/{serialNumber}")
  public ResponseEntity<?> checkusername(@PathVariable Integer serialNumber) {
    boolean exists = studentService.isSerialNumberExists(serialNumber);
    if (exists) {
      // serial number가 이미 존재하는 경우
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Serial Number already exists.");
    } else {
      // serial number가 존재하지 않는 경우
      return ResponseEntity.ok("Serial Number is available.");
    }
  }

  // 특정 학년의 모든 Students 조회
  @GetMapping("/students/grade")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
  public ResponseEntity<Page<UserResponse>> getAllStudents(
      @RequestParam(value = "grade") Grades grade,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {
    Page<UserResponse> students = studentService.findGradeStudents(grade, page, size);
    return ResponseEntity.ok(students);
  }

  //특정 학년의 할당되지 않은 Students 조회
//  @GetMapping("/students/notAssigned/grade")
//  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
//  public ResponseEntity<Page<UserResponse>> getNotAssignedStudents(
//      @RequestParam(value = "grade") Grades grade,
//      @RequestParam(value = "page", defaultValue = "0") int page,
//      @RequestParam(value = "size", defaultValue = "10") int size) {
//    Page<UserResponse> students = studentService.findNotAssignedGradeStudents(grade, page, size);
//    return ResponseEntity.ok(students);
//  }



}
