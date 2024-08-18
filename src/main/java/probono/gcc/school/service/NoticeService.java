package probono.gcc.school.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import probono.gcc.school.model.dto.CreateNoticeRequest;
import probono.gcc.school.model.dto.course.CourseResponse;
import probono.gcc.school.model.dto.courseUser.CourseUserResponse;
import probono.gcc.school.model.dto.image.CreateImageResponseDTO;
import probono.gcc.school.model.dto.NoticeResponse;
import probono.gcc.school.model.dto.UpdateNoticeRequest;
import probono.gcc.school.model.dto.classes.ClassResponse;
import probono.gcc.school.model.dto.image.ImageResponseDTO;
import probono.gcc.school.model.entity.Classes;
import probono.gcc.school.model.entity.Course;
import probono.gcc.school.model.entity.Image;
import probono.gcc.school.model.entity.Notice;
import probono.gcc.school.model.enums.NoticeType;
import probono.gcc.school.model.enums.Status;
import probono.gcc.school.repository.ClassRepository;
import probono.gcc.school.repository.CourseRepository;
import probono.gcc.school.repository.NoticeRepository;

@Service
@RequiredArgsConstructor
public class NoticeService {

  private final ModelMapper modelMapper;
  private final NoticeRepository noticeRepository;
  private final ClassRepository classRepository;
  private final CourseRepository courseRepository;

  private final ImageService imageService;

  private final S3ImageService s3ImageService;


  @Transactional
  public NoticeResponse create(CreateNoticeRequest request) {

    Notice notice = new Notice();
    notice.setTitle(request.getTitle());
    notice.setContent(request.getContent());
    notice.setType(request.getType());
    notice.setCreatedChargeId(-1L);

    // 저장할 이미지가 존재하는 경우 S3에 저장 후 notice와 연결
    if (request.getImageList().isEmpty() || !request.getImageList().get(0).isEmpty()) {
      List<String> imageUrls = new ArrayList<>();
      for (MultipartFile imageFile : request.getImageList()) {
        String url = s3ImageService.upload(imageFile);
        imageUrls.add(url);
      }

      List<Image> images = new ArrayList<>();
      for (String imageUrl : imageUrls) {
        images.add(imageService.saveNoticeImage(imageUrl, notice));
      }
      notice.setImageList(images);
    }

    LocalDateTime now = LocalDateTime.now();
    Timestamp timestamp = Timestamp.valueOf(now);
    notice.setUpdatedAt(timestamp);

    Long classId = request.getClassId();
    Long courseId = request.getCourseId();

    // CLASS 또는 COURSE와 공지 연결
    if (request.getType().equals(NoticeType.CLASS)) {
      Optional<Classes> findClass = classRepository.findById(classId);
      if (findClass.isEmpty()) {
        throw new IllegalArgumentException("ClassId가 올바르지 않습니다.");
      }
      notice.setClassId(findClass.get());
    } else if (request.getType().equals(NoticeType.COURSE)) {
      Course findCourse = courseRepository.findById(courseId)
          .filter(course -> Status.ACTIVE.equals(course.getStatus()))
          .orElseThrow(() -> new NoSuchElementException("courseId가 존재하지 않습니다."));
      notice.setCourseId(findCourse);
    }

    Notice savedNotice = noticeRepository.save(notice);
    return mapToResponseDto(savedNotice);
  }

  @Transactional
  public NoticeResponse getNotice(Long id) {
    Notice findNotice = this.getNoticeById(id);
    List<Image> imageList = findNotice.getImageList();

//    List<ImageResponseDTO> imageResponse = imageList.stream()
//        .filter(n -> n.getStatus() == Status.ACTIVE)
//        .map(
//            m -> new ImageResponseDTO(m.getImageId(), m.getImagePath(), m.getCreatedChargeId()
//            ))
//        .collect(Collectors.toList());
//
//    NoticeResponse noticeResponse = mapToResponseDto(findNotice);
//    noticeResponse.setImageList(imageResponse);
//    noticeResponse.setViews(noticeResponse.getViews() + 1);
    /**
     * view증가 로직
     */
    findNotice.setViews(findNotice.getViews() + 1);
    noticeRepository.save(findNotice);

//    return mapToResponseDto(findNotice);
//    return noticeResponse;

    return modelMapper.map(findNotice, NoticeResponse.class);
  }

  @Transactional
  public NoticeResponse updateNotice(Long id, UpdateNoticeRequest request) {
    Notice existingNotice = this.getNoticeById(id);
    existingNotice.setTitle(request.getTitle());
    existingNotice.setContent(request.getContent());

    List<String> imageUrls = new ArrayList<>();

    for (MultipartFile imageFile : request.getImageList()) {
      String url = s3ImageService.upload(imageFile);
      imageUrls.add(url);
    }

    List<Image> imageList = new ArrayList<>();
    for (String imageUrl : imageUrls) {
      imageList.add(imageService.saveNoticeImage(imageUrl, existingNotice));
    }

//    List<Image> existingNoticeImageList = existingNotice.getImageList();
//    for (Image image : existingNoticeImageList) {
//      imageService.deleteProfileImage(image.getImageId());
//      image.setNoticeId(null); // 연관관계 제거
//    }

    // 기존 이미지 리스트를 비우고 새로운 리스트를 설정합니다.
    existingNotice.getImageList().clear();
    existingNotice.getImageList().addAll(imageList);

    LocalDateTime now = LocalDateTime.now();
    Timestamp timestamp = Timestamp.valueOf(now);
    existingNotice.setUpdatedAt(timestamp);

    Notice savedNotice = noticeRepository.save(existingNotice);
    return mapToResponseDto(savedNotice);
  }

  public Notice getNoticeById(Long id) {
    Optional<Notice> findNotice = noticeRepository.findById(id);

    if (findNotice.isEmpty() || Status.INACTIVE.equals(findNotice.get().getStatus())) {
      throw new NoSuchElementException("Notice not found with id: " + id);
    }

    return findNotice.get();  // 증가된 view를 저장
  }

  @Transactional
  public void deleteNotice(Long id) {
    Notice existingNotice = this.getNoticeById(id);
    existingNotice.setStatus(Status.INACTIVE);

    List<Image> existingImageList = existingNotice.getImageList();

    for (Image image : existingImageList) {
      imageService.deleteProfileImage(image.getImageId());
    }

//    LocalDateTime now = LocalDateTime.now();
//    Timestamp timestamp = Timestamp.valueOf(now);
//    existingNotice.setUpdatedAt(timestamp);
//    existingNotice.setUpdatedChargeId(-1L);
//
//    Notice savedNotice = noticeRepository.save(existingNotice);
  }

  //  @Transactional(readOnly = true)
//  public List<NoticeResponse> getNoticeList(long id, NoticeType type) {
//    List<Notice> findNoticeList = new ArrayList<Notice>();
//    if (NoticeType.CLASS.equals(type)) {
//      Classes findClass = classRepository.findById(id)
//          .filter(classes -> Status.ACTIVE.equals(classes.getStatus()))
//          .orElseThrow(() -> new NoSuchElementException("존재하지 않는 classId 입니다."));
//      findNoticeList = noticeRepository.findByClassId(findClass);
//    } else if (NoticeType.COURSE.equals(type)) {
//      Course findCourse = courseRepository.findById(id)
//          .filter(course -> Status.ACTIVE.equals(course.getStatus()))
//          .orElseThrow(() -> new NoSuchElementException("존재하지 않는 courseId 입니다."));
//      findNoticeList = noticeRepository.findByCourseId(findCourse);
//    } else {
//      findNoticeList = noticeRepository.findByClassIdIsNullAndCourseIdIsNull();
//    }
//
//    /**
//     * dto로 변환과정 추가
//     */
//    List<NoticeResponse> noticeList = findNoticeList.stream()
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
//
//    return noticeList;
//  }
  @Transactional(readOnly = true)
  public Page<NoticeResponse> getNoticeList(long id, int page, int size, NoticeType type) {

    //첫 페이지, 가져올 갯수, 정렬기준, 정렬 필드 설정
    PageRequest pageRequest = PageRequest.of(page, size,
        Sort.by(Sort.Order.asc("createdAt")));

    if (NoticeType.CLASS.equals(type)) {
      Classes findClass = classRepository.findById(id)
          .filter(classes -> Status.ACTIVE.equals(classes.getStatus()))
          .orElseThrow(() -> new NoSuchElementException("존재하지 않는 classId 입니다."));

      //조회
      Page<Notice> findClassNoticeList = noticeRepository.findByStatusAndClassId(
          Status.ACTIVE, findClass,
          pageRequest);
      if (findClassNoticeList.isEmpty()) {
        throw new NoSuchElementException(
            "Class Notice not found with classId : " + id + " or page number is too big");
      }
      //DTO변환
      Page<NoticeResponse> noticeResponse = findClassNoticeList.map(
          notice -> new NoticeResponse(
              notice.getNoticeId(),
              notice.getTitle(),
              notice.getContent(),
              notice.getCreatedAt(),
              notice.getUpdatedAt(),
              notice.getCreatedChargeId(),
              notice.getUpdatedChargeId(),
              notice.getViews()
          ));
      return noticeResponse;
    } else if (NoticeType.COURSE.equals(type)) {
      Course findCourse = courseRepository.findById(id)
          .filter(course -> Status.ACTIVE.equals(course.getStatus()))
          .orElseThrow(() -> new NoSuchElementException("존재하지 않는 courseId 입니다."));
      //조회
      Page<Notice> findCourseNoticeList = noticeRepository.findByStatusAndCourseId(
          Status.ACTIVE, findCourse,
          pageRequest);
      if (findCourseNoticeList.isEmpty()) {
        throw new NoSuchElementException(
            "Course Notice not found with courseId : " + id + " or page number is too big");
      }

      //DTO변환
      Page<NoticeResponse> noticeResponse = findCourseNoticeList.map(
          notice -> new NoticeResponse(
              notice.getNoticeId(),
              notice.getTitle(),
              notice.getContent(),
              notice.getCreatedAt(),
              notice.getUpdatedAt(),
              notice.getCreatedChargeId(),
              notice.getUpdatedChargeId(),
              notice.getViews()
          ));
      return noticeResponse;
    } else {
      Page<Notice> findSchoolNoticeList = noticeRepository.findByTypeAndStatus(type,
          Status.ACTIVE,
          pageRequest);
      if (findSchoolNoticeList.isEmpty()) {
        throw new NoSuchElementException("School Notice not found" + " or page number is too big");
      }
      //DTO변환
      Page<NoticeResponse> noticeResponse = findSchoolNoticeList.map(
          notice -> new NoticeResponse(
              notice.getNoticeId(),
              notice.getTitle(),
              notice.getContent(),
              notice.getCreatedAt(),
              notice.getUpdatedAt(),
              notice.getCreatedChargeId(),
              notice.getUpdatedChargeId(),
              notice.getViews()
          ));
      return noticeResponse;
    }

  }

  @Transactional(readOnly = true)
  public Page<NoticeResponse> getClassAndCourseNoticeList(long classId, int page, int size) {
    Classes findClass = classRepository.findById(classId)
        .filter(classes -> Status.ACTIVE.equals(classes.getStatus()))
        .orElseThrow(() -> new NoSuchElementException("class not found with id " + classId));

    List<Course> classCourseList = courseRepository.findByClassId(findClass);

    List<Notice> classNoticeList = noticeRepository.findByStatusAndClassId(Status.ACTIVE,
        findClass);

    for (Course course : classCourseList) {
      List<Notice> courseNoticeList = noticeRepository.findByStatusAndCourseId(Status.ACTIVE,
          course);
      classNoticeList.addAll(courseNoticeList);
    }

    classNoticeList.sort(Comparator.comparing(Notice::getCreatedAt).reversed());

    List<NoticeResponse> collect = classNoticeList.stream()
        .map(notice -> modelMapper.map(notice, NoticeResponse.class))
        .collect(Collectors.toList());

    PageRequest pageRequest = PageRequest.of(page, size);
    int start = (int) pageRequest.getOffset();
    int end = Math.min((start + pageRequest.getPageSize()), collect.size());
    Page<NoticeResponse> response = new PageImpl<>(collect.subList(start, end), pageRequest,
        collect.size());
    return response;
  }

  private NoticeResponse mapToResponseDto(Notice savedNotice) {
    NoticeResponse responseDto = new NoticeResponse();
    responseDto.setNoticeId(savedNotice.getNoticeId());
    responseDto.setTitle(savedNotice.getTitle());
    responseDto.setContent(savedNotice.getContent());
    responseDto.setViews(savedNotice.getViews());
    responseDto.setCreatedAt(savedNotice.getCreatedAt());
    responseDto.setCreatedChargeId(savedNotice.getCreatedChargeId());
    responseDto.setUpdatedAt(savedNotice.getUpdatedAt());
    responseDto.setUpdatedChargeId(savedNotice.getUpdatedChargeId());

    if (savedNotice.getImageList() != null) {
      List<ImageResponseDTO> collect = savedNotice.getImageList().stream()
          .map(image -> new ImageResponseDTO(image.getImageId(), image.getImagePath(),
              image.getCreatedChargeId())).collect(Collectors.toList());
      responseDto.setImageList(collect);
    }
    return responseDto;
  }
}
