package probono.gcc.school.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
  public ResponseEntity<NoticeResponse> createNotice(
      @Valid @ModelAttribute CreateNoticeRequest request) {

    NoticeResponse createdNotice = noticeService.create(request);
    return ResponseEntity.ok(createdNotice);
  }

  @GetMapping("/notice/{id}")
  public ResponseEntity<NoticeResponse> getNotice(@PathVariable Long id) {
    NoticeResponse noticeResponse = noticeService.getNotice(id);
    return ResponseEntity.ok(noticeResponse);
  }

  @GetMapping("/notice/classNoticeList/{id}")
  public ResponseEntity<List<NoticeResponse>> getClassNoticeList(@PathVariable Long id) {
    List<NoticeResponse> noticeList = noticeService.getNoticeList(id, NoticeType.CLASS);
    return ResponseEntity.ok(noticeList);
  }

  @GetMapping("/notice/classAndCourseNoticeList/{id}")
  public ResponseEntity<List<NoticeResponse>> getClassAndCourseNoticeList(@PathVariable Long id) {
    List<NoticeResponse> noticeList = noticeService.getClassAndCourseNoticeList(id);
    return ResponseEntity.ok(noticeList);
  }

  @GetMapping("/notice/courseNoticeList/{id}")
  public ResponseEntity<List<NoticeResponse>> getCourseNoticeList(@PathVariable Long id) {
    List<NoticeResponse> noticeList = noticeService.getNoticeList(id, NoticeType.COURSE);
    return ResponseEntity.ok(noticeList);
  }

  @GetMapping("/notice/schoolNoticeList/{id}")
  public ResponseEntity<List<NoticeResponse>> getSchoolNoticeList(@PathVariable Long id) {
    List<NoticeResponse> noticeList = noticeService.getNoticeList(id, NoticeType.SCHOOL);
    return ResponseEntity.ok(noticeList);
  }

  @PutMapping("/notice/{id}")
  public ResponseEntity<NoticeResponse> updateNotice(@PathVariable Long id,
      @Valid @ModelAttribute UpdateNoticeRequest request) {
    NoticeResponse updatedNotice = noticeService.updateNotice(id, request);
    return ResponseEntity.ok(updatedNotice);
  }

  //
  @DeleteMapping("/notice/{id}")
  public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
    noticeService.deleteNotice(id);
    return ResponseEntity.noContent().build();
  }
}
