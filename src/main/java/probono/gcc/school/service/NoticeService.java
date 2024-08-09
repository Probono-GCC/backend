package probono.gcc.school.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import probono.gcc.school.model.dto.ClassResponse;
import probono.gcc.school.model.dto.CreateClassRequest;
import probono.gcc.school.model.dto.CreateNoticeRequest;
import probono.gcc.school.model.dto.NoticeResponse;
import probono.gcc.school.model.entity.Classes;
import probono.gcc.school.model.entity.Notice;
import probono.gcc.school.model.enums.NoticeType;
import probono.gcc.school.model.enums.Status;
import probono.gcc.school.repository.ClassRepository;
import probono.gcc.school.repository.NoticeRepository;

@Service
@RequiredArgsConstructor
public class NoticeService {

  private final NoticeRepository noticeRepository;
  private final ClassRepository classRepository;

  @Transactional
  public NoticeResponse create(Notice requestNotice, Long classId, Long courseId) {

    if (requestNotice.getType().equals(NoticeType.CLASS)) {
      Optional<Classes> findClass = classRepository.findById(classId);
      requestNotice.setClassId(findClass.get());
    } else if (requestNotice.getType().equals(NoticeType.COURSE)) {
      /**
       * Course완성 이후 추가 로직 필요
       */
    }

    Notice savedNotice = noticeRepository.save(requestNotice);
    return mapToResponseDto(savedNotice);
  }

  public NoticeResponse getNotice(Long id) {
    Notice noticeEntity = this.getNoticeById(id);
    /**
     * view증가 로직
     */
    noticeEntity.setViews(noticeEntity.getViews() + 1);
    noticeRepository.save(noticeEntity);
    return mapToResponseDto(noticeEntity);
  }

  @Transactional
  public NoticeResponse updateNotice(Long id, CreateNoticeRequest request) {
    Notice existingNotice = this.getNoticeById(id);
    existingNotice.setTitle(request.getTitle());
    existingNotice.setContent(request.getContent());

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

    LocalDateTime now = LocalDateTime.now();
    Timestamp timestamp = Timestamp.valueOf(now);
    existingNotice.setUpdatedAt(timestamp);
    existingNotice.setUpdatedAt(timestamp);
    existingNotice.setUpdatedChargeId(-1L);

    Notice savedNotice = noticeRepository.save(existingNotice);
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
    return responseDto;
  }
}
