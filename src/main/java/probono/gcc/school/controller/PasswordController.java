package probono.gcc.school.controller;

import static probono.gcc.school.model.enums.Role.ROLE_ADMIN;
import static probono.gcc.school.model.enums.Role.ROLE_TEACHER;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import probono.gcc.school.model.dto.PasswordRequestDTO;
import probono.gcc.school.model.dto.users.StudentResponseDTO;
import probono.gcc.school.model.dto.users.StudentUpdateRequestDTO;
import probono.gcc.school.service.PasswordService;
import probono.gcc.school.service.StudentService;
import probono.gcc.school.service.TeacherService;

@RestController
@RequiredArgsConstructor
public class PasswordController {

  private final PasswordService passwordService;
  private final StudentService studentService;
  private final TeacherService teacherService;


  //Admin이 학생계정 , 선생님계정 비밀전호 바꾸기
  @PutMapping("/admin/changePassword/{username}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public ResponseEntity<?> updateUserPasswordByAdmin(
      @PathVariable String username, @RequestBody @Valid PasswordRequestDTO requestDto) {

    passwordService.changePassword(username,requestDto,ROLE_ADMIN);
    return ResponseEntity.ok("changed password successfully");
  }

  //Teacher가 학생계정 비밀전호 바꾸기
  @PutMapping("/teacher/changePassword/{username}")
  @PreAuthorize("hasAnyRole('Teacher')")
  public ResponseEntity<?> updateStudentPasswordByTeacher(
      @PathVariable String username, @RequestBody @Valid PasswordRequestDTO requestDto
     ) {

    passwordService.changePassword(username,requestDto,ROLE_TEACHER);
    return ResponseEntity.ok("changed password successfully");

  }


}
