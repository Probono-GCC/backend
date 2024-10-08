package probono.gcc.school.controller;

import static probono.gcc.school.model.enums.Role.ROLE_ADMIN;
import static probono.gcc.school.model.enums.Role.ROLE_TEACHER;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import probono.gcc.school.model.dto.NewPasswordDTO;
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


  //Admin이 학생계정 , 선생님계정 비밀번호 바꾸기
  @Operation(summary = "Admin이 학생계정 , 선생님계정 비밀번호 바꾸기")
  @PutMapping("/admin/changePassword/{username}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public ResponseEntity<?> updateUserPasswordByAdmin(
      @PathVariable String username, @RequestBody @Valid NewPasswordDTO requestDto) {
    passwordService.changePassword(username, requestDto, ROLE_ADMIN);
    return ResponseEntity.ok("changed password successfully");
  }

  //Teacher가 학생계정 비밀번호 바꾸기
  @Operation(summary = "Teacher가 학생계정 비밀번호 바꾸기")
  @PutMapping("/teacher/changePassword/{username}")
  @PreAuthorize("hasAnyRole('Teacher')")
  public ResponseEntity<?> updateStudentPasswordByTeacher(

      @PathVariable String username, @RequestBody @Valid NewPasswordDTO requestDto
  ) {

    passwordService.changePassword(username, requestDto, ROLE_TEACHER);
    return ResponseEntity.ok("changed password successfully");

  }

  //비밀번호 잃어버린 경우 바꾸기 (본인이 비밀번호 바꾸기)
  @Operation(summary = "Check password Answer", description = "Check password Answer to reset password")
  @GetMapping("/checkPwAnswer/{username}/{pwAnswer}")
  @PreAuthorize("hasAnyRole('Teacher','Student')")
  public ResponseEntity<?> checkPwAnswerIfForgotPassword(
      @PathVariable String username, @PathVariable String pwAnswer
  ) {
    passwordService.checkPwAnswerIfForgotPassword(username, pwAnswer);
    return ResponseEntity.ok("pwAnswer is right");
  }

  //forget pw - reset password
  @Operation(summary = "Reset user password", description = "Allows User to reset password by verifying the password answer.")
  @PutMapping("/resetPassword/{username}")
  @PreAuthorize("hasAnyRole('Teacher','Student')")
  public ResponseEntity<?> updatePasswordByPwAnswer(
      @PathVariable String username, @RequestBody NewPasswordDTO requestDto) {

    passwordService.changePasswordByPwAnswer(username, requestDto);
    return ResponseEntity.ok("changed password successfully");
  }
}
