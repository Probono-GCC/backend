package probono.gcc.school.model.dto.image;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class ImageResponseDTO {

  private Long imageId;
  @Column(length = 2048, nullable = false)
  private String imagePath;
  private String createdChargeId;
}
