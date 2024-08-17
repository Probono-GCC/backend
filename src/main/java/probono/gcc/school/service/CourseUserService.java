package probono.gcc.school.service;

import static probono.gcc.school.model.enums.Role.ROLE_STUDENT;
import static probono.gcc.school.model.enums.Role.ROLE_TEACHER;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import probono.gcc.school.exception.DuplicateEntityException;
import probono.gcc.school.model.dto.classes.ClassResponse;
import probono.gcc.school.model.dto.SubjectResponseDTO;
import probono.gcc.school.model.dto.course.CourseResponse;
import probono.gcc.school.model.dto.courseUser.CourseUserResponse;
import probono.gcc.school.model.dto.courseUser.CreateCourseUserRequest;
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

    CourseUser courseUser = new CourseUser();

    courseUser.setCourseId(findCourse);
    courseUser.setUsername(findUser);
    courseUser.setCreatedChargeId(-1l);

    if (Role.ROLE_STUDENT.equals(findUser.getRole())) {
      courseUser.setRole(Role.ROLE_STUDENT);
    } else if (Role.ROLE_STUDENT.equals(findUser.getRole())) {
      courseUser.setRole(ROLE_TEACHER);
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
    existingCourseUser.setUpdatedChargeId(-1l);

    CourseUser savedCourseUser = courseUserRepository.save(existingCourseUser);
    return mapToResponseDto(savedCourseUser);
  }

  @Transactional
  public void deleteCourseUser(long id) {
    CourseUser existingCourseUser = getCourseUserById(id);
    existingCourseUser.setStatus(Status.INACTIVE);
    existingCourseUser.setUpdatedChargeId(-1l);

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
    if (courseUserRepository.existsByCourseIdAndUsername(findCourse, findUser)) {
      throw new DuplicateEntityException("이미 존재하는 CourseUser 입니다.");
    }
  }


  public List<CourseUserResponse> getStudentsByCourseId(long courseId) {

    Course findCourse = courseRepository.findById(courseId)
        .orElseThrow(() -> new NoSuchElementException("Course not found with id: " + courseId));

    List<CourseUser> courseUsers = courseUserRepository.findByCourseId(findCourse);

    List<CourseUser> studentCourseUsers = courseUsers.stream()
        .filter(courseUser -> Role.ROLE_STUDENT.equals(courseUser.getRole()))
        .toList();

    return studentCourseUsers.stream()
        .map(courseUser -> mapToResponseDto(courseUser))  // Explicitly passing courseUser as parameter
        .toList();
  }


  public List<CourseUserResponse> getTeachersByCourseId(Long courseId) {
    Course findCourse = courseRepository.findById(courseId)
        .orElseThrow(() -> new NoSuchElementException("Course not found with id: " + courseId));

    List<CourseUser> courseUsers = courseUserRepository.findByCourseId(findCourse);

    List<CourseUser> studentCourseUsers = courseUsers.stream()
        .filter(courseUser -> Role.ROLE_TEACHER.equals(courseUser.getRole()))
        .toList();

    return studentCourseUsers.stream()
        .map(courseUser -> mapToResponseDto(courseUser))  // Explicitly passing courseUser as parameter
        .toList();

  }

  public CourseUserResponse assignTeacherToCourse(Long courseId, String teacherUsername) {
    Course course = courseRepository.findById(courseId)
        .orElseThrow(() -> new NoSuchElementException("Course not found"));
    Users teacher = userRepository.findByUsername(teacherUsername)
        .orElseThrow(() -> new NoSuchElementException("Teacher not found"));

    // Check if user is a teacher
    if (teacher.getRole()!=ROLE_TEACHER) {
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

  public CourseUserResponse assignStudentToCourse(Long courseId, String studentUsername) {
    Course course = courseRepository.findById(courseId)
        .orElseThrow(() -> new IllegalArgumentException("Course not found"));
    Users student = userRepository.findByUsername(studentUsername)
        .orElseThrow(() -> new IllegalArgumentException("Student not found"));

    if (student.getRole()!=ROLE_STUDENT) {
      throw new IllegalArgumentException("User is not a student");
    }

    // Assign the student to the course
    CourseUser courseUser = new CourseUser();
    courseUser.setCourseId(course);
    courseUser.setUsername(student);
    courseUser.setRole(ROLE_STUDENT);  // Optionally set the role here
    courseUserRepository.save(courseUser);

    return mapToResponseDto(courseUser);

  }

  private CourseUserResponse mapToResponseDto(CourseUser savedCourseUser) {
    CourseUserResponse responseDto = new CourseUserResponse();

    responseDto.setCourseUserId(savedCourseUser.getCuId());

    CourseResponse savedCourse = modelMapper.map(savedCourseUser.getCourseId(),
        CourseResponse.class);
    UserResponse savedUser = modelMapper.map(savedCourseUser.getUsername(),
        UserResponse.class);
    ClassResponse savedClass = modelMapper.map(savedCourseUser.getCourseId().getClassId(),
        ClassResponse.class);
    SubjectResponseDTO savedSubject = modelMapper.map(savedCourseUser.getCourseId().getSubjectId(),
        SubjectResponseDTO.class);

    responseDto.setCourse(savedCourse);
    responseDto.getCourse().setClassResponse(savedClass);
    responseDto.getCourse().setSubjectResponseDTO(savedSubject);
    responseDto.setUser(savedUser);
    return responseDto;
  }
}
