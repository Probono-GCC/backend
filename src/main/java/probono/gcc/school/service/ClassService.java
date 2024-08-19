package probono.gcc.school.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import probono.gcc.school.exception.DuplicateEntityException;
import probono.gcc.school.mapper.StudentMapper;
import probono.gcc.school.mapper.TeacherMapper;
import probono.gcc.school.model.dto.classes.ClassResponse;
import probono.gcc.school.model.dto.classes.CreateClassRequest;
import probono.gcc.school.model.dto.users.StudentResponseDTO;
import probono.gcc.school.model.dto.users.TeacherResponseDTO;
import probono.gcc.school.model.entity.Classes;
import probono.gcc.school.model.entity.Course;
import probono.gcc.school.model.entity.Users;
import probono.gcc.school.model.enums.Role;
import probono.gcc.school.model.enums.Status;
import probono.gcc.school.repository.ClassRepository;
import probono.gcc.school.repository.CourseRepository;

@Service
@RequiredArgsConstructor

public class ClassService {

  private final ModelMapper modelMapper;
  private final ClassRepository classRepository;

  private final CourseService courseService;
  private final TeacherMapper teacherMapper;
  private final StudentMapper studentMapper;

  private final CourseRepository courseRepository;
  private static final Logger logger = LoggerFactory.getLogger(ClassService.class);

  @Transactional
  public ClassResponse create(Classes requestClass) {

    validateDuplicateClass(requestClass); //중복 클래스 검증
    Classes savedClass = classRepository.save(requestClass);
    return modelMapper.map(savedClass, ClassResponse.class);
    //return mapToResponseDto(savedClass);
  }

  private void validateDuplicateClass(Classes requestClass) {
    if (classRepository.existsByYearAndGradeAndSectionAndStatus(requestClass.getYear(),
        requestClass.getGrade(), requestClass.getSection(), Status.ACTIVE)) {
      throw new DuplicateEntityException("이미 존재하는 class 입니다.");
    }
  }

  @Transactional(readOnly = true)
  public ClassResponse getClass(Long id) {
    Classes classEntity = this.getClassById(id);
    return mapToResponseDto(classEntity);
  }

  @Transactional
  public ClassResponse updateClass(Long id, CreateClassRequest request) {
    Classes existingClass = this.getClassById(id);
    existingClass.setGrade(request.getGrade());
    existingClass.setSection(request.getSection());
    existingClass.setYear(request.getYear());
    existingClass.setUpdatedChargeId(
        SecurityContextHolder.getContext().getAuthentication().getName());

    Classes savedClass = classRepository.save(existingClass);
    return mapToResponseDto(savedClass);
  }

  @Transactional
  public void deleteClass(Long id) {
    Classes existingClass = this.getClassById(id);
    existingClass.setStatus(Status.INACTIVE);
    existingClass.setUpdatedChargeId(
        SecurityContextHolder.getContext().getAuthentication().getName());

    List<Course> classCourseList = courseRepository.findByClassId(existingClass);
    for (Course course : classCourseList) {
      courseService.deleteCourse(course.getCourseId());
    }

    Classes savedClass = classRepository.save(existingClass);
  }

  public Classes getClassById(Long id) {
    Optional<Classes> findClass = classRepository.findById(id);
    if (findClass.isEmpty() || Status.INACTIVE.equals(findClass.get().getStatus())) {
      throw new NoSuchElementException("Class not found with id: " + id);
    }

    return findClass.get();
  }

  public Page<ClassResponse> getClassList(int page, int size, int year) {

    //첫 페이지, 가져올 갯수, 정렬기준, 정렬 필드 설정
    PageRequest pageRequest = PageRequest.of(page, size,
        Sort.by(Sort.Order.asc("grade"), Sort.Order.asc("section")));

    //조회
    Page<Classes> findClassList = classRepository.findByStatusAndYear(Status.ACTIVE, year,
        pageRequest);
    if (findClassList.isEmpty()) {
      throw new NoSuchElementException("Class not found with year: " + year);
    }

    //DTO변환
    Page<ClassResponse> classResponse = findClassList.map(
        classes -> new ClassResponse(classes.getClassId(), classes.getYear(), classes.getGrade(),
            classes.getSection()));
    return classResponse;
  }

//  @Transactional
//  public List<NoticeResponse> getClassNoticeList(Long id) {
//    Classes findClass = getClassById(id);
//    List<Notice> notice = findClass.getNotice();
//
//    List<NoticeResponse> collect = notice.stream()
//        .filter(n -> n.getStatus() == Status.ACTIVE)
//        .map(
//            m -> new NoticeResponse(m.getNoticeId(), m.getTitle(), m.getContent(), m.getCreatedAt(),
//                m.getUpdatedAt(), m.getCreatedChargeId(), m.getUpdatedChargeId(), m.getViews(),
//                m.getImageList().stream()
//                    .map(image -> modelMapper.map(image, ImageResponseDTO.class))
//                    .collect(Collectors.toList())
//            ))
//        .collect(
//            Collectors.toList());
//    return collect;
//  }

//  @Transactional
//  public Page<NoticeResponse> getClassNoticeList(Long id, int page, int size) {
//    //첫 페이지, 가져올 갯수, 정렬기준, 정렬 필드 설정
//    PageRequest pageRequest = PageRequest.of(page, size,
//        Sort.by(Sort.Order.asc("createdAt")));
//
//    //조회
//    Page<Classes> findClassNoticeList = classRepository.findByStatusAndYear(Status.ACTIVE, year,
//        pageRequest);
//    if (findClassList.isEmpty()) {
//      throw new NoSuchElementException("Class not found with year: " + year);
//    }
//
//    //DTO변환
//    Page<ClassResponse> classResponse = findClassList.map(
//        classes -> new ClassResponse(classes.getClassId(), classes.getYear(), classes.getGrade(),
//            classes.getSection()));
//    return collect;
//  }


  public ClassResponse mapToResponseDto(Classes savedClass) {
    ClassResponse responseDto = new ClassResponse();
    responseDto.setClassId(savedClass.getClassId());
    responseDto.setGrade(savedClass.getGrade());
    responseDto.setYear(savedClass.getYear());
    responseDto.setSection(savedClass.getSection());
    return responseDto;
  }


  @Transactional
  public List<TeacherResponseDTO> getTeachersInClass(Long classId) {
    Optional<Classes> findClass = classRepository.findById(classId);

    Hibernate.initialize(findClass.map(Classes::getUsers)); // 명시적으로 초기화

    List<Users> userList = findClass.map(Classes::getUsers) // Optional<Classes>에서 getUsers() 호출
        .orElseThrow(() -> new NoSuchElementException("Class not found with id: " + classId));

    logger.info("userList.size() : {}", userList.size());

    // Role이 ROLE_TEACHER인 사용자만 필터링
    List<Users> teacherList = userList.stream()
        .filter(user -> Role.ROLE_TEACHER.equals(user.getRole())) // Role이 ROLE_TEACHER인 사용자 필터링
        .toList();

    return teacherList.stream()
        .map(teacherMapper::mapToResponseDTO)
        .collect(Collectors.toList());

  }

  @Transactional
  public List<StudentResponseDTO> getStudentsInClass(Long classId) {
    Optional<Classes> findClass = classRepository.findById(classId);
    Hibernate.initialize(findClass.map(Classes::getUsers)); // 명시적으로 초기화

    List<Users> userList = findClass.map(Classes::getUsers) // Optional<Classes>에서 getUsers() 호출
        .orElseThrow(() -> new NoSuchElementException("Class not found with id: " + classId));
    logger.info("userList.size() : {}", userList.size());

    // Role이 ROLE_TEACHER인 사용자만 필터링
    List<Users> studentList = userList.stream()
        .filter(user -> Role.ROLE_STUDENT.equals(user.getRole())) // Role이 ROLE_TEACHER인 사용자 필터링
        .toList();

    return studentList.stream()
        .map(studentMapper::mapToResponseDTO)
        .collect(Collectors.toList());

  }
}
