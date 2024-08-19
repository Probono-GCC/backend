package probono.gcc.school.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import probono.gcc.school.exception.CustomException;
import probono.gcc.school.model.dto.users.TeacherResponseDTO;
import probono.gcc.school.model.dto.users.UserResponse;
import probono.gcc.school.service.TeacherService;
import probono.gcc.school.service.UserService;

@Slf4j
@RestController
@AllArgsConstructor
public class UserController {
  private final UserService userService;

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @GetMapping("/users/{username}")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
  public ResponseEntity<UserResponse> getOneUser(@PathVariable String username) {
    try {
      UserResponse userResponse = userService.findOneUser(username);
      return ResponseEntity.ok(userResponse);
    } catch (CustomException ex) {
      logger.error("User not found: {}", ex.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    } catch (Exception ex) {
      logger.error("Unexpected error occurred while fetching the User: {}", ex.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }




}
