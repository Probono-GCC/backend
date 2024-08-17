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
public class CreateImageResponseDTO {

  private Long imageId;
  @Column(length = 2048, nullable = false)
  private String imagePath;
  private long createdChargeId;
  private String username; //해당 프로필 이미지의 주인

  // imageId, imagePath, createdChargeId만을 사용하는 생성자
  public CreateImageResponseDTO(Long imageId, String imagePath, long createdChargeId) {
    this.imageId = imageId;
    this.imagePath = imagePath;
    this.createdChargeId = createdChargeId;
    this.username = null; // 기본값 또는 null로 설정
  }
}
