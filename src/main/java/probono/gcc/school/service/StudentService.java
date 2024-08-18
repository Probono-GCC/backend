package probono.gcc.school.service;

import static probono.gcc.school.model.enums.Status.ACTIVE;

import java.util.List;

import java.util.NoSuchElementException;
import java.util.Optional;

import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import probono.gcc.school.exception.CustomException;
import probono.gcc.school.exception.DuplicateEntityException;
import probono.gcc.school.model.dto.classes.ClassResponse;
import probono.gcc.school.model.dto.image.CreateImageResponseDTO;
import probono.gcc.school.model.dto.image.ImageResponseDTO;
import probono.gcc.school.model.dto.users.StudentCreateRequestDTO;
import probono.gcc.school.model.dto.users.StudentResponseDTO;
import probono.gcc.school.model.dto.users.StudentUpdateRequestDTO;

import probono.gcc.school.model.dto.users.TeacherResponseDTO;
import probono.gcc.school.model.dto.users.UserResponse;
import probono.gcc.school.model.entity.Classes;

import probono.gcc.school.model.entity.Image;
import probono.gcc.school.model.entity.Users;
import probono.gcc.school.model.enums.Role;
import probono.gcc.school.model.enums.Status;
import probono.gcc.school.repository.ClassRepository;
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
  private BCryptPasswordEncoder passwordEncoder;

  @Lazy
  private ClassService classService;
  private ClassRepository classRepository;

  private static final Logger logger = LoggerFactory.getLogger(TeacherService.class);

  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  public StudentResponseDTO createStudent(StudentCreateRequestDTO requestDto) {


    try {
      //username 중복 확인 체크
      if (studentRepository.existsByUsername(requestDto.getUsername())) {
        throw new CustomException("Login ID already exists.", HttpStatus.CONFLICT);
      }

      // serialNumber 중복 확인
      if (studentRepository.existsBySerialNumber(requestDto.getSerialNumber())) {
        throw new DuplicateEntityException("Serial number already exists.");
      }

      // Create a new Users entity for the student
      Users student = new Users();
      student.setUsername(requestDto.getUsername());
      student.setSerialNumber(requestDto.getSerialNumber());
      logger.info("requestDto.getSerialNumber() : {}", requestDto.getSerialNumber());
      logger.info("student.getSerialNumber() : {}", student.getSerialNumber());
      student.setName(requestDto.getName());
      student.setPassword(bCryptPasswordEncoder.encode(requestDto.getPassword()));
      student.setGrade(requestDto.getGrade());
      student.setStatus(ACTIVE);
      student.setCreatedChargeId(1L); // Set the createdChargeId
      student.setRole(Role.ROLE_STUDENT);

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
  public Page<UserResponse> findAllStudents(int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("name")));

    Page<Users> studentList = studentRepository.findByStatusAndRole(Status.ACTIVE,
        Role.ROLE_STUDENT, pageRequest);
    // Use stream and ModelMapper to convert entity list to DTO list

    Page<UserResponse> responses = studentList.map(
        student -> new UserResponse(student.getUsername(), student.getName(),
            student.getSerialNumber(), student.getGrade(),
            student.getBirth(), student.getSex(), student.getPhoneNum(),
            student.getFatherPhoneNum(), student.getMotherPhoneNum(),
            student.getGuardiansPhoneNum(),
            student.getRole(), Optional.ofNullable(student.getImageId())
            .map(imageId -> modelMapper.map(imageId, ImageResponseDTO.class))
            .orElse(null)));
    return responses;
  }

  // Retrieve a single student by ID
  public StudentResponseDTO findOneStudent(String username) {
    try {
      Users student = studentRepository.findByUsernameAndStatus(username, ACTIVE).orElseThrow(
          () -> new CustomException("Student not found with ID: " + username, HttpStatus.NOT_FOUND)
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


  public StudentResponseDTO updateStudent(String username, StudentUpdateRequestDTO requestDto) {
    try {
      Users student = studentRepository.findByUsernameAndStatus(username, ACTIVE).orElseThrow(
          () -> new CustomException("Student not found with ID: " + username, HttpStatus.NOT_FOUND)
      );

      // Check if it's the first update (i.e., if birth, sex, or pwAnswer are null)
      boolean isFirstUpdate =
          student.getBirth() == null && student.getSex() == null && student.getPwAnswer() == null;

      if (isFirstUpdate) {
        // Ensure birth, sex, and pwAnswer are provided on the first update
        if (requestDto.getBirth() == null || requestDto.getSex() == null
            || requestDto.getPwAnswer() == null || requestDto.getImageId() == null) {
          logger.info("birth or sex or pwAnswer or imageId field is null");
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
        updateAlwaysChangeableField(requestDto, student);

      } else {
        if (requestDto.getPwAnswer() != null) {//최초 1회 접속이 아닌데 pwAnswer 요청 들어올 경우 예외처리
          // pwAnswer should not be provided after the first update
          throw new CustomException("Password answer can only be set during the first update.",
              HttpStatus.BAD_REQUEST);
        }


        //항상 바꿀 수 있는 field
        updateAlwaysChangeableField(requestDto, student);

        if (requestDto.getBirth()!=null){
          student.setBirth(requestDto.getBirth());
        }
        if(requestDto.getSex()!=null){
          student.setSex(requestDto.getSex());
        }
        if (requestDto.getImageId() != null) {
          Image image = imageRepository.findById(requestDto.getImageId())
              .orElseThrow(() -> new CustomException("Image not found", HttpStatus.NOT_FOUND));
          student.setImageId(image);
        }
      }

      // Save the updated student entity
      studentRepository.save(student);
      return mapToResponseDTO(student);


    } catch (CustomException e) {
      logger.error("Error updating student: {}", e.getMessage());
      throw e;
    }
  }

  private void updateAlwaysChangeableField(StudentUpdateRequestDTO requestDto, Users student) {
    changePassword(requestDto, student);
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
  }

  private void changePassword(StudentUpdateRequestDTO requestDto, Users student) {
    // Update fields that can always be updated
    if (requestDto.getNewPassword() != null) {
      if (passwordEncoder.matches(requestDto.getCurrentPassword(), student.getPassword())) {
        student.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
      } else {
        throw new IllegalArgumentException("current password is wrong");
      }
    }
  }

  public Users findById(String username) {
    try {
      return studentRepository.findByUsername(username).orElseThrow(
          () -> new CustomException("Student not found with ID: " + username, HttpStatus.NOT_FOUND)
      );
    } catch (CustomException e) {
      logger.error("Student not found: {}", e.getMessage());
      throw e;
    } catch (Exception e) {
      logger.error("Unexpected error occurred while fetching the student: {}", e.getMessage());
      throw new RuntimeException("An unexpected error occurred while fetching the student", e);
    }
  }

  public String deleteStudent(String username) {

    Users student = studentRepository.findByUsername(username).orElseThrow(
        () -> new CustomException("Student not found with ID: " + username, HttpStatus.NOT_FOUND)
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
    return student.getUsername();
  }

  public boolean isusernameExists(String username) {
    if (studentRepository.existsByUsername(username)) {
      return true;
    }
    return false;
  }


  public StudentResponseDTO mapToResponseDTO(Users student) {
    // Create a new StudentResponseDTO instance
    StudentResponseDTO responseDto = new StudentResponseDTO();

    // Set fields directly from the student entity
    responseDto.setUsername(student.getUsername());
    responseDto.setName(student.getName());
    responseDto.setSerialNumber(student.getSerialNumber());
    responseDto.setGrade(student.getGrade());
    responseDto.setBirth(student.getBirth());
    responseDto.setSex(student.getSex());
    responseDto.setPhoneNum(student.getPhoneNum());
    responseDto.setFatherPhoneNum(student.getFatherPhoneNum());
    responseDto.setMotherPhoneNum(student.getMotherPhoneNum());
    responseDto.setGuardiansPhoneNum(student.getGuardiansPhoneNum());
    responseDto.setPwAnswer(student.getPwAnswer());
    responseDto.setRole(student.getRole());
    responseDto.setStatus(student.getStatus());
    responseDto.setCreatedAt(student.getCreatedAt());
    responseDto.setUpdatedAt(student.getUpdatedAt());
    responseDto.setCreatedChargeId(student.getCreatedChargeId());
    responseDto.setUpdatedChargeId(student.getUpdatedChargeId());

    // Map the class entity (Classes) to ClassResponse if the class is assigned
    if (student.getClassId() != null) {
      ClassResponse classResponse = modelMapper.map(student.getClassId(), ClassResponse.class);
      responseDto.setClassResponse(classResponse);
    }

    // Map the image entity (Image) to ImageResponseDTO if the image is assigned
    if (student.getImageId() != null) {
      CreateImageResponseDTO imageResponse = modelMapper.map(student.getImageId(),
          CreateImageResponseDTO.class);
      responseDto.setImageResponseDTO(imageResponse);
    }

    return responseDto;
  }

}
