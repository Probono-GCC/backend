package probono.gcc.school.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;

import org.springframework.context.annotation.Lazy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.expression.spel.ast.Assign;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import probono.gcc.school.exception.DuplicateEntityException;
import probono.gcc.school.model.dto.classes.AssignClassResponseDTO;
import probono.gcc.school.model.dto.classes.ClassResponse;
import probono.gcc.school.model.dto.classes.CreateClassRequest;
import probono.gcc.school.model.dto.image.CreateImageResponseDTO;
import probono.gcc.school.model.dto.NoticeResponse;
import probono.gcc.school.model.dto.users.StudentResponseDTO;
import probono.gcc.school.model.dto.users.TeacherResponseDTO;
import probono.gcc.school.model.entity.Classes;
import probono.gcc.school.model.entity.Course;
import probono.gcc.school.model.entity.Notice;
import probono.gcc.school.model.entity.Users;
import probono.gcc.school.model.enums.Role;
import probono.gcc.school.model.enums.Status;
import probono.gcc.school.repository.ClassRepository;
import probono.gcc.school.repository.CourseRepository;
import probono.gcc.school.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ClassService {

  private final ModelMapper modelMapper;
  private final ClassRepository classRepository;

  private final CourseService courseService;

  private final CourseRepository courseRepository;

  private final UserRepository userRepository;

  //@Lazy
 // private final TeacherService teacherService;
  //@Lazy
  //private final StudentService studentService;

  /**
   * 클래스 생성
   */
  @Transactional
  public ClassResponse create(Classes requestClass) {

    validateDuplicateClass(requestClass); //중복 클래스 검증
    Classes savedClass = classRepository.save(requestClass);
    return modelMapper.map(savedClass, ClassResponse.class);
    //return mapToResponseDto(savedClass);
  }

  private void validateDuplicateClass(Classes requestClass) {
    if (classRepository.existsByYearAndGradeAndSection(requestClass.getYear(),
        requestClass.getGrade(), requestClass.getSection())) {
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
    existingClass.setUpdatedChargeId(-1L);

    Classes savedClass = classRepository.save(existingClass);
    return mapToResponseDto(savedClass);
  }

  @Transactional
  public void deleteClass(Long id) {
    Classes existingClass = this.getClassById(id);
    existingClass.setStatus(Status.INACTIVE);
    existingClass.setUpdatedChargeId(-1L);

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




  private ClassResponse mapToResponseDto(Classes savedClass) {
    ClassResponse responseDto = new ClassResponse();
    responseDto.setClassId(savedClass.getClassId());
    responseDto.setGrade(savedClass.getGrade());
    responseDto.setYear(savedClass.getYear());
    responseDto.setSection(savedClass.getSection());
    return responseDto;
  }




}
