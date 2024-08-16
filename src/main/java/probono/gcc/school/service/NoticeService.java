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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import probono.gcc.school.model.dto.CreateNoticeRequest;
import probono.gcc.school.model.dto.ImageResponseDTO;
import probono.gcc.school.model.dto.NoticeResponse;
import probono.gcc.school.model.dto.UpdateNoticeRequest;
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

    List<String> imageUrls = new ArrayList<>();

    for (MultipartFile imageFile : request.getImageList()) {
      String url = s3ImageService.upload(imageFile);
      imageUrls.add(url);
    }

    List<Image> imageList = new ArrayList<>();
    for (String imageUrl : imageUrls) {
      imageList.add(imageService.saveNoticeImage(imageUrl, notice));
    }

    notice.setImageList(imageList);

    LocalDateTime now = LocalDateTime.now();
    Timestamp timestamp = Timestamp.valueOf(now);
    notice.setUpdatedAt(timestamp);

    Long classId = request.getClassId();
    Long courseId = request.getCourseId();

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

  @Transactional(readOnly = true)
  public List<NoticeResponse> getNoticeList(long id, NoticeType type) {
    List<Notice> findNoticeList = new ArrayList<Notice>();
    if (NoticeType.CLASS.equals(type)) {
      Classes findClass = classRepository.findById(id)
          .filter(classes -> Status.ACTIVE.equals(classes.getStatus()))
          .orElseThrow(() -> new NoSuchElementException("존재하지 않는 classId 입니다."));
      findNoticeList = noticeRepository.findByClassId(findClass);
    } else if (NoticeType.COURSE.equals(type)) {
      Course findCourse = courseRepository.findById(id)
          .filter(course -> Status.ACTIVE.equals(course.getStatus()))
          .orElseThrow(() -> new NoSuchElementException("존재하지 않는 courseId 입니다."));
      findNoticeList = noticeRepository.findByCourseId(findCourse);
    } else {
      findNoticeList = noticeRepository.findByClassIdIsNullAndCourseIdIsNull();
    }

    /**
     * dto로 변환과정 추가
     */
    List<NoticeResponse> noticeList = findNoticeList.stream()
        .filter(n -> n.getStatus() == Status.ACTIVE)
        .map(
            m -> new NoticeResponse(m.getNoticeId(), m.getTitle(), m.getContent(), m.getCreatedAt(),
                m.getUpdatedAt(), m.getCreatedChargeId(), m.getUpdatedChargeId(), m.getViews(),
                m.getImageList().stream()
                    .map(image -> modelMapper.map(image, ImageResponseDTO.class))
                    .collect(Collectors.toList())
            ))
        .collect(
            Collectors.toList());

    return noticeList;
  }

  @Transactional(readOnly = true)
  public List<NoticeResponse> getClassAndCourseNoticeList(long id) {
    List<Notice> findNoticeList = new ArrayList<Notice>();

    Classes findClass = classRepository.findById(id)
        .filter(classes -> Status.ACTIVE.equals(classes.getStatus()))
        .orElseThrow(() -> new NoSuchElementException("class not foudn with id " + id));

    List<Course> classCourseList = courseRepository.findByClassId(findClass);

    List<Notice> classNoticeList = noticeRepository.findByClassId(findClass);

    findNoticeList.addAll(classNoticeList);

    for (Course classCourse : classCourseList) {
      List<Notice> courseNoticeList = noticeRepository.findByCourseId(classCourse);
      findNoticeList.addAll(courseNoticeList);
    }

    /**
     * dto로 변환과정 추가
     */
    List<NoticeResponse> noticeList = findNoticeList.stream()
        .filter(n -> n.getStatus() == Status.ACTIVE)
        .sorted(Comparator.comparing(Notice::getCreatedAt).reversed())
        .map(
            m -> new NoticeResponse(m.getNoticeId(), m.getTitle(), m.getContent(), m.getCreatedAt(),
                m.getUpdatedAt(), m.getCreatedChargeId(), m.getUpdatedChargeId(), m.getViews(),
                m.getImageList().stream()
                    .map(image -> modelMapper.map(image, ImageResponseDTO.class))
                    .collect(Collectors.toList())
            ))
        .collect(
            Collectors.toList());

    return noticeList;
  }

//  public List<NoticeResponse> getNoticeList(Long id) {
//    List<Notice> findNotice = noticeRepository.findByClassId(id);
//
//    if (findNotice.isEmpty()) {
//      throw new NoSuchElementException("Class Notice not found with");
//    }
//
//    List<NoticeResponse> collect = findNotice.stream()
//        .map(
//            m -> new NoticeResponse(m.getNoticeId(), m.getTitle(), m.getContent(), m.getCreatedAt(),
//                m.getUpdatedAt(), m.getCreatedChargeId(), m.getUpdatedChargeId(), m.getViews()))
//        .collect(
//            Collectors.toList());
//    return collect;
//  }

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

    List<ImageResponseDTO> collect = savedNotice.getImageList().stream()
        .map(image -> new ImageResponseDTO(image.getImageId(), image.getImagePath(),
            image.getCreatedChargeId())).collect(Collectors.toList());
    responseDto.setImageList(collect);
    return responseDto;
  }
}
