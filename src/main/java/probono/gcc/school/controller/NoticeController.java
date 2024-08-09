package probono.gcc.school.controller;

import jakarta.validation.Valid;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import probono.gcc.school.model.dto.ClassResponse;
import probono.gcc.school.model.dto.CreateClassRequest;
import probono.gcc.school.model.dto.CreateNoticeRequest;
import probono.gcc.school.model.dto.NoticeResponse;
import probono.gcc.school.model.entity.Notice;
import probono.gcc.school.service.NoticeService;

@RestController
@RequiredArgsConstructor
public class NoticeController {

  private final NoticeService noticeService;

  @PostMapping("/notice")
  public ResponseEntity<NoticeResponse> createStudent(
      @RequestBody @Valid CreateNoticeRequest request) {
    Notice notice = new Notice();
    notice.setTitle(request.getTitle());
    notice.setContent(request.getContent());
    notice.setType(request.getType());
    notice.setCreatedChargeId(-1L);

    LocalDateTime now = LocalDateTime.now();
    Timestamp timestamp = Timestamp.valueOf(now);
    notice.setUpdatedAt(timestamp);

    Long classId = request.getClassId();
    Long courseId = request.getCourseId();

    NoticeResponse createdNotice = noticeService.create(notice, classId, courseId);
    return ResponseEntity.ok(createdNotice);
  }

  @GetMapping("/notice/{id}")
  public ResponseEntity<NoticeResponse> getNotice(@PathVariable Long id) {
    NoticeResponse noticeResponse = noticeService.getNotice(id);
    return ResponseEntity.ok(noticeResponse);
  }

//  @GetMapping("/classNoticeList/{id}")
//  public ResponseEntity<List<NoticeResponse>> getClassNoticeList(@PathVariable Long id) {
//    List<NoticeResponse> noticeList = noticeService.getNoticeList(id);
//    return ResponseEntity.ok(noticeList);
//  }

  @PutMapping("/notice/{id}")
  public ResponseEntity<NoticeResponse> updateNotice(@PathVariable Long id,
      @RequestBody @Valid CreateNoticeRequest request) {
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
