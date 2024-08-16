package probono.gcc.school.model.dto;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import probono.gcc.school.model.entity.Notice;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ImageRequestDTO {

  private Long noticeId;
  private String imagePath;
  private String username;

}
