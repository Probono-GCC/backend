package probono.gcc.school.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import probono.gcc.school.model.dto.SubjectRequestDTO;
import probono.gcc.school.model.dto.SubjectResponseDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import probono.gcc.school.model.entity.Subject;
import probono.gcc.school.service.SubjectService;

@Slf4j
@RestController
@AllArgsConstructor
public class SubjectController {

  private final SubjectService subjectService;
  private ModelMapper modelMapper;
  private static final Logger logger = LoggerFactory.getLogger(SubjectController.class);

  //과목 생성
  @PostMapping("/subjects")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
  public ResponseEntity<SubjectResponseDTO> createSubject(
      @RequestBody SubjectRequestDTO requestDto) {

    try {
      SubjectResponseDTO subject = subjectService.createSubject(requestDto);
      return ResponseEntity.status(HttpStatus.CREATED).body(subject);
    } catch (IllegalArgumentException ex) {
      logger.error("Error creating subject: {}", ex.getMessage());
      return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // HTTP 409 Conflict
    }

  }

  //과목 목록 조회
  @GetMapping("/subjects")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
  public ResponseEntity<List<SubjectResponseDTO>> getAllSubjects() {
    List<SubjectResponseDTO> subjects = subjectService.findAllSubject();
    return ResponseEntity.ok(subjects);
  }


  // 과목 한 명 조회
  @GetMapping("/subjects/{id}")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
  public ResponseEntity<SubjectResponseDTO> getOneSubject(@PathVariable Long id) {
    SubjectResponseDTO subject = subjectService.findOneSubject(id);
    return ResponseEntity.ok(subject);
  }

  // 과목 수정
  @PutMapping("/subjects/{id}")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
  public ResponseEntity<SubjectResponseDTO> updateSubject(@PathVariable Long id,
      @RequestBody SubjectRequestDTO requestDto) {
    try {
      // 서비스에서 업데이트 수행
      Long updatedSubjectId = subjectService.update(id, requestDto);
      // 업데이트된 Teacher 객체를 조회
      Subject updatedSubject = subjectService.findById(updatedSubjectId);
      // Teacher 엔티티를 TeacherResponseDto로 변환
      SubjectResponseDTO responseDto = modelMapper.map(updatedSubject, SubjectResponseDTO.class);
      // 업데이트된 TeacherResponseDto 객체를 응답 본문으로 반환
      return ResponseEntity.ok(responseDto);
    } catch (ResponseStatusException ex) {
      // 예외 처리: ResponseStatusException이 발생할 경우, 클라이언트에게 에러 응답을 반환
      return ResponseEntity.status(ex.getStatusCode()).body(null);
    }
  }

  //과목 삭제
  @DeleteMapping("/subjects/{id}")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Teacher deleted", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubjectResponseDTO.class))),
      @ApiResponse(responseCode = "404", description = "Teacher not found", content = @Content(mediaType = "application/json")),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<SubjectResponseDTO> deleteSubject(@PathVariable Long id) {
    try {
      // 서비스에서 삭제 수행
      Long deletedSubjectId = subjectService.deleteSubject(id);
      // 삭제된 Teacher를 조회 시, 존재하지 않을 경우 예외 처리
      Subject deletedTeacher = subjectService.findById(deletedSubjectId);
      // Teacher 엔티티를 DTO로 변환
      SubjectResponseDTO responseDto = modelMapper.map(deletedTeacher, SubjectResponseDTO.class);
      // 성공적인 삭제 후 응답 반환
      return ResponseEntity.ok(responseDto);
    } catch (IllegalArgumentException ex) {
      // 존재하지 않는 ID로 인한 예외 처리
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    } catch (Exception ex) {
      // 다른 예외 처리
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }


}


