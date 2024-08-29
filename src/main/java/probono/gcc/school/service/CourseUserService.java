package probono.gcc.school.service;

import static probono.gcc.school.model.enums.Role.ROLE_STUDENT;
import static probono.gcc.school.model.enums.Role.ROLE_TEACHER;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import probono.gcc.school.exception.DuplicateEntityException;
import probono.gcc.school.model.dto.classes.ClassResponse;
import probono.gcc.school.model.dto.SubjectResponseDTO;
import probono.gcc.school.model.dto.course.CourseResponse;
import probono.gcc.school.model.dto.courseUser.CourseUserResponse;
import probono.gcc.school.model.dto.courseUser.CreateCourseUserRequest;
import probono.gcc.school.model.dto.image.ImageResponseDTO;
import probono.gcc.school.model.dto.users.UserResponse;
import probono.gcc.school.model.entity.Course;
import probono.gcc.school.model.entity.CourseUser;
import probono.gcc.school.model.entity.Users;
import probono.gcc.school.model.enums.Role;
import probono.gcc.school.model.enums.Status;
import probono.gcc.school.repository.CourseRepository;
import probono.gcc.school.repository.CourseUserRepository;
import probono.gcc.school.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CourseUserService {

  private final ModelMapper modelMapper;

  private final CourseUserRepository courseUserRepository;

  private final CourseRepository courseRepository;

  private final UserRepository userRepository;

  @Transactional
  public CourseUserResponse create(CreateCourseUserRequest request) {
    Course findCourse = courseRepository.findById(request.getCourseId())
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 courseId 입니다."));
    Users findUser = userRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 userId 입니다."));

    if (findCourse.getStatus() == Status.INACTIVE
        || findUser.getStatus() == Status.INACTIVE) {
      throw new NoSuchElementException("존재하지 않는 course 혹은 user 입니다.");
    }

    validateDuplicateCourseUser(findCourse, findUser);
    //course에 teacher 이미 할당했으면 예외처리
    if (findUser.getRole().equals(ROLE_TEACHER)) {
      AlreadyAssignedTeacherInCourse(findCourse, findUser);
    }

    CourseUser courseUser = new CourseUser();

    courseUser.setCourseId(findCourse);
    courseUser.setUsername(findUser);
    courseUser.setCreatedChargeId(SecurityContextHolder.getContext().getAuthentication().getName());

    if (Role.ROLE_STUDENT.equals(findUser.getRole())) {
      courseUser.setRole(Role.ROLE_STUDENT);
    } else if (Role.ROLE_TEACHER.equals(findUser.getRole())) {
      courseUser.setRole(Role.ROLE_TEACHER);
    } else {
      throw new IllegalArgumentException("course에 할당할 수 없는 유저입니다.");
    }

    CourseUser savedCourseUser = courseUserRepository.save(courseUser);
    return mapToResponseDto(savedCourseUser);
  }

  @Transactional(readOnly = true)
  public CourseUserResponse getCourseUser(long id) {
    CourseUser findCourseUser = getCourseUserById(id);
    return mapToResponseDto(findCourseUser);
  }

  @Transactional
  public CourseUserResponse updateCourseUser(long id, CreateCourseUserRequest request) {
    CourseUser existingCourseUser = getCourseUserById(id);

    Course findCourse = courseRepository.findById(request.getCourseId()).orElseThrow(
        () -> new NoSuchElementException("Course not found with id " + request.getCourseId()));
    if (Status.INACTIVE.equals(findCourse.getStatus())) {
      throw new NoSuchElementException("Course not found with id: " + request.getCourseId());
    }

    Users findUser = userRepository.findByUsername(request.getUsername()).orElseThrow(
        () -> new NoSuchElementException("User not found with username " + request.getUsername()));

    existingCourseUser.setCourseId(findCourse);
    existingCourseUser.setUsername(findUser);
    existingCourseUser.setUpdatedChargeId(
        SecurityContextHolder.getContext().getAuthentication().getName());

    CourseUser savedCourseUser = courseUserRepository.save(existingCourseUser);
    return mapToResponseDto(savedCourseUser);
  }

  @Transactional
  public void deleteCourseUser(long id) {
    CourseUser existingCourseUser = getCourseUserById(id);
    existingCourseUser.setStatus(Status.INACTIVE);
    existingCourseUser.setUpdatedChargeId(
        SecurityContextHolder.getContext().getAuthentication().getName());

    courseUserRepository.save(existingCourseUser);
  }

  public CourseUser getCourseUserById(long id) {
    CourseUser findCourseUser = courseUserRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("courseUser not found with id " + id));
    if (Status.INACTIVE.equals(findCourseUser.getStatus())) {
      throw new NoSuchElementException("courseUser not found with id " + id);
    }

    return findCourseUser;
  }

  private void validateDuplicateCourseUser(Course findCourse, Users findUser) {
    if (courseUserRepository.existsByCourseIdAndUsernameAndStatus(findCourse, findUser,
        Status.ACTIVE)) {
      throw new DuplicateEntityException("이미 존재하는 CourseUser 입니다.");
    }
  }

  private void AlreadyAssignedTeacherInCourse(Course findCourse, Users findUser) {
    //이미 해당 course에 할당된 teacher가 존재하면

    if (!courseUserRepository.findByCourseIdAndRoleAndStatus(findCourse, ROLE_TEACHER,
        Status.ACTIVE).isEmpty()) {
      throw new IllegalArgumentException("이미 해당 Course에 Teacher가 할당되어 있습니다");
    }
  }


  //  public <CourseUserResponse> getStudentsByCourseId(long courseId) {
//
//    Course findCourse = courseRepository.findById(courseId)
//        .orElseThrow(() -> new NoSuchElementException("Course not found with id: " + courseId));
//
//    List<CourseUser> courseUsers = courseUserRepository.findByCourseId(findCourse);
//
//    List<CourseUser> studentCourseUsers = courseUsers.stream()
//        .filter(courseUser -> Role.ROLE_STUDENT.equals(courseUser.getRole()))
//        .toList();
//
//    return studentCourseUsers.stream()
//        .map(courseUser -> mapToResponseDto(courseUser))  // Explicitly passing courseUser as parameter
//        .toList();
//  }
  public Page<CourseUserResponse> getStudentsByCourseId(long courseId, int page, int size) {
    Course findCourse = (Course) courseRepository.findByCourseIdAndStatus(courseId,Status.ACTIVE)
        .orElseThrow(() -> new NoSuchElementException("Course not found with id: " + courseId));

    //첫 페이지, 가져올 갯수, 정렬기준, 정렬 필드 설정
    PageRequest pageRequest = PageRequest.of(page, size,
        Sort.by(Sort.Order.asc("cuId")));

    //조회
    Page<CourseUser> findList = courseUserRepository.findByStatusAndRoleAndCourseId(Status.ACTIVE,
        Role.ROLE_STUDENT, findCourse,
        pageRequest);

    if (findList.isEmpty()) {
      throw new NoSuchElementException("Student not found with courseId : " + courseId);
    }

    //DTO변환
    Page<CourseUserResponse> response = findList.map(
        courseUser -> new CourseUserResponse(courseUser.getCuId(),
            modelMapper.map(courseUser.getUsername(), UserResponse.class),
            modelMapper.map(findCourse,
                CourseResponse.class)));
    return response;
  }

  public Page<CourseUserResponse> getTeachersByCourse(long courseId, int page, int size) {
    Course findCourse = courseRepository.findById(courseId)
        .orElseThrow(() -> new NoSuchElementException("Course not found with id: " + courseId));

    //첫 페이지, 가져올 갯수, 정렬기준, 정렬 필드 설정
    PageRequest pageRequest = PageRequest.of(page, size,
        Sort.by(Sort.Order.asc("cuId")));

    //조회
    Page<CourseUser> findList = courseUserRepository.findByStatusAndRoleAndCourseId(Status.ACTIVE,
        Role.ROLE_TEACHER, findCourse,
        pageRequest);

    if (findList.isEmpty()) {
      throw new NoSuchElementException("Teacher not found with courseId : " + courseId);
    }

    //DTO변환
    Page<CourseUserResponse> response = findList.map(
        courseUser -> new CourseUserResponse(courseUser.getCuId(),
            modelMapper.map(courseUser.getUsername(), UserResponse.class),
            modelMapper.map(findCourse,
                CourseResponse.class)));
    return response;
  }


  public CourseUserResponse getTeacherByCourseId(Long courseId) {
    // courseId로 course 객체 조회
    Course findCourse = (Course) courseRepository.findByCourseIdAndStatus(courseId,Status.ACTIVE)
        .orElseThrow(() -> new NoSuchElementException("Course not found with id: " + courseId));

    // 해당 courseId에 해당하는 CourseUser 목록 조회
    List<CourseUser> courseUsers = courseUserRepository.findByCourseId(findCourse);

    // 해당 courseUsers 중에서 Role이 ROLE_TEACHER인 것만 필터링
    CourseUser teacherCourseUser = courseUsers.stream()
        .filter(courseUser -> Role.ROLE_TEACHER.equals(courseUser.getRole()))
        .findFirst()  // 하나만 있을 것으로 기대하므로 첫 번째 요소 반환
        .orElseThrow(
            () -> new NoSuchElementException("No teacher found for course with id: " + courseId));

    // CourseUser 객체를 CourseUserResponse로 변환하여 반환
    return mapToResponseDto(teacherCourseUser);  // courseUser -> response로 변환하는 메서드

  }

  public CourseUserResponse assignTeacherToCourse(Long courseId, String teacherUsername) {
    Course course = courseRepository.findById(courseId)
        .orElseThrow(() -> new NoSuchElementException("Course not found"));
    Users teacher = userRepository.findByUsername(teacherUsername)
        .orElseThrow(() -> new NoSuchElementException("Teacher not found"));

    // Check if user is a teacher
    if (teacher.getRole() != ROLE_TEACHER) {
      throw new IllegalArgumentException("User is not a teacher");
    }

    // Assign the teacher to the course
    CourseUser courseUser = new CourseUser();
    courseUser.setCourseId(course);
    courseUser.setUsername(teacher);
    courseUser.setRole(ROLE_TEACHER);
    courseUserRepository.save(courseUser);

    return mapToResponseDto(courseUser);

  }


  private CourseUserResponse mapToResponseDto(CourseUser savedCourseUser) {
    CourseUserResponse responseDto = new CourseUserResponse();

    responseDto.setCourseUserId(savedCourseUser.getCuId());

    CourseResponse savedCourse = modelMapper.map(savedCourseUser.getCourseId(),
        CourseResponse.class);
    ClassResponse savedClass = modelMapper.map(savedCourseUser.getCourseId().getClassId(),
        ClassResponse.class);
    SubjectResponseDTO savedSubject = modelMapper.map(savedCourseUser.getCourseId().getSubjectId(),
        SubjectResponseDTO.class);

    UserResponse savedUser = modelMapper.map(savedCourseUser.getUsername(),
        UserResponse.class);

    responseDto.setCourse(savedCourse);
    responseDto.getCourse().setClassResponse(savedClass);
    responseDto.getCourse().setSubjectResponseDTO(savedSubject);
    responseDto.setUserResponse(savedUser);
    return responseDto;
  }

  public Page<CourseUserResponse> getCoursesByTeacherUsername(String username, int page, int size) {
    Users teacher = userRepository.findByUsernameAndStatus(username,Status.ACTIVE)
        .orElseThrow(() -> new NoSuchElementException("Teacher not found"));
    List<CourseUser> courseUserList = courseUserRepository.findByUsernameAndRole(teacher,
        ROLE_TEACHER);
    List<CourseUserResponse> collect = courseUserList.stream()
        .map(this::mapToResponseDto)
        .collect(Collectors.toList());

    PageRequest pageRequest = PageRequest.of(page, size);
    int start = (int) pageRequest.getOffset();
    int end = Math.min((start + pageRequest.getPageSize()), collect.size());
    Page<CourseUserResponse> response = new PageImpl<>(collect.subList(start, end), pageRequest,
        collect.size());
    return response;
  }
}
