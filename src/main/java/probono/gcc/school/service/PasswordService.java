package probono.gcc.school.service;

import static probono.gcc.school.model.enums.Status.ACTIVE;

import jakarta.validation.Valid;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import probono.gcc.school.exception.CustomException;
import probono.gcc.school.model.dto.PasswordRequestDTO;
import probono.gcc.school.model.entity.Users;
import probono.gcc.school.model.enums.Role;
import probono.gcc.school.repository.UserRepository;

@Service
@AllArgsConstructor
public class PasswordService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private static final Logger logger = LoggerFactory.getLogger(PasswordService.class);
  public void changePassword(String username, @Valid PasswordRequestDTO requestDto, Role role) {
    //Role은 요청 보낸 객체

    Users user = userRepository.findByUsernameAndStatus(username, ACTIVE).orElseThrow(
        () -> new NoSuchElementException("User not found with ID: " + username));

    if(role==Role.ROLE_TEACHER && user.getRole()!=Role.ROLE_STUDENT){
      //Teacher가 바꿔주는데 Student 계정을 바꾸는 것이 아니라면 예외처리
      throw new IllegalArgumentException("Teacher는 Student의 password만 변경해줄 수 있습니다.");

    }

    logger.info("current password : {}",user.getPassword());
    if (bCryptPasswordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
      user.setPassword(bCryptPasswordEncoder.encode(requestDto.getNewPassword()));
    } else {
      throw new IllegalArgumentException("current password is wrong");
    }

    userRepository.save(user);
    logger.info("new password : {}",user.getPassword());

  }


}
