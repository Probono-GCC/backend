package probono.gcc.school.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import probono.gcc.school.model.dto.CreateNoticeRequest;
import probono.gcc.school.model.dto.NoticeResponse;
import probono.gcc.school.model.dto.UpdateNoticeRequest;
import probono.gcc.school.model.enums.NoticeType;
import probono.gcc.school.service.NoticeService;
import probono.gcc.school.service.S3ImageService;

@RestController
@RequiredArgsConstructor
public class NoticeController {

  private final NoticeService noticeService;
  private final S3ImageService s3ImageService;

  @PostMapping("/notice")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
  public ResponseEntity<NoticeResponse> createNotice(
      @Valid CreateNoticeRequest request) {

    NoticeResponse createdNotice = noticeService.create(request);
    return ResponseEntity.ok(createdNotice);
  }

  @GetMapping("/notice/{id}")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN','STUDENT')")
  public ResponseEntity<NoticeResponse> getNotice(@PathVariable Long id) {
    NoticeResponse noticeResponse = noticeService.getNotice(id);
    return ResponseEntity.ok(noticeResponse);
  }

  @GetMapping("/notice/classNoticeList")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN','STUDENT')")
  public ResponseEntity<Page<NoticeResponse>> getClassNoticeList(@RequestParam Long id,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {
    Page<NoticeResponse> noticeList = noticeService.getNoticeList(id, page, size, NoticeType.CLASS);
    return ResponseEntity.ok(noticeList);
  }

  /**
   * notice에서 course정보를 받아오기 쉽지 않다.
   */
  @GetMapping("/notice/classAndCourseNoticeList")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN','STUDENT')")
  public ResponseEntity<Page<NoticeResponse>> getClassAndCourseNoticeList(
      @RequestParam Long classId,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {
    Page<NoticeResponse> noticeList = noticeService.getClassAndCourseNoticeList(classId, page,
        size);
    return ResponseEntity.ok(noticeList);
  }

  @GetMapping("/notice/courseNoticeList")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN','STUDENT')")
  public ResponseEntity<Page<NoticeResponse>> getCourseNoticeList(@RequestParam Long id,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {
    Page<NoticeResponse> noticeList = noticeService.getNoticeList(id, page, size,
        NoticeType.COURSE);
    return ResponseEntity.ok(noticeList);
  }

  @GetMapping("/notice/schoolNoticeList")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN','STUDENT')")
  public ResponseEntity<Page<NoticeResponse>> getSchoolNoticeList(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {
    Page<NoticeResponse> noticeList = noticeService.getNoticeList(-1l, page, size,
        NoticeType.SCHOOL);
    return ResponseEntity.ok(noticeList);
  }

  @PutMapping("/notice/{id}")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
  public ResponseEntity<NoticeResponse> updateNotice(@PathVariable Long id,
      @Valid @ModelAttribute UpdateNoticeRequest request) {
    NoticeResponse updatedNotice = noticeService.updateNotice(id, request);
    return ResponseEntity.ok(updatedNotice);
  }

  //
  @DeleteMapping("/notice/{id}")
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
  public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
    noticeService.deleteNotice(id);
    return ResponseEntity.noContent().build();
  }
}
