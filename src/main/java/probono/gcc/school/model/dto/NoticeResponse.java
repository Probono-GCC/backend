package probono.gcc.school.model.dto;

import java.sql.Timestamp;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import probono.gcc.school.model.dto.image.CreateImageResponseDTO;

import lombok.RequiredArgsConstructor;
import probono.gcc.school.model.dto.course.CourseResponse;
import probono.gcc.school.model.dto.image.ImageResponseDTO;
import probono.gcc.school.model.entity.Classes;
import probono.gcc.school.model.entity.Course;
import probono.gcc.school.model.entity.Users;
import probono.gcc.school.model.enums.NoticeType;
import probono.gcc.school.model.enums.Status;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponse {

  private Long noticeId;

  private String title;

  private String content;

  private Timestamp createdAt;

  private Timestamp updatedAt;

  private long createdChargeId;

  private long updatedChargeId;

  private int views;

  //private CourseResponse courseResponse;

//  private List<CreateImageResponseDTO> imageList;

  private List<ImageResponseDTO> imageList;

  public NoticeResponse(long noticeId, String title, String content, Timestamp createdAt,
      Timestamp updatedAt, long createdChargeId, long updatedChargeId, int views) {
    this.noticeId = noticeId;
    this.title = title;
    this.content = content;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.createdChargeId = createdChargeId;
    this.updatedChargeId = updatedChargeId;
    this.views = views;
  }

}
