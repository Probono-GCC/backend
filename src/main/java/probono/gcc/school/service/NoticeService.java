package probono.gcc.school.service;

import jakarta.persistence.EntityManager;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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
import org.springframework.security.core.context.SecurityContextHolder;
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
import probono.gcc.school.repository.ImageRepository;
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

  private final EntityManager entityManager;

  private final ImageRepository imageRepository;


  @Transactional
  public NoticeResponse create(CreateNoticeRequest request) {

    Notice notice = new Notice();
    notice.setTitle(request.getTitle());
    notice.setContent(request.getContent());
    notice.setType(request.getType());
    notice.setCreatedChargeId(SecurityContextHolder.getContext().getAuthentication().getName());

    // 저장할 이미지가 존재하는 경우 S3에 저장 후 notice와 연결
    if (request.getImageList() != null || !request.getImageList().isEmpty()) {
      List<String> imageUrls = new ArrayList<>();
      for (MultipartFile imageFile : request.getImageList()) {
        String url = s3ImageService.upload(imageFile);
        imageUrls.add(url);
      }

      List<Image> images = new ArrayList<>();
      System.out.println(imageUrls.isEmpty());
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

//  @Transactional
//  public NoticeResponse getNotice(Long id) {
//    Notice findNotice = this.getNoticeById(id);
//    List<Image> imageList = findNotice.getImageList();
//
//    /**
//     * view증가 로직
//     */
//    findNotice.setViews(findNotice.getViews() + 1);
//    noticeRepository.save(findNotice);
//
//    return modelMapper.map(findNotice, NoticeResponse.class);
//  }

  @Transactional
  public NoticeResponse getNotice(Long id) {
    Notice findNotice = this.getNoticeById(id);

    // ACTIVE인 이미지들만 보여주기
    List<Image> imageList = findNotice.getImageList().stream()
        .filter(image -> image.getStatus().equals(Status.ACTIVE)).collect(
            Collectors.toList());
    findNotice.setImageList(imageList);

    // view 증가 로직
    noticeRepository.incrementViews(id);

    // findNotice 객체를 새로 고침하여 변경된 views 값을 반영
    entityManager.refresh(findNotice);

//    return modelMapper.map(findNotice, NoticeResponse.class);
    return mapToResponseDto(findNotice);
  }

  @Transactional
  public NoticeResponse updateNotice(Long id, UpdateNoticeRequest request) {
    Notice existingNotice = this.getNoticeById(id);
    existingNotice.setTitle(request.getTitle());
    existingNotice.setContent(request.getContent());
    existingNotice.setUpdatedChargeId(
        SecurityContextHolder.getContext().getAuthentication().getName());

    // 입력 데이터 null 예외처리
    List<Long> preservedImageIdList = Optional.ofNullable(request.getMaintainImageList())
        .orElse(Collections.emptyList());
    List<MultipartFile> requiredNewImage = Optional.ofNullable(request.getImageList())
        .orElse(Collections.emptyList()); // imageList는 빈 리스트로 요청이 오면 안됨. 아예 보내지 않아야 함.

    // 기존 이미지
    // 기존 Notice의 이미지 리스트에서 imageId만 추출하여 리스트로 저장
    List<Long> existingImageIdList = existingNotice.getImageList().stream()
        .filter(image -> image.getStatus().equals(Status.ACTIVE))
        .map(Image::getImageId) // Image 객체에서 imageId 추출
        .collect(Collectors.toList());

    List<Long> imagesToDelete = new ArrayList<>();

    // 유지할 이미지가 존재한다면
    if (!preservedImageIdList.isEmpty()) {
      // 유지할 이미지 id가 올바른지 확인
      for (Long ids : preservedImageIdList) {
        if (!imageRepository.existsByStatusAndImageId(Status.ACTIVE, ids)) {
          throw new IllegalArgumentException("유지 할 이미지의 id가 잘못되었습니다.");
        }
      }

      // preservedImageIdList에 존재하지 않는 id들을 추출
      imagesToDelete = existingImageIdList.stream()
          .filter(imageId -> !preservedImageIdList.contains(
              imageId)) // preservedImageIdList에 없는 이미지 필터링
          .collect(Collectors.toList());
    } else {
      imagesToDelete = existingImageIdList;
    }

    // 삭제할 이미지들 삭제 처리
    // 이미지 삭제 메서드 호출
    imagesToDelete.forEach(imageId -> {
      imageService.deleteImage(imageId); // imageService의 삭제 메서드 호출
    });

    // 새롭게 추가할 이미지 처리
    List<Image> newImageList = new ArrayList<>();
    List<String> newImageUrls = new ArrayList<>();
    if (!requiredNewImage.isEmpty()) { // 새롭게 추가 할 이미지가 존재 한다면
      for (MultipartFile imageFile : requiredNewImage) {
        String url = s3ImageService.upload(imageFile);
        newImageUrls.add(url);
      }

      // 새롭게 추가 할 이미지 객체들 생성
      for (String imageUrl : newImageUrls) {
        newImageList.add(imageService.saveNoticeImage(imageUrl, existingNotice));
      }
      existingNotice.getImageList().addAll(newImageList);

    }

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
    existingNotice.setUpdatedChargeId(
        SecurityContextHolder.getContext().getAuthentication().getName());

    List<Image> existingImageList = existingNotice.getImageList();

    for (Image image : existingImageList) {
      imageService.deleteImage(image.getImageId());
    }

//    LocalDateTime now = LocalDateTime.now();
//    Timestamp timestamp = Timestamp.valueOf(now);
//    existingNotice.setUpdatedAt(timestamp);
//    existingNotice.setUpdatedChargeId(SecurityContextHolder.getContext().getAuthentication().getName());
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
        Sort.by(Sort.Order.desc("createdAt")));

    if (NoticeType.CLASS.equals(type)) {
      Classes findClass = classRepository.findById(id)
          .filter(classes -> Status.ACTIVE.equals(classes.getStatus()))
          .orElseThrow(() -> new NoSuchElementException("존재하지 않는 classId 입니다."));

      //조회
      Page<Notice> findClassNoticeList = noticeRepository.findByStatusAndClassId(
          Status.ACTIVE, findClass,
          pageRequest);
//      if (findClassNoticeList.isEmpty()) {
//        throw new NoSuchElementException(
//            "Class Notice not found with classId : " + id + " or page number is too big");
//      }
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
//      if (findCourseNoticeList.isEmpty()) {
//        throw new NoSuchElementException(
//            "Course Notice not found with courseId : " + id + " or page number is too big");
//      }

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
//      if (findSchoolNoticeList.isEmpty()) {
//        throw new NoSuchElementException("School Notice not found" + " or page number is too big");
//      }
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

    if (!savedNotice.getImageList().isEmpty()) {
      List<ImageResponseDTO> collect = savedNotice.getImageList().stream()
          .filter(image -> image.getStatus().equals(Status.ACTIVE))
          .map(image -> new ImageResponseDTO(image.getImageId(), image.getImagePath(),
              image.getCreatedChargeId())).collect(Collectors.toList());
      responseDto.setImageList(collect);
    }
    return responseDto;
  }
}
