package probono.gcc.school.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import probono.gcc.school.exception.CustomException;
import probono.gcc.school.model.dto.SubjectResponseDTO;
import probono.gcc.school.model.dto.users.TeacherCreateRequestDTO;
import probono.gcc.school.model.dto.users.TeacherRequestDTO;
import probono.gcc.school.model.dto.users.TeacherResponseDTO;
import probono.gcc.school.model.entity.Users;
import probono.gcc.school.service.TeacherService;

@Slf4j
@RestController
@AllArgsConstructor
public class TeacherController {

  private final TeacherService teacherService;
  private final String number = "500";
  private ModelMapper modelMapper;
  private static final Logger logger = LoggerFactory.getLogger(TeacherController.class);


  //teacher 생성
  @PostMapping("/teachers/join")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public ResponseEntity<TeacherResponseDTO> createTeacher(
       @RequestBody @Valid TeacherCreateRequestDTO requestDto) {

    TeacherResponseDTO teacher = teacherService.createTeacher(requestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(teacher);

  }

  // Retrieve all teachers
  @GetMapping("/teachers")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public ResponseEntity<List<TeacherResponseDTO>> getAllTeachers() {
    try {
      List<TeacherResponseDTO> teachers = teacherService.findAllTeachers();
      return ResponseEntity.ok(teachers);
    } catch (Exception ex) {
      logger.error("Unexpected error occurred while fetching teachers: {}", ex.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Retrieve a single teacher by ID
  @GetMapping("/teachers/{username}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public ResponseEntity<TeacherResponseDTO> getOneTeacher(@PathVariable String username) {
    try {
      TeacherResponseDTO teacher = teacherService.findOneTeacher(username);
      return ResponseEntity.ok(teacher);
    } catch (CustomException ex) {
      logger.error("Teacher not found: {}", ex.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    } catch (Exception ex) {
      logger.error("Unexpected error occurred while fetching the teacher: {}", ex.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Update a teacher
  @PutMapping("/teachers/{username}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public ResponseEntity<?> updateTeacher(
      @PathVariable String username, @RequestBody TeacherRequestDTO requestDto) {
    try {

      String updatedTeacherId = teacherService.updateTeacher(username, requestDto);
      Users updatedTeacher = teacherService.findById(updatedTeacherId);

//      logger.info("updatedTeacher.getCreatedAt() : {}",updatedTeacher.getCreatedAt());
//      logger.info("updatedTeacher.getUpdatedAt() : {}",updatedTeacher.getUpdatedAt());

      TeacherResponseDTO responseDto = modelMapper.map(updatedTeacher, TeacherResponseDTO.class);

      return ResponseEntity.ok(responseDto);
    } catch (CustomException ex) {
      logger.error("Error occurred during teacher update: {}", ex.getMessage());
      return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    } catch (Exception ex) {
      logger.error("Unexpected error occurred during teacher update: {}", ex.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
  }

  //Delete a teacher
  @DeleteMapping("/teachers/{username}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Teacher deleted", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubjectResponseDTO.class))),
      @ApiResponse(responseCode = "404", description = "Teacher not found", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<?> deleteTeacher(@PathVariable String username) {
    try {
      // Perform delete operation using service
      teacherService.deleteTeacher(username);
      Users deletedTeacher = teacherService.findById(username);
      // Teacher 엔티티를 DTO로 변환
      TeacherResponseDTO responseDto = modelMapper.map(deletedTeacher, TeacherResponseDTO.class);
      // Return success response
      return ResponseEntity.ok(responseDto);
    } catch (CustomException ex) {
      // Handle specific exception and return appropriate response
      logger.error("Error occurred during teacher deletion: {}", ex.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception ex) {
      // Handle any other unforeseen exceptions
      logger.error("Unexpected error occurred during teacher deletion: {}", ex.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  //username 중복 체크 endpoint
  @GetMapping("/teachers/checkusername/{username}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public ResponseEntity<?> checkusername(@PathVariable String username) {
    boolean exists = teacherService.isusernameExists(username);
    if (exists) {
      // 로그인 ID가 이미 존재하는 경우
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Login ID already exists.");
    } else {
      // 로그인 ID가 존재하지 않는 경우
      return ResponseEntity.ok("Login ID is available.");
    }
  }

  //teacher를 class에 할당
  //담당하는 class 할당(담임선생님)
  @PutMapping("/teachers/{loginId}/assignClass/{classId}")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
  public ResponseEntity<?> assignClassToTeacher(
      @PathVariable String username, @PathVariable Long classId) {

    TeacherResponseDTO updatedTeacher = teacherService.assignClass(username, classId);
    return ResponseEntity.ok(updatedTeacher);

  }


}
