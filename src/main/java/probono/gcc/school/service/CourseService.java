package probono.gcc.school.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import probono.gcc.school.exception.DuplicateEntityException;
import probono.gcc.school.model.dto.classes.ClassResponse;
import probono.gcc.school.model.dto.SubjectResponseDTO;
import probono.gcc.school.model.dto.course.CourseResponse;
import probono.gcc.school.model.dto.course.CreateCourseRequest;
import probono.gcc.school.model.entity.Classes;
import probono.gcc.school.model.entity.Course;
import probono.gcc.school.model.entity.CourseUser;
import probono.gcc.school.model.entity.Notice;
import probono.gcc.school.model.entity.Subject;
import probono.gcc.school.model.enums.Status;
import probono.gcc.school.repository.ClassRepository;
import probono.gcc.school.repository.CourseRepository;
import probono.gcc.school.repository.CourseUserRepository;
import probono.gcc.school.repository.NoticeRepository;
import probono.gcc.school.repository.SubjectRepository;

@Service
@RequiredArgsConstructor
public class CourseService {

  private final ModelMapper modelMapper;
  private final CourseRepository courseRepository;
  private final ClassRepository classRepository;

  private final SubjectRepository subjectRepository;

  private final NoticeRepository noticeRepository;

  private final NoticeService noticeService;

  private final CourseUserRepository courseUserRepository;

  private final CourseUserService courseUserService;

  private static final Logger logger = LoggerFactory.getLogger(CourseService.class);

  @Transactional
  public CourseResponse create(Long classId, long subjectId) {

    Subject findSubject = subjectRepository.findById(subjectId)
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 subjectId 입니다."));

    if (classId == null && !findSubject.isElective()) { // 공통과목인데 classId가 null일 경우
      throw new IllegalArgumentException("공통과목의 classId는 null일 수 없습니다.");
    }

    Classes findClass=null;
    if(classId!=null) {//공통과목일 때
     findClass = classRepository.findById(classId)
          .orElseThrow(() -> new NoSuchElementException("존재하지 않는 classId 입니다."));
    }

    if (findSubject.getStatus() == Status.INACTIVE) {
      if(findClass!=null & findClass.getStatus() == Status.INACTIVE){ //공통과목일 때
        throw new NoSuchElementException("존재하지 않는 class 혹은 course 입니다.");
      }
      throw new NoSuchElementException("존재하지 않는 class 혹은 course 입니다."); //선택과목일 때
    }

    validateDuplicateCourse(findClass, findSubject);

    logger.info("end of excption create() in CourseService");


    Course course = new Course();
    if(!findSubject.isElective()) { //공통과목일 때(findClass가 null이 아닐때)
      course.setClassId(findClass);
    }
    course.setSubjectId(findSubject);
    course.setCreatedChargeId(-1L);

    Course savedCourse = courseRepository.save(course);
    logger.info("end of create() in CourseService");
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

    List<Notice> courseNoticeList = noticeRepository.findByCourseId(existingCourse);
    for (Notice notice : courseNoticeList) {
      noticeService.deleteNotice(notice.getNoticeId());
    }

    List<CourseUser> courseUsersList = courseUserRepository.findByCourseId(existingCourse);
    for (CourseUser courseUser : courseUsersList) {
      courseUserService.deleteCourseUser(courseUser.getCuId());
    }

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

  @Transactional(readOnly = true)
  public Page<CourseResponse> getAllCourses(int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Order.asc("createdAt")));

    Page<Course> allCourse = courseRepository.findByStatus(Status.ACTIVE, pageRequest);

//    if (allCourse.isEmpty()) {
//      throw new NoSuchElementException("No courses found.");
//    }

    Page<CourseResponse> response = allCourse.map(
        course -> new CourseResponse(course.getCourseId(), modelMapper.map(course.getClassId(),
            ClassResponse.class),
            modelMapper.map(course.getSubjectId(), SubjectResponseDTO.class)));
    return response;
  }

  private CourseResponse mapToResponseDto(Course savedCourse) {
    CourseResponse responseDto = new CourseResponse();
    responseDto.setCourseId(savedCourse.getCourseId());

    if(savedCourse.getClassId()!=null) {
      ClassResponse savedClass = modelMapper.map(savedCourse.getClassId(), ClassResponse.class);
      responseDto.setClassResponse(savedClass);
    }
    SubjectResponseDTO savedSubject = modelMapper.map(savedCourse.getSubjectId(),
        SubjectResponseDTO.class);

    responseDto.setSubjectResponseDTO((savedSubject));
    return responseDto;
  }


}
