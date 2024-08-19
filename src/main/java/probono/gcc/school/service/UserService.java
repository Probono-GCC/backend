package probono.gcc.school.service;

import static probono.gcc.school.model.enums.Status.ACTIVE;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import probono.gcc.school.model.dto.classes.ClassResponse;
import probono.gcc.school.model.dto.image.CreateImageResponseDTO;
import probono.gcc.school.model.dto.users.TeacherResponseDTO;
import probono.gcc.school.model.dto.users.UserResponse;
import probono.gcc.school.model.entity.Users;
import probono.gcc.school.repository.UserRepository;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class UserService {

  private final UserRepository userRepository;
  private ModelMapper modelMapper;
  public UserResponse findOneUser(String username) {
    Users teacher = userRepository.findByUsernameAndStatus(username, ACTIVE).orElseThrow(
        () -> new IllegalArgumentException("User not found with ID: " + username)
    );
    return mapToResponseDTO(teacher);
  }
  public UserResponse mapToResponseDTO(Users user) {
    // Create a new TeacherResponseDTO instance
    UserResponse responseDto = new UserResponse();

    // Set fields directly from the savedTeacher entity
    responseDto.setUsername(user.getUsername());
    responseDto.setRole(user.getRole());
    responseDto.setName(user.getName());
    responseDto.setBirth(user.getBirth());
    responseDto.setSex(user.getSex());
    responseDto.setPhoneNum(user.getPhoneNum());
//    responseDto.setPwAnswer(user.getPwAnswer());

    return responseDto;
  }

}
