package probono.gcc.school.service;

import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import probono.gcc.school.exception.DuplicateEntityException;
import probono.gcc.school.model.dto.ClassResponse;
import probono.gcc.school.model.dto.StudentDTO;
import probono.gcc.school.model.dto.SubjectResponseDTO;
import probono.gcc.school.model.dto.course.CourseResponse;
import probono.gcc.school.model.dto.courseUser.CourseUserResponse;
import probono.gcc.school.model.dto.courseUser.CreateCourseUserRequest;
import probono.gcc.school.model.entity.Classes;
import probono.gcc.school.model.entity.Course;
import probono.gcc.school.model.entity.CourseUser;
import probono.gcc.school.model.entity.Subject;
import probono.gcc.school.model.entity.Users;
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
    Users findUser = userRepository.findByLoginId(request.getLoginId())
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 userId 입니다."));

    if (findCourse.getStatus() == Status.INACTIVE
        || findUser.getStatus() == Status.INACTIVE) {
      throw new NoSuchElementException("존재하지 않는 course 혹은 user 입니다.");
    }

    validateDuplicateCourseUser(findCourse, findUser);

    CourseUser courseUser = new CourseUser();

    courseUser.setCourseId(findCourse);
    courseUser.setLoginId(findUser);
    courseUser.setCreatedChargeId(-1l);

    CourseUser savedCourseUser = courseUserRepository.save(courseUser);
    return mapToResponseDto(savedCourseUser);
  }

  private void validateDuplicateCourseUser(Course findCourse, Users findUser) {
    if (courseUserRepository.existsByCourseIdAndLoginId(findCourse, findUser)) {
      throw new DuplicateEntityException("이미 존재하는 CourseUser 입니다.");
    }
  }

  private CourseUserResponse mapToResponseDto(CourseUser savedCourseUser) {
    CourseUserResponse responseDto = new CourseUserResponse();

    responseDto.setCourseUserId(savedCourseUser.getCuId());

    CourseResponse savedCourse = modelMapper.map(savedCourseUser.getCourseId(),
        CourseResponse.class);
    StudentDTO savedUser = modelMapper.map(savedCourseUser.getLoginId(), StudentDTO.class);

    responseDto.setCourse(savedCourse);
    responseDto.setUser(savedUser);
    return responseDto;
  }
}
