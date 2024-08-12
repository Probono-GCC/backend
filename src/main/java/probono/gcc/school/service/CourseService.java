package probono.gcc.school.service;

import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import probono.gcc.school.exception.DuplicateEntityException;
import probono.gcc.school.model.dto.ClassResponse;
import probono.gcc.school.model.dto.CreateClassRequest;
import probono.gcc.school.model.dto.SubjectResponseDTO;
import probono.gcc.school.model.dto.course.CourseResponse;
import probono.gcc.school.model.dto.course.CreateCourseRequest;
import probono.gcc.school.model.entity.Classes;
import probono.gcc.school.model.entity.Course;
import probono.gcc.school.model.entity.Subject;
import probono.gcc.school.model.enums.Status;
import probono.gcc.school.repository.ClassRepository;
import probono.gcc.school.repository.CourseRepository;
import probono.gcc.school.repository.SubjectRepository;

@Service
@RequiredArgsConstructor
public class CourseService {

  private final ModelMapper modelMapper;
  private final CourseRepository courseRepository;
  private final ClassRepository classRepository;

  private final SubjectRepository subjectRepository;

  @Transactional
  public CourseResponse create(long classId, long subjectId) {
    Classes findClass = classRepository.findById(classId)
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 classId 입니다."));
    Subject findSubject = subjectRepository.findById(subjectId)
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 subjectId 입니다."));

    if (findClass.getStatus() == Status.INACTIVE
        || findSubject.getStatus() == Status.INACTIVE) {
      throw new NoSuchElementException("존재하지 않는 class 혹은 course 입니다.");
    }

    validateDuplicateCourse(findClass, findSubject);

    Course course = new Course();

    course.setClassId(findClass);
    course.setSubjectId(findSubject);
    course.setCreatedChargeId(-1L);

    Course savedCourse = courseRepository.save(course);
    return mapToResponseDto(savedCourse);
  }

  @Transactional(readOnly = true)
  public CourseResponse getCourse(long id) {
    Course findCourse = getCourseById(id);
    return mapToResponseDto(findCourse);
  }

  @Transactional
  public CourseResponse updateCourse(long id, CreateCourseRequest request) {
    Course existingCourse = getCourseById(id);

    Classes findClass = classRepository.findById(request.getClassId()).orElseThrow(
        () -> new NoSuchElementException("Class not found with id " + request.getClassId()));
    if (Status.INACTIVE.equals(findClass.getStatus())) {
      throw new NoSuchElementException("Class not found with id: " + request.getClassId());
    }

    Subject findSubject = subjectRepository.findById(request.getSubjectId()).orElseThrow(
        () -> new NoSuchElementException("Subject not found with id " + request.getSubjectId()));

    existingCourse.setClassId(findClass);
    existingCourse.setSubjectId(findSubject);
    existingCourse.setUpdatedChargeId(-1l);

    Course savedCourse = courseRepository.save(existingCourse);
    return mapToResponseDto(savedCourse);
  }

  @Transactional
  public void deleteCourse(long id) {
    Course existingCourse = getCourseById(id);
    existingCourse.setStatus(Status.INACTIVE);
    existingCourse.setUpdatedChargeId(-1l);

    courseRepository.save(existingCourse);
  }

  private void validateDuplicateCourse(Classes findClass, Subject findSubject) {
    if (courseRepository.existsByClassIdAndSubjectId(findClass, findSubject)) {
      throw new DuplicateEntityException("이미 존재하는 Course 입니다.");
    }
  }

  public Course getCourseById(long id) {
    Course findCourse = courseRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("course not found with id " + id));
    if (Status.INACTIVE.equals(findCourse.getStatus())) {
      throw new NoSuchElementException("course not found with id " + id);
    }

    return findCourse;
  }

  private CourseResponse mapToResponseDto(Course savedCourse) {
    CourseResponse responseDto = new CourseResponse();
    responseDto.setCourseId(savedCourse.getCourseId());

    ClassResponse savedClass = modelMapper.map(savedCourse.getClassId(), ClassResponse.class);
    SubjectResponseDTO savedSubject = modelMapper.map(savedCourse.getSubjectId(),
        SubjectResponseDTO.class);

    responseDto.setClassResponse(savedClass);
    responseDto.setSubjectResponseDTO((savedSubject));
    return responseDto;
  }
}
