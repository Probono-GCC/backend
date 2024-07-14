package probono.gcc.school.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import probono.gcc.school.model.dto.TeacherListResponseDto;
import probono.gcc.school.model.dto.TeacherRequestDto;
import probono.gcc.school.model.dto.TeacherResponseDto;
import probono.gcc.school.service.TeacherService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TeacherController {
    private final TeacherService teacherService;

    //선생님 계정 생성
    @PostMapping("/teachers")
    public TeacherResponseDto createTeacher(@RequestBody TeacherRequestDto requestDto){
        TeacherResponseDto teacher = teacherService.createTeacher(requestDto);
        return teacher;
    }


    //선생님 목록 조회
    @GetMapping("/teachers")
    public List<TeacherListResponseDto> getAllTeachers() {
        return teacherService.findAllTeacher();
    }


    //선생님 한 명 조회

    //선생님 수정

    //선생님 삭제



}
