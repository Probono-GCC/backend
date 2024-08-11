package probono.gcc.school.model.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

  private Long createdChargeId;

  private Long updatedChargeId;

  private int views;

  private List<ImageResponseDTO> imageList;
}
