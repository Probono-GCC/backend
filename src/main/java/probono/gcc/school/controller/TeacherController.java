package probono.gcc.school.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import probono.gcc.school.model.dto.*;
import probono.gcc.school.model.entity.Teacher;
import probono.gcc.school.service.TeacherService;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class TeacherController {
    private final TeacherService teacherService;
    private ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(TeacherController.class);
    //선생님 계정 생성
    @PostMapping("/teachers")
    public ResponseEntity<TeacherCreateResponseDto>  createTeacher(@RequestBody TeacherCreateRequestDto requestDto){
        // ID 중복 체크 로직 추가
        boolean isDuplicate = teacherService.isLoginIdDuplicate(requestDto.getLoginId());
        if (isDuplicate) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // 409 Conflict
        }

        TeacherCreateResponseDto teacher = teacherService.createTeacher(requestDto);

        // 필드 값 로그로 출력
//        logger.info("TeacherResponseDto Details:");
//        logger.info("Login ID: {}", teacher.getLogin_id());
//        logger.info("Name: {}", teacher.getName());
//        logger.info("Login PW: {}", teacher.getLogin_pw());

        return ResponseEntity.status(HttpStatus.CREATED).body(teacher);




    }


    //선생님 목록 조회
    @GetMapping("/teachers")
    public ResponseEntity<List<TeacherResponseDto>>  getAllTeachers() {
        List<TeacherResponseDto> teachers = teacherService.findAllTeacher();
        return ResponseEntity.ok(teachers);
    }

    // 선생님 한 명 조회
    @GetMapping("/teachers/{id}")
    public ResponseEntity<TeacherResponseDto> getOneTeacher(@PathVariable Long id) {
        TeacherResponseDto teacher=teacherService.findOneTeacher(id);
        return ResponseEntity.ok(teacher);
    }

    // 선생님 수정
    @PutMapping("/teachers/{id}")
    public ResponseEntity<TeacherResponseDto> updateTeacher(@PathVariable Long id, @RequestBody TeacherUpdateRequestDto requestDto) {
        try {
            // 서비스에서 업데이트 수행
            Long updatedTeacherId = teacherService.update(id, requestDto);
            // 업데이트된 Teacher 객체를 조회
            Teacher updatedTeacher = teacherService.findById(updatedTeacherId);
            // Teacher 엔티티를 TeacherResponseDto로 변환
            TeacherResponseDto responseDto = modelMapper.map(updatedTeacher, TeacherResponseDto.class);
            // 업데이트된 TeacherResponseDto 객체를 응답 본문으로 반환
            return ResponseEntity.ok(responseDto);
        } catch (ResponseStatusException ex) {
            // 예외 처리: ResponseStatusException이 발생할 경우, 클라이언트에게 에러 응답을 반환
            return ResponseEntity.status(ex.getStatusCode()).body(null);
        }
    }

    // 선생님 삭제
    @DeleteMapping("/teachers/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teacher deleted", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TeacherResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Teacher not found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<TeacherResponseDto> deleteTeacher(@PathVariable Long id) {
        try {
            // 서비스에서 삭제 수행
            Long deletedTeacherId = teacherService.deleteTeacher(id);
            // 삭제된 Teacher를 조회 시, 존재하지 않을 경우 예외 처리
            Teacher deletedTeacher = teacherService.findById(deletedTeacherId);
            // Teacher 엔티티를 DTO로 변환
            TeacherResponseDto responseDto = modelMapper.map(deletedTeacher, TeacherResponseDto.class);
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

    // ID 중복 체크 메소드
    // 중복이면 return true , 중복이 아니면 return false
    @PostMapping("teachers/check-id")
    public ResponseEntity<Boolean> checkIdDuplicate(@RequestBody TeacherCheckIdDTO teacherCheckIdDTO) {
        boolean isDuplicate = teacherService.isLoginIdDuplicate(teacherCheckIdDTO.getLoginId());
        return ResponseEntity.ok(isDuplicate);
    }


}
