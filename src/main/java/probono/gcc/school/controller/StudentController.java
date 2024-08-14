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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import probono.gcc.school.exception.CustomException;
import probono.gcc.school.model.dto.users.StudentCreateRequestDTO;
import probono.gcc.school.model.dto.users.StudentResponseDTO;
import probono.gcc.school.model.dto.users.StudentUpdateRequestDTO;
import probono.gcc.school.model.dto.users.TeacherRequestDTO;
import probono.gcc.school.model.dto.users.TeacherResponseDTO;
import probono.gcc.school.model.entity.Users;
import probono.gcc.school.service.ImageService;
import probono.gcc.school.service.StudentService;

@Slf4j
@RestController
@AllArgsConstructor
public class StudentController {

  private final StudentService studentService;
  private ModelMapper modelMapper;
  private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

  //student 생성
  @PostMapping("/students")
  public ResponseEntity<StudentResponseDTO> createStudent(
      @RequestBody @Valid StudentCreateRequestDTO requestDto) {
    StudentResponseDTO student = studentService.createStudent(requestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(student);
  }

  // 모든 Students 조회
  @GetMapping("/students")
  public ResponseEntity<List<StudentResponseDTO>> getAllStudents() {
    try {
      List<StudentResponseDTO> students = studentService.findAllStudents();

      return ResponseEntity.ok(students);
    } catch (Exception ex) {
      logger.error("Unexpected error occurred while fetching students: {}", ex.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // 특정 Student 조회 (loginId로 조회)
  @GetMapping("/students/{loginId}")
  public ResponseEntity<StudentResponseDTO> getOneStudent(@PathVariable String loginId) {

    StudentResponseDTO student = studentService.findOneStudent(loginId);
    return ResponseEntity.ok(student);

  }

  @PutMapping("/students/{loginId}")
  public ResponseEntity<?> updateStudent(
      @PathVariable String loginId, @RequestBody @Valid StudentUpdateRequestDTO requestDto) {
    try {

      String updatedTeacherId = studentService.updateStudent(loginId, requestDto);
      Users updatedTeacher = studentService.findById(updatedTeacherId);

//      logger.info("updatedTeacher.getCreatedAt() : {}",updatedTeacher.getCreatedAt());
//      logger.info("updatedTeacher.getUpdatedAt() : {}",updatedTeacher.getUpdatedAt());

      TeacherResponseDTO responseDto = modelMapper.map(updatedTeacher, TeacherResponseDTO.class);

      return ResponseEntity.ok(responseDto);
    } catch (CustomException ex) {
      logger.error("Error occurred during student update: {}", ex.getMessage());
      return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    } catch (Exception ex) {
      logger.error("Unexpected error occurred during student update: {}", ex.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
  }

  // Delete a student
  @DeleteMapping("/students/{loginId}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Student deleted", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "404", description = "Student not found", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<?> deleteStudent(@PathVariable String loginId) {
    try {

      // Perform delete operation using service
      studentService.deleteStudent(loginId);
      Users deletedStudent = studentService.findById(loginId);

      // Teacher 엔티티를 DTO로 변환

      StudentResponseDTO responseDTO = modelMapper.map(deletedStudent, StudentResponseDTO.class);
      // Return success response
      return ResponseEntity.ok(responseDTO);
    } catch (CustomException ex) {
      // Handle specific exception and return appropriate response
      logger.error("Error occurred during student deletion: {}", ex.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception ex) {
      // Handle any other unforeseen exceptions
      logger.error("Unexpected error occurred during student deletion: {}", ex.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  // Check if loginId is already taken
  @GetMapping("/students/checkLoginId/{loginId}")
  public ResponseEntity<?> checkLoginId(@PathVariable String loginId) {
    boolean exists = studentService.isLoginIdExists(loginId);
    if (exists) {
      // 로그인 ID가 이미 존재하는 경우
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Login ID already exists.");
    } else {
      // 로그인 ID가 존재하지 않는 경우
      return ResponseEntity.ok("Login ID is available.");
    }
  }


}
