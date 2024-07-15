package probono.gcc.school.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import probono.gcc.school.model.dto.TeacherListResponseDto;
import probono.gcc.school.model.dto.TeacherCreateRequestDto;
import probono.gcc.school.model.dto.TeacherCreateResponseDto;
import probono.gcc.school.model.dto.TeacherUpdateRequestDto;
import probono.gcc.school.model.entity.Teacher;
import probono.gcc.school.service.TeacherService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TeacherController {
    private final TeacherService teacherService;
    private ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(TeacherController.class);
    //선생님 계정 생성
    @PostMapping("/teachers")
    public ResponseEntity<TeacherCreateResponseDto>  createTeacher(@RequestBody TeacherCreateRequestDto requestDto){
        TeacherCreateResponseDto teacher = teacherService.createTeacher(requestDto);

        // 필드 값 로그로 출력
        logger.info("TeacherResponseDto Details:");
        logger.info("Login ID: {}", teacher.getLogin_id());
        logger.info("Name: {}", teacher.getName());
        logger.info("Login PW: {}", teacher.getLogin_pw());

        return ResponseEntity.status(HttpStatus.CREATED).body(teacher);

    }


    //선생님 목록 조회
    @GetMapping("/teachers")
    public ResponseEntity<List<TeacherListResponseDto>>  getAllTeachers() {
        List<TeacherListResponseDto> teachers = teacherService.findAllTeacher();
        return ResponseEntity.ok(teachers);
    }

    // 선생님 한 명 조회
    @GetMapping("/teachers/{id}")
    public TeacherCreateResponseDto getOneTeacher(@PathVariable Long id) {
        return teacherService.findOneTeacher(id);
    }

    // 선생님 수정
    @PutMapping("/teachers/{id}")
    public ResponseEntity<Teacher> updateTeacher(@PathVariable Long id, @RequestBody TeacherUpdateRequestDto requestDto) {
        // 서비스에서 업데이트 수행
        Long updatedTeacherId = teacherService.update(id, requestDto);
        // 업데이트된 Teacher 객체를 조회
        Teacher updatedTeacher = teacherService.findById(updatedTeacherId);
        // 업데이트된 Teacher 객체를 응답 본문으로 반환
        return ResponseEntity.ok(updatedTeacher);
    }

//    private Teacher convertToEntity(TeacherUpdateRequestDto requestDto) {
//        Teacher teacher = modelMapper.map(requestDto, Teacher.class);
//        return teacher;
//    }

    // 선생님 삭제
    @DeleteMapping("/teachers/{id}")
    public Long deleteTeacher(@PathVariable Long id) {
        return  teacherService.deleteTeacher(id);
    }





}
