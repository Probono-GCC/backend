package probono.gcc.school.model.dto;

import java.sql.Timestamp;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import probono.gcc.school.model.dto.image.CreateImageResponseDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponse {

  private Long noticeId;

  private String title;

  private String content;

  private Timestamp createdAt;

  private Timestamp updatedAt;

  private Long createdChargeId;

  private Long updatedChargeId;

  private int views;

  //private CourseResponse courseResponse;

  private List<CreateImageResponseDTO> imageList;
}
