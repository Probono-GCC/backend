package probono.gcc.school.model.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import probono.gcc.school.model.dto.users.UserResponse;
import probono.gcc.school.model.entity.Notice;
import probono.gcc.school.model.enums.Status;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class ImageResponseDTO {

  private Long imageId;
  @Column(length = 2048, nullable = false)
  private String imagePath;
  private long createdChargeId;
  private String username; //해당 프로필 이미지의 주인

  // imageId, imagePath, createdChargeId만을 사용하는 생성자
  public ImageResponseDTO(Long imageId, String imagePath, long createdChargeId) {
    this.imageId = imageId;
    this.imagePath = imagePath;
    this.createdChargeId = createdChargeId;
    this.username = null; // 기본값 또는 null로 설정
  }
}
