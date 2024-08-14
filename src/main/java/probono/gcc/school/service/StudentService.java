package probono.gcc.school.service;

import static probono.gcc.school.model.enums.Status.ACTIVE;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import probono.gcc.school.exception.CustomException;
import probono.gcc.school.model.dto.users.StudentCreateRequestDTO;
import probono.gcc.school.model.dto.users.StudentResponseDTO;
import probono.gcc.school.model.dto.users.StudentUpdateRequestDTO;
import probono.gcc.school.model.entity.Image;
import probono.gcc.school.model.entity.Users;
import probono.gcc.school.model.enums.Role;
import probono.gcc.school.model.enums.Status;
import probono.gcc.school.repository.ImageRepository;
import probono.gcc.school.repository.UserRepository;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class StudentService {

  private ModelMapper modelMapper;
  private UserRepository studentRepository;
  private ImageRepository imageRepository;
  private ImageService imageService;
  private static final Logger logger = LoggerFactory.getLogger(TeacherService.class);


  public StudentResponseDTO createStudent(StudentCreateRequestDTO requestDto) {

    try {
      //loginId 중복 확인 체크
      if (studentRepository.existsByLoginId(requestDto.getLoginId())) {
        throw new CustomException("Login ID already exists.", HttpStatus.CONFLICT);
      }

      // Create a new Users entity for the student
      Users student = new Users();
      student.setLoginId(requestDto.getLoginId());
      student.setSerialNumber(requestDto.getSerialNumber());
      logger.info("requestDto.getSerialNumber() : {}", requestDto.getSerialNumber());
      logger.info("student.getSerialNumber() : {}", student.getSerialNumber());
      student.setName(requestDto.getName());
      student.setLoginPw(requestDto.getLoginPw());
      student.setGrade(requestDto.getGrade());
      student.setStatus(ACTIVE);
      student.setCreatedChargeId(1L); // Set the createdChargeId
      student.setRole(Role.STUDENT);

      Users studentCreated = studentRepository.save(student);
      return modelMapper.map(studentCreated, StudentResponseDTO.class);
    } catch (DataIntegrityViolationException ex) {
      // Handle database-related exceptions (including SQL constraint violations)
      logger.error("Database integrity violation: {}", ex.getMessage());
      throw new CustomException("Student creation failed due to conflict with existing data.",
          HttpStatus.CONFLICT);

    }
  }


  // Retrieve all students
  public List<StudentResponseDTO> findAllStudents() {
    try {
      List<Users> studentList = studentRepository.findByStatus(ACTIVE);
      // Use stream and ModelMapper to convert entity list to DTO list
      return studentList.stream()
          .map(student -> modelMapper.map(student, StudentResponseDTO.class))
          .collect(Collectors.toList());
    } catch (Exception e) {
      // Handle any exceptions that occur during the fetching process
      logger.error("An error occurred while fetching students: {}", e.getMessage());
      throw new RuntimeException("An error occurred while fetching students", e);
    }
  }

  // Retrieve a single student by ID
  public StudentResponseDTO findOneStudent(String loginId) {
    try {
      Users student = studentRepository.findByLoginIdAndStatus(loginId, ACTIVE).orElseThrow(
          () -> new CustomException("Student not found with ID: " + loginId, HttpStatus.NOT_FOUND)
      );
      // Convert the found entity to a DTO
      return modelMapper.map(student, StudentResponseDTO.class);
    } catch (CustomException e) {
      logger.error("Student not found: {}", e.getMessage());
      throw e;
    } catch (Exception e) {
      logger.error("Unexpected error occurred while fetching the student: {}", e.getMessage());
      throw new RuntimeException("An unexpected error occurred while fetching the student", e);
    }
  }


  public String updateStudent(String loginId, StudentUpdateRequestDTO requestDto) {
    try {
      Users student = studentRepository.findByLoginIdAndStatus(loginId, ACTIVE).orElseThrow(
          () -> new CustomException("Student not found with ID: " + loginId, HttpStatus.NOT_FOUND)
      );

      // Check if it's the first update (i.e., if birth, sex, or pwAnswer are null)
      boolean isFirstUpdate =
          student.getBirth() == null && student.getSex() == null && student.getPwAnswer() == null;

      if (isFirstUpdate) {
        // Ensure birth, sex, and pwAnswer are provided on the first update
        if (requestDto.getBirth() == null || requestDto.getSex() == null
            || requestDto.getPwAnswer() == null || requestDto.getImageId() == null) {
          logger.info("birth or sex or pwAnswer field is null");
          throw new CustomException(
              "Birth, sex, Image and password answer are required for the first update.",
              HttpStatus.BAD_REQUEST);
        }
        student.setBirth(requestDto.getBirth());
        student.setSex(requestDto.getSex());
        student.setPwAnswer(requestDto.getPwAnswer());
        Image image = imageRepository.findById(requestDto.getImageId())
            .orElseThrow(
                () -> new CustomException("Image not found with id: " + requestDto.getImageId(),
                    HttpStatus.NOT_FOUND));
        student.setImageId(image);
      } else if (requestDto.getPwAnswer() != null) {//최초 1회 접속이 아닌데 pwAnswer 요청 들어올 경우 예외처리
        // pwAnswer should not be provided after the first update
        throw new CustomException("Password answer can only be set during the first update.",
            HttpStatus.BAD_REQUEST);
      }

      // Update fields that can always be updated
      if (requestDto.getLoginPw() != null) {
        student.setLoginPw(requestDto.getLoginPw());
      }
      if (requestDto.getName() != null) {
        student.setName(requestDto.getName());
      }
      if (requestDto.getSerialNumber() != 0) {
        student.setSerialNumber(requestDto.getSerialNumber());
      }
      if (requestDto.getGrade() != null) {
        student.setGrade(requestDto.getGrade());
      }
      if (requestDto.getPhoneNum() != null) {
        student.setPhoneNum(requestDto.getPhoneNum());
      }
      if (requestDto.getFatherPhoneNum() != null) {
        student.setFatherPhoneNum(requestDto.getFatherPhoneNum());
      }
      if (requestDto.getMotherPhoneNum() != null) {
        student.setMotherPhoneNum(requestDto.getMotherPhoneNum());
      }
      if (requestDto.getGuardiansPhoneNum() != null) {
        student.setGuardiansPhoneNum(requestDto.getGuardiansPhoneNum());
      }
      if (requestDto.getImageId() != null) {
        Image image = imageRepository.findById(requestDto.getImageId())
            .orElseThrow(() -> new CustomException("Image not found", HttpStatus.NOT_FOUND));
        student.setImageId(image);
      }

      // Save the updated student entity
      studentRepository.save(student);
      return student.getLoginId();

    } catch (CustomException e) {
      logger.error("Error updating student: {}", e.getMessage());
      throw e;
    }
  }

  public Users findById(String loginId) {
    try {
      return studentRepository.findByLoginId(loginId).orElseThrow(
          () -> new CustomException("Student not found with ID: " + loginId, HttpStatus.NOT_FOUND)
      );
    } catch (CustomException e) {
      logger.error("Student not found: {}", e.getMessage());
      throw e;
    } catch (Exception e) {
      logger.error("Unexpected error occurred while fetching the student: {}", e.getMessage());
      throw new RuntimeException("An unexpected error occurred while fetching the student", e);
    }
  }

  public String deleteStudent(String loginId) {

    Users student = studentRepository.findByLoginId(loginId).orElseThrow(
        () -> new CustomException("Student not found with ID: " + loginId, HttpStatus.NOT_FOUND)
    );

    // 매핑된 이미지가 있는 경우 삭제
    if (student.getImageId() != null) {
      Long imageId = student.getImageId().getImageId();
      logger.info("Deleting associated image with ID: {}", imageId);
      imageService.deleteProfileImage(imageId);
    }
    // 논리적 삭제 수행
    student.setStatus(Status.INACTIVE);
    // Dummy Data
    student.setUpdatedChargeId(2L);
    return student.getLoginId();
  }

  public boolean isLoginIdExists(String loginId) {
    if (studentRepository.existsByLoginId(loginId)) {
      return true;
    }
    return false;
  }


}
