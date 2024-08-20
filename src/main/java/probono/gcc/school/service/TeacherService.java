package probono.gcc.school.service;

import static probono.gcc.school.model.enums.Status.ACTIVE;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import probono.gcc.school.exception.CustomException;
import probono.gcc.school.exception.DuplicateEntityException;
import probono.gcc.school.model.dto.classes.ClassResponse;
import probono.gcc.school.model.dto.image.CreateImageResponseDTO;

import probono.gcc.school.model.dto.users.StudentUpdateRequestDTO;

import probono.gcc.school.model.dto.image.ImageResponseDTO;

import probono.gcc.school.model.dto.users.TeacherCreateRequestDTO;
import probono.gcc.school.model.dto.users.TeacherRequestDTO;
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
public class TeacherService {

  private ModelMapper modelMapper;
  private UserRepository teacherRepository;
  private ImageRepository imageRepository;
  private ClassRepository classRepository;
  private ImageService imageService;
  private BCryptPasswordEncoder passwordEncoder;
  private static final Logger logger = LoggerFactory.getLogger(TeacherService.class);


  private final BCryptPasswordEncoder bCryptPasswordEncoder;


  public TeacherResponseDTO createTeacher(TeacherCreateRequestDTO requestDto) {

    if (requestDto.getUsername() == null || requestDto.getUsername().trim().isEmpty()) {
      logger.info("username is required.");
      throw new CustomException("Login ID is required.", HttpStatus.BAD_REQUEST);
    }

    if (requestDto.getPassword() == null || requestDto.getPassword().trim().isEmpty()) {
      logger.info("password is required.");
      throw new CustomException("Login Password is required.", HttpStatus.BAD_REQUEST);
    }

    //username 중복 확인 체크
    if (teacherRepository.existsByUsername(requestDto.getUsername())) {
      throw new CustomException("Login ID already exists.", HttpStatus.CONFLICT);
    }

    // Create a new Users entity for the teacher
    Users teacher = new Users();
    teacher.setName(requestDto.getName());
    teacher.setUsername(requestDto.getUsername());
    teacher.setPassword(bCryptPasswordEncoder.encode(requestDto.getPassword()));
    teacher.setStatus(ACTIVE);
    teacher.setCreatedChargeId(SecurityContextHolder.getContext().getAuthentication()
        .getName()); // Set the createdChargeId
    teacher.setRole(Role.ROLE_TEACHER);
    teacher.setSerialNumber(null);

    Users teacherCreated = teacherRepository.save(teacher);

    return mapToResponseDTO(teacherCreated);

  }


  // Retrieve all teachers
  public Page<TeacherResponseDTO> findAllTeachers(int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.asc("name")));

    Page<Users> teacherList = teacherRepository.findByStatusAndRole(Status.ACTIVE,
        Role.ROLE_TEACHER, pageRequest);
    // Use stream and ModelMapper to convert entity list to DTO list

    Page<TeacherResponseDTO> responses = teacherList.map(
        teacher -> new TeacherResponseDTO(teacher.getUsername(), teacher.getRole(), teacher.getName(),
            teacher.getBirth(), teacher.getSex(), teacher.getPhoneNum(),teacher.getPwAnswer(),teacher.getStatus(),
            Optional.ofNullable(teacher.getClassId())
                .map(classId -> modelMapper.map(classId, ClassResponse.class))
                .orElse(null),
            Optional.ofNullable(teacher.getImageId())
                .map(imageId -> modelMapper.map(imageId, ImageResponseDTO.class))
                .orElse(null)));
    return responses;
  }

  // Retrieve a single teacher by ID
  public TeacherResponseDTO findOneTeacher(String username) {

    Users teacher = teacherRepository.findByUsernameAndStatus(username, ACTIVE).orElseThrow(
        () -> new IllegalArgumentException("Teacher not found with ID: " + username)
    );
    // Convert the found entity to a DTO
    return mapToResponseDTO(teacher);

  }


  @Transactional
  public TeacherResponseDTO updateTeacher(String username, TeacherRequestDTO requestDto) {
    Users teacher = teacherRepository.findByUsernameAndStatus(username, ACTIVE).orElseThrow(
        () -> new CustomException("Teacher with username " + username + " not found.",
            HttpStatus.NOT_FOUND)
    );

    if(teacher.getRole()!=Role.ROLE_TEACHER){
      throw new IllegalArgumentException("해당 user는 teacher가 아닙니다.");
    }

    boolean isFirstUpdate = teacher.getBirth() == null;

    if (isFirstUpdate) {
      if (requestDto.getBirth() == null || requestDto.getSex() == null
          || requestDto.getPwAnswer() == null || requestDto.getImageId() == null) {
        throw new IllegalStateException(
            "Birth, sex, Image and password answer are required for the first update.");
      }
      teacher.setBirth(requestDto.getBirth());
      teacher.setSex(requestDto.getSex());
      teacher.setPwAnswer(requestDto.getPwAnswer());
      Image image = imageRepository.findById(requestDto.getImageId())
          .orElseThrow(
              () -> new CustomException("Image not found with id: " + requestDto.getImageId(),
                  HttpStatus.NOT_FOUND));
      teacher.setImageId(image);
      updateAlwaysChangableField(requestDto, teacher);

    } else {
      if (requestDto.getPwAnswer() != null) {//최초 1회 접속이 아닌데 pwAnswer 요청 들어올 경우 예외처리
        // pwAnswer should not be provided after the first update
        throw new CustomException("Password answer can only be set during the first update.",
            HttpStatus.BAD_REQUEST);
      }

      updateAlwaysChangableField(requestDto, teacher);

      if (requestDto.getBirth() != null) {
        teacher.setBirth(requestDto.getBirth());
      }
      if (requestDto.getSex() != null) {
        teacher.setSex(requestDto.getSex());
      }
      if (requestDto.getImageId() != null) {
        Image image = imageRepository.findById(requestDto.getImageId())
            .orElseThrow(() -> new CustomException("Image not found", HttpStatus.NOT_FOUND));
        teacher.setImageId(image);
      }
    }

    teacherRepository.save(teacher);
    return mapToResponseDTO(teacher);


  }

  private void updateAlwaysChangableField(TeacherRequestDTO requestDto, Users teacher) {

    changePassword(requestDto, teacher);
    if (requestDto.getName() != null) {
      teacher.setName(requestDto.getName());
    }
    if (requestDto.getPhoneNum() != null) {
      teacher.setPhoneNum(requestDto.getPhoneNum());
    }

  }

  private void changePassword(TeacherRequestDTO requestDto, Users student) {
    // Update fields that can always be updated
    if (requestDto.getNewPassword() != null) {
      if (passwordEncoder.matches(requestDto.getCurrentPassword(), student.getPassword())) {
        student.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
      } else {
        throw new IllegalArgumentException("current password is wrong");
      }
    }
  }


  private void updateTeacherpasswordField(Supplier<String> getpassword,
      Consumer<String> setpassword,
      String password, String errorMessage) {
    if (password != null) {//새로운 비밀번호가 password에 담겨있음
      // 새로운 비밀번호가 제공된 경우 업데이트

    }
  }


  private void updateTeacherImageIdField(Supplier<Image> getImageId, Consumer<Image> setImageId,
      Long imageId, String errorMessage) {
    if (imageId != null) {
      Image image = imageRepository.findById(imageId)
          .orElseThrow(() -> new CustomException("Image not found with id: " + imageId,
              HttpStatus.NOT_FOUND));
      setImageId.accept(image);
    } else if (getImageId.get() == null) {
      throw new CustomException(errorMessage, HttpStatus.BAD_REQUEST);
    }

  }


  public Users findById(String username) {

    Users teacher = teacherRepository.findByUsername(username).orElseThrow(
        () -> new IllegalArgumentException("unvalid id")
    );
    return teacher;

  }

  private <T> void updateTeacherFieldIfNull(Supplier<T> getter, Consumer<T> setter, T value,
      String errorMessage) {
    if (getter.get() == null) {
      if (value == null) {
        throw new CustomException(errorMessage, HttpStatus.BAD_REQUEST);
      }
      setter.accept(value);
    }
  }

  @Transactional
  public String deleteTeacher(String username) {
    Users teacher = teacherRepository.findByUsername(username).orElseThrow(
        () -> new IllegalArgumentException("unvalid username")
    );

    // 매핑된 이미지가 있는 경우 삭제
    if (teacher.getImageId() != null) {
      Long imageId = teacher.getImageId().getImageId();
      logger.info("Deleting associated image with ID: {}", imageId);
      imageService.deleteProfileImage(imageId);
    }

    //매핑된 class가 있는 경우 삭제
    // 2. teacher가 속한 class의 users 리스트에서 해당 teacher를 제거한다
    Classes assignedClass = teacher.getClassId();
    if (assignedClass != null) {
      assignedClass.getUsers().remove(teacher); // 양방향 관계에서 제거
      teacher.setClassId(null);
    }

    // 논리적 삭제 수행
    teacher.setStatus(Status.INACTIVE);
    // Dummy Data
    teacher.setUpdatedChargeId(SecurityContextHolder.getContext().getAuthentication().getName());
    return teacher.getUsername();
  }


  public boolean isusernameExists(String username) {
    if (teacherRepository.existsByUsername(username)) {
      return true;
    }
    return false;
  }


  public TeacherResponseDTO mapToResponseDTO(Users savedTeacher) {
    // Create a new TeacherResponseDTO instance
    TeacherResponseDTO responseDto = new TeacherResponseDTO();

    // Set fields directly from the savedTeacher entity
    responseDto.setUsername(savedTeacher.getUsername());
    responseDto.setRole(savedTeacher.getRole());
    responseDto.setName(savedTeacher.getName());
    responseDto.setBirth(savedTeacher.getBirth());
    responseDto.setSex(savedTeacher.getSex());
    responseDto.setPhoneNum(savedTeacher.getPhoneNum());
    responseDto.setPwAnswer(savedTeacher.getPwAnswer());
    responseDto.setStatus(savedTeacher.getStatus());
//    responseDto.setCreatedAt(savedTeacher.getCreatedAt());
//    responseDto.setUpdatedAt(savedTeacher.getUpdatedAt());
//    responseDto.setCreatedChargeId(savedTeacher.getCreatedChargeId());
//    responseDto.setUpdatedChargeId(savedTeacher.getUpdatedChargeId());

    // Map the class entity (Classes) to ClassResponse if the class is assigned
    if (savedTeacher.getClassId() != null) {
      ClassResponse classResponse = modelMapper.map(savedTeacher.getClassId(),
          ClassResponse.class);
      responseDto.setClassId(classResponse);
    }

    // Map the image entity (Image) to ImageResponseDTO if the image is assigned
    if (savedTeacher.getImageId() != null) {
      ImageResponseDTO imageResponse = modelMapper.map(savedTeacher.getImageId(),
          ImageResponseDTO.class);
      responseDto.setImageId(imageResponse);
    }

    return responseDto;
  }


}
