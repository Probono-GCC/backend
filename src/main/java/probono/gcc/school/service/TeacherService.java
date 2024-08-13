package probono.gcc.school.service;

import static probono.gcc.school.model.enums.Status.ACTIVE;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import probono.gcc.school.exception.CustomException;
import probono.gcc.school.model.dto.ImageResponseDTO;
import probono.gcc.school.model.dto.users.TeacherRequestDTO;
import probono.gcc.school.model.dto.users.TeacherResponseDTO;
import probono.gcc.school.model.entity.Classes;
import probono.gcc.school.model.entity.Image;
import probono.gcc.school.model.entity.Subject;
import probono.gcc.school.model.entity.Users;
import probono.gcc.school.model.enums.Role;
import probono.gcc.school.model.enums.Sex;
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
  private static final Logger logger = LoggerFactory.getLogger(TeacherService.class);


  public TeacherResponseDTO createTeacher(TeacherRequestDTO requestDto) {

    try {
      // 필수 필드 null 체크
      if (requestDto.getName() == null || requestDto.getName().trim().isEmpty()) {
        logger.info("Name is required.");
        throw new CustomException("Name is required.", HttpStatus.BAD_REQUEST);
      }

      if (requestDto.getLoginId() == null || requestDto.getLoginId().trim().isEmpty()) {
        logger.info("LoginId is required.");
        throw new CustomException("Login ID is required.", HttpStatus.BAD_REQUEST);
      }

      if (requestDto.getLoginPw() == null || requestDto.getLoginPw().trim().isEmpty()) {
        logger.info("LoginPw is required.");
        throw new CustomException("Login Password is required.", HttpStatus.BAD_REQUEST);
      }

      //loginId 중복 확인 체크
      if (teacherRepository.existsByLoginId(requestDto.getLoginId())) {
        throw new CustomException("Login ID already exists.", HttpStatus.CONFLICT);
      }

      // Create a new Users entity for the teacher
      Users teacher = new Users();
      teacher.setName(requestDto.getName());
      teacher.setLoginId(requestDto.getLoginId());
      teacher.setLoginPw(requestDto.getLoginPw());
      teacher.setStatus(ACTIVE);
      teacher.setCreatedChargeId(1L); // Set the createdChargeId
      teacher.setRole(Role.TEACHER);
      teacher.setSerialNumber(null);

      // Save the teacher entity to the database
      Users teacherCreated = teacherRepository.save(teacher);

      // Convert and return the saved entity to a DTO
      return modelMapper.map(teacherCreated, TeacherResponseDTO.class);

    }
    catch (DataIntegrityViolationException ex) {
      // Handle database-related exceptions (including SQL constraint violations)
      logger.error("Database integrity violation: {}", ex.getMessage());
      throw new CustomException("Teacher creation failed due to conflict with existing data.", HttpStatus.CONFLICT);

    }
//    catch (Exception ex) {
//      // Handle any other unforeseen exceptions
//      logger.error("Unexpected error during teacher creation: {}", ex.getMessage());
//      throw new CustomException("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
//    }


  }

  // Retrieve all teachers
  public List<TeacherResponseDTO> findAllTeachers() {
    try {
      List<Users> teacherList = teacherRepository.findAll();
      // Use stream and ModelMapper to convert entity list to DTO list
      return teacherList.stream()
          .map(teacher -> modelMapper.map(teacher, TeacherResponseDTO.class))
          .collect(Collectors.toList());
    } catch (Exception e) {
      // Handle any exceptions that occur during the fetching process
      logger.error("An error occurred while fetching teachers: {}", e.getMessage());
      throw new RuntimeException("An error occurred while fetching teachers", e);
    }
  }

  // Retrieve a single teacher by ID
  public TeacherResponseDTO findOneTeacher(String loginId) {
    try {
      Users teacher = teacherRepository.findByLoginId(loginId).orElseThrow(
          () -> new IllegalArgumentException("Teacher not found with ID: " + loginId)
      );
      // Convert the found entity to a DTO
      return modelMapper.map(teacher, TeacherResponseDTO.class);
    } catch (IllegalArgumentException e) {
      logger.error("Teacher not found: {}", e.getMessage());
      throw e;
    } catch (Exception e) {
      logger.error("Unexpected error occurred while fetching the teacher: {}", e.getMessage());
      throw new RuntimeException("An unexpected error occurred while fetching the teacher", e);
    }
  }

  @Transactional
  public String updateTeacher(String loginId, TeacherRequestDTO requestDto) {
    logger.info("enter into updateTeacher");
    // birth , sex , pwAnswer null
    //loginId로 DB 조회했을 때 birth, sex , pwAnswer가 null이라면 requestDto에서 해당값 get해서 update하기
    // 세 값 하나라도 빠지면 예외처리
    // DB에서 loginId로 교사 정보 조회
    Users teacher = teacherRepository.findByLoginId(loginId).orElseThrow(
        () -> new CustomException("Teacher with loginId " + loginId + " not found.",HttpStatus.NOT_FOUND)
    );

    if (teacher.getBirth() == null && teacher.getSex() == null && teacher.getPwAnswer() == null) {
      firstTimeUpdate(teacher, requestDto);
    } else {
      logger.info("enter into subsequentUpdate()");
      subsequentUpdate(teacher, requestDto);
    }

    teacherRepository.save(teacher);


    return teacher.getLoginId();

  }


  private void subsequentUpdate(Users teacher, TeacherRequestDTO requestDto) {
    //name,birth,phoneNum,loginPw,imageId
    if (requestDto.getName() != null) {
      teacher.setName(requestDto.getName());
    }
    if (requestDto.getBirth() != null) {
      teacher.setBirth(requestDto.getBirth());
    }
    if (requestDto.getPhoneNum() != null) {
      teacher.setPhoneNum(requestDto.getPhoneNum());
    }
    if (requestDto.getLoginPw() != null) {
      teacher.setLoginPw(requestDto.getLoginPw());
    }
    if (requestDto.getImageId() != null) {
      Image image = imageRepository.findById(requestDto.getImageId())
          .orElseThrow(() -> new CustomException("Image not found",HttpStatus.NOT_FOUND));
      teacher.setImageId(image);
    }


    //`updated_charged_id`를 설정
    teacher.setUpdatedChargeId(2L); // Dummy data 설정

    // 엔티티 상태가 변경되었으므로 JPA는 이를 자동으로 감지하고 업데이트
    // `save` 메소드를 호출하지 않아도 트랜잭션 종료 시 자동으로 업데이트

  }

  private void firstTimeUpdate(Users teacher, TeacherRequestDTO requestDto) {
    updateTeacherFieldIfNull(teacher::getBirth, teacher::setBirth, requestDto.getBirth(), "Birth is missing in the request.");
    updateTeacherFieldIfNull(teacher::getSex, teacher::setSex, requestDto.getSex(), "Sex is missing in the request.");
    updateTeacherFieldIfNull(teacher::getPwAnswer, teacher::setPwAnswer, requestDto.getPwAnswer(), "Password answer is missing in the request.");

    //requestDto에 imageId가 있으면 update
    updateTeacherImageIdField(teacher::getImageId, teacher::setImageId, requestDto.getImageId(), "Image ID is missing in the request.");
    //requestDto에 loginPw가 있으면 update
    updateTeacherLoginPwField(teacher::getLoginPw,teacher::setLoginPw,requestDto.getLoginPw(),"Login Pw is missing in the request.");


  }

  private void updateTeacherLoginPwField(Supplier<String> getLoginPw, Consumer<String> setLoginPw, String loginPw, String errorMessage) {
    if (loginPw != null) {
      // 새로운 비밀번호가 제공된 경우 업데이트
      setLoginPw.accept(loginPw);
    } else if (getLoginPw.get() == null) {
      // 기존 비밀번호가 없고 새로운 비밀번호도 제공되지 않은 경우 예외 처리
      throw new CustomException(errorMessage, HttpStatus.BAD_REQUEST);
    }
  }



  private void updateTeacherImageIdField(Supplier<Image> getImageId, Consumer<Image> setImageId, Long imageId, String errorMessage) {
    if (imageId != null) {
      Image image = imageRepository.findById(imageId)
          .orElseThrow(() -> new CustomException("Image not found with id: " + imageId, HttpStatus.NOT_FOUND));
      setImageId.accept(image);
    } else if (getImageId.get() == null) {
      throw new CustomException(errorMessage, HttpStatus.BAD_REQUEST);
    }

  }


  public Users findById(String loginId) {

    Users teacher = teacherRepository.findByLoginId(loginId).orElseThrow(
        () -> new IllegalArgumentException("unvalid id")
    );
    return teacher;

  }

  private <T> void updateTeacherFieldIfNull(Supplier<T> getter, Consumer<T> setter, T value, String errorMessage) {
    if (getter.get() == null) {
      if (value == null) {
        throw new CustomException(errorMessage, HttpStatus.BAD_REQUEST);
      }
      setter.accept(value);
    }
  }

  @Transactional
  public String deleteTeacher(String loginId) {
    Users teacher = teacherRepository.findByLoginId(loginId).orElseThrow(
        () -> new IllegalArgumentException("unvalid loginId")
    );
    // 논리적 삭제 수행
    teacher.setStatus(Status.INACTIVE);
    // Dummy Data
    teacher.setUpdatedChargeId(2L);
    return teacher.getLoginId();
  }


  public boolean isLoginIdExists(String loginId) {
    if (teacherRepository.existsByLoginId(loginId)) {
      return true;
    }
    return false;
  }

  //teacher의 담당 class 할당
  public Users assignClass(String loginId, Long classId) {
    // Find the teacher by loginId
    Users teacher = teacherRepository.findByLoginId(loginId)
        .orElseThrow(() -> new CustomException("Teacher not found with ID: " + loginId, HttpStatus.NOT_FOUND));

    // Find the class by classId
    Classes assignedClass = classRepository.findById(classId)
        .orElseThrow(() -> new CustomException("Class not found with ID: " + classId, HttpStatus.NOT_FOUND));

    Hibernate.initialize(assignedClass.getNotice());

    // Assign the class to the teacher
    teacher.setClassId(assignedClass);

    // Save the updated teacher entity
    teacherRepository.save(teacher);

    return teacher;

  }
}
